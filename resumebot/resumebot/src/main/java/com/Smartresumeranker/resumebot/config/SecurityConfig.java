package com.Smartresumeranker.resumebot.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
//This is the security gaurd of backend
  @Value("${jwt.secret}")
  private String jwtSecret;
  //store the application.properties jwt.secret in a variable

  private final com.Smartresumeranker.resumebot.repository.UserAccountRepository userRepo;

  public SecurityConfig(com.Smartresumeranker.resumebot.repository.UserAccountRepository userRepo) {
    this.userRepo = userRepo;
    //store the userRepo in a variable
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    //generate a random password hash to store it in the database
    //password look like a 3$2a$10$...
    return new BCryptPasswordEncoder();
  }

  @SuppressWarnings("removal")
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //configure the security filter chain
    //This code runs AFTER the user is authenticated
    AuthenticationSuccessHandler successHandler = (request, response, authentication) -> {
      OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;
      //get the email from the OAuth2User principal
      OAuth2User principal = (OAuth2User) oauth.getPrincipal();
      String email = resolveEmail(principal, oauth.getAuthorizedClientRegistrationId());
      //if the email is null, return a bad request error
      if (email == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
      //normalize the email to lowercase example: "Test@example.com" -> "test@example.com"
      String normalized = email.toLowerCase();
      com.Smartresumeranker.resumebot.model.UserAccount ua = userRepo.findByEmail(normalized).orElseGet(() -> {
        //if the user is not found, create a new user account
        com.Smartresumeranker.resumebot.model.UserAccount u = new com.Smartresumeranker.resumebot.model.UserAccount();
        //set the email to the normalized email
        u.setEmail(normalized);
        //set the password hash to a random UUID string example: "1234567890"
        u.setPasswordHash(passwordEncoder().encode(java.util.UUID.randomUUID().toString()));
        //save the user account to the database
        return userRepo.save(u);
      });
      //issue a JWT token for the user account
      String token = issueToken(ua);
      //get the origin from the request header, default to "http://localhost:5173"
      String origin = Optional.ofNullable(request.getHeader("Origin")).orElse("http://localhost:5173");
      //redirect the user to the callback URL with the token as a query parameter
      String redirect = origin + "/oauth2/callback?token=" + URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
      response.sendRedirect(redirect);
    };

    //configure the HTTP security
    http
        //disable CSRF protection CSRF full form Cross-Site Request Forgery
        .csrf(csrf -> csrf.disable())
        //enable CORS with the custom CORS configuration source
        //cors full form Cross-Origin Resource Sharing
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        //set the session creation policy to IF_REQUIRED
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        //authorize HTTP requests
        .authorizeHttpRequests(auth -> auth
            //permit all requests to the /auth/**, /oauth2/**, /login/oauth2/**, and /h2-console/** endpoints
            .requestMatchers("/auth/**",
            "/oauth2/**",
            "/login/oauth2/**",
             "/h2-console/**").permitAll()
             //require authentication for all other requests
            .anyRequest().authenticated())
            //use the default form login page
            .formLogin(withDefaults())
            //disable frame options to allow the H2 console to be embedded in an iframe
        .headers(h -> h.frameOptions(f -> f.disable()))
        //configure OAuth2 login with the custom success handler
        .oauth2Login(o -> o.successHandler(successHandler))
        //configure the OAuth2 resource server with JWT
        .oauth2ResourceServer(ors -> ors.jwt());
        //build the HTTP security configuration
    return http.build();

  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    //get the authentication manager from the configuration
    //authentication manager is responsible for authenticating users
    return cfg.getAuthenticationManager();
  }

  private CorsConfigurationSource corsConfigurationSource() {
    //create a new CORS configuration
    CorsConfiguration config = new CorsConfiguration();
    //add allowed origins patterns to the configuration
    //"*" means allow all origins
    config.addAllowedOriginPattern("*");
    //add allowed headers to the configuration
    //"*" means allow all headers
    config.addAllowedHeader("*");
    //add allowed methods to the configuration
    //"*" means allow all methods
    config.addAllowedMethod("*");
    //create a new URL-based CORS configuration source
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //register the CORS configuration for all endpoints
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  //create a new JWT encoder with the secret key
  @Bean
  @SuppressWarnings("Convert2Diamond")
  public JwtEncoder jwtEncoder() {
    //create a new secret key from the JWT secret
    //HmacSHA256 is the algorithm used to sign the JWT example: "1234567890"
    SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    //create a new Nimbus JWT encoder with the secret key
    return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(key));
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    //create a new secret key from the JWT secret
    //HmacSHA256 is the algorithm used to sign the JWT example: "1234567890"
    SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    //create a new Nimbus JWT decoder with the secret key
    return NimbusJwtDecoder.withSecretKey(key).build();
  }

  //resolve the email from the OAuth2 user principal based on the registration ID
  //if the email is not found, try to resolve it from the GitHub login attribute
  private String resolveEmail(OAuth2User principal, String registrationId) {
    //try to resolve the email from the "email" attribute of the OAuth2 user principal
    Object email = principal.getAttributes().get("email");
    if (email == null && "github".equalsIgnoreCase(registrationId)) {
      //if the email is not found and the registration ID is "github", try to resolve it from the "login" attribute
      email = principal.getAttributes().get("email");
      //if the email is still not found, try to resolve it from the "login" attribute
      if (email == null) {
        //if the email is still not found, try to resolve it from the "login" attribute
        //GitHub login attribute is used to form the email address example: "johndoe"
        Object login = principal.getAttributes().get("login");
        //if the login attribute is not null, append "@users.noreply.github.com" to it to form the email address
        if (login != null) {
          //if the login attribute is not null, append "@users.noreply.github.com" to it to form the email address
          email = String.valueOf(login) + "@users.noreply.github.com";
        }
      }
    }
    //return the email address if it is not null, otherwise return null
    return email == null ? null : String.valueOf(email);
  }

  //issue a JWT token for the given user account
  private String issueToken(com.Smartresumeranker.resumebot.model.UserAccount ua) {
    //create a new JWT claims set builder
    org.springframework.security.oauth2.jwt.JwtClaimsSet claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
        //set the subject of the JWT to the email address of the user account
        .subject(ua.getEmail())
        //set the issued at time of the JWT to the current time
        .issuedAt(Instant.now())
        //set the expiration time of the JWT to 8 hours from the current time
        .expiresAt(Instant.now().plusSeconds(60L * 60L * 8L))
        //add the roles of the user account to the JWT claims as a list of strings
        .claim("roles", ua.getRoles())
        .build();
        //create a new JWT header with the HMAC SHA-256 algorithm
    org.springframework.security.oauth2.jwt.JwsHeader header = org.springframework.security.oauth2.jwt.JwsHeader.with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256).build();
    //encode the JWT claims set with the header and the secret key to form the JWT token
    return jwtEncoder().encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(header, claims)).getTokenValue();
  }
}