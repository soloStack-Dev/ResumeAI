import './index.css'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
const backendBase = (import.meta as any).env?.VITE_BACKEND_URL || 'http://localhost:8080'

function SignUp() {
  const navigate = useNavigate()

  const BrandIcon = () => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="4" y="3" width="16" height="18" rx="3" stroke="#2563eb" strokeWidth="1.5"/>
      <path d="M8 8h8M8 12h8M8 16h6" stroke="#2563eb" strokeWidth="1.5"/>
    </svg>
  )

  const Check = () => (
    <span className="check-dot">✓</span>
  )

  return (
    <div className="page">
      <div className="container">
        <button className="muted" style={{background:'transparent', border:'none', cursor:'pointer', marginBottom:12}} onClick={()=>navigate('/')}>← Back to Home</button>

        <div style={{display:'flex', alignItems:'center', gap:10, marginBottom:12}}>
          <div className="brand-icon"><BrandIcon/></div>
          <strong>Smart Resume Ranker</strong>
        </div>

        <div className="auth-grid">
          <div className="card">
            <h2 style={{marginTop:0}}>Create an account</h2>
            <p className="muted">Get started with your free account and boost your career</p>

            <label className="label">Full Name</label>
            <input className="input" type="text" placeholder="John Doe" />

            <label className="label">Email</label>
            <input className="input" type="email" placeholder="your.email@example.com" />

            <label className="label">Password</label>
            <input className="input" type="password" placeholder="Create a strong password" />

            <label className="label">Confirm Password</label>
            <input className="input" type="password" placeholder="Re-enter your password" />

            <label className="checkbox" style={{marginTop:10}}>
              <input type="checkbox" style={{marginRight:8}} /> I agree to the <button className="link-inline">Terms of Service</button> and <button className="link-inline">Privacy Policy</button>
            </label>

            <button className="btn-primary" style={{width:'100%', marginTop:14}} onClick={async()=>{
              const fullName = (document.querySelector('input[placeholder="John Doe"]') as HTMLInputElement)?.value || ''
              const email = (document.querySelector('input[placeholder="your.email@example.com"]') as HTMLInputElement)?.value || ''
              const password = (document.querySelector('input[placeholder="Create a strong password"]') as HTMLInputElement)?.value || ''
              const confirmPassword = (document.querySelector('input[placeholder="Re-enter your password"]') as HTMLInputElement)?.value || ''
              try {
                const { data } = await axios.post('/auth/signup', { fullName, email, password, confirmPassword })
                if (data?.token) {
                  localStorage.setItem('token', data.token)
                  navigate('/analyze')
                }
              } catch {
                alert('Sign up failed')
              }
            }}>Create Account</button>
            <div className="divider"><span>Or sign up with</span></div>
            <div className="social-row">
              <button className="social-btn" onClick={()=>{ window.location.href = `${backendBase}/oauth2/authorization/google` }}>⚪ Google</button>
              <button className="social-btn" onClick={()=>{ window.location.href = `${backendBase}/oauth2/authorization/github` }}>⚪ GitHub</button>
            </div>
            <div style={{textAlign:'center', marginTop:14}}>
              <span className="muted">Already have an account?</span> <button className="muted" style={{background:'transparent', border:'none', color:'var(--blue)', cursor:'pointer'}} onClick={()=>navigate('/signin')}>Sign in</button>
            </div>
          </div>

          <div>
            <div className="card benefits-card">
              <h3 style={{marginTop:0}}>What you'll get with your account</h3>
              <ul className="benefits-list">
                {[
                  'Unlimited resume analyses',
                  'AI-powered suggestions',
                  'ATS score optimization',
                  'Skills gap analysis',
                  'Save and compare results',
                  'Priority support',
                ].map((b, i)=> (
                  <li key={i} className="benefit"><Check/><span>{b}</span></li>
                ))}
              </ul>
            </div>

            <div className="card trust-card">
              <div style={{display:'flex', gap:8}}>
                <div className="pill" style={{background:'#6366f1'}}></div>
                <div className="pill" style={{background:'#a855f7'}}></div>
                <div className="pill" style={{background:'#10b981'}}></div>
              </div>
              <h4 style={{margin:'10px 0 0'}}>Join 10,000+ job seekers</h4>
              <p className="muted">Trusted by professionals worldwide to optimize their resumes and land dream jobs</p>
            </div>

            <div className="cta-offer">
              <strong>🎉 Limited Time Offer</strong>
              <p style={{marginTop:6}}>Get 3 months of premium features free when you sign up today!</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SignUp