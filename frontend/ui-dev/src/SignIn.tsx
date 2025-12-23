import './index.css'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
const backendBase = (import.meta as any).env?.VITE_BACKEND_URL || 'http://localhost:8080'

function SignIn() {
  const navigate = useNavigate()

  return (
    <div className="page">
      <div className="container">
        <button className="muted" style={{background:'transparent', border:'none', cursor:'pointer', marginBottom:12}} onClick={()=>navigate('/')}>← Back to Home</button>

        <div className="auth-grid">
          <div>
            <div className="card" style={{padding:0}}>
              <img src="https://i.ibb.co/1GLDVkPJ/pexels-mikhail-nilov-7731363.jpg" alt="pexer-image2" className="hero-image" style={{height:260}} />
            </div>
            <div className="card" style={{marginTop:12}}>
              <p style={{fontWeight:600}}>"This tool helped me land my dream job!"</p>
              <p className="muted">The AI suggestions were spot-on. I improved my resume match score from 65% to 92% and got 3 interview calls within a week.</p>
              <div style={{display:'flex', alignItems:'center', gap:10, marginTop:12}}>
                <div style={{width:36, height:36, borderRadius:'999px', background:'#2563eb'}}></div>
                <div>
                  <div style={{fontWeight:600}}>Sarah Johnson</div>
                  <div className="muted" style={{fontSize:12}}>Software Engineer at Google</div>
                </div>
              </div>
            </div>
          </div>

          <div className="card">
            <h2 style={{marginTop:0}}>Welcome back</h2>
            <p className="muted">Sign in to your account to continue analyzing resumes</p>

            <label className="label">Email</label>
            <input id="signin-email" className="input" type="email" placeholder="your.email@example.com" required/>

            <div className="row" style={{marginTop:12}}>
              <label className="label" style={{margin:0}}>Password</label>
              <button className="muted" style={{background:'transparent', border:'none', cursor:'pointer'}}>Forgot password?</button>
            </div>
            <input id="signin-password" className="input" type="password" placeholder="Enter your password" required/>

            <label className="checkbox" style={{marginTop:10}}>
              <input type="checkbox" style={{marginRight:8}} /> Remember me for 30 days
            </label>

            <button className="btn-primary" style={{width:'100%', marginTop:14}} onClick={async()=>{
              const email = (document.getElementById('signin-email') as HTMLInputElement)?.value || ''
              const password = (document.getElementById('signin-password') as HTMLInputElement)?.value || ''
              try {
                const { data } = await axios.post('/auth/signin', { email, password })
                if (data?.token) {
                  localStorage.setItem('token', data.token)
                  navigate('/analyze')
                }
              } catch {
                alert('Invalid credentials')
              }
            }}>Sign In</button>

            <div className="divider"><span>Or continue with</span></div>
            <div className="social-row">
              <button className="social-btn" onClick={()=>{ window.location.href = `${backendBase}/oauth2/authorization/google` }}>⚪ Google</button>
              <button className="social-btn" onClick={()=>{ window.location.href = `${backendBase}/oauth2/authorization/github` }}>⚪ GitHub</button>
            </div>

            <div style={{textAlign:'center', marginTop:14}}>
              <span className="muted">Don't have an account?</span> <button className="muted" style={{background:'transparent', border:'none', color:'var(--blue)', cursor:'pointer'}} onClick={()=>navigate('/signup')}>Sign up for free</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SignIn