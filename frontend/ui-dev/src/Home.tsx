import './index.css'
import { useNavigate } from 'react-router-dom'

function Home() {
  const navigate = useNavigate()
  const BrandIcon = () => (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="4" y="3" width="16" height="18" rx="3" stroke="#2563eb" strokeWidth="1.5"/>
      <path d="M8 8h8M8 12h8M8 16h6" stroke="#2563eb" strokeWidth="1.5"/>
    </svg>
  )

  return (
    <div className="page">
      <div className="container">
        <header className="header" style={{justifyContent:'space-between'}}>
          <div className="header">
            <div className="brand-icon"><BrandIcon/></div>
            <strong>Smart Resume Ranker</strong>
          </div>
          <div style={{display:'flex', gap:12}}>
            <button className="choose-file" onClick={()=>navigate('/signin')}>Sign In</button>
            <button className="btn-primary" style={{padding:'8px 14px'}} onClick={()=>navigate('/signup')}>Get Started</button>
          </div>
        </header>

        <section className="hero" style={{marginTop:24}}>
          <div>
            <div className="subtitle" style={{display:'inline-block', background:'#e6f0ff', padding:'6px 10px', borderRadius:12}}>
              AI-Powered Resume Analysis
            </div>
            <h1 className="hero-title">Land Your Dream Job with <span className="highlight">Smart</span><br/>Resume Ranking</h1>
            <p className="muted">Upload your resume, paste the job description, and get instant AI-powered insights. Discover your match score, missing skills, and actionable suggestions to stand out from the competition.</p>
            <div style={{display:'flex', gap:12, marginTop:16}}>
              <button className="btn-primary" onClick={()=>navigate('/signup')}>Get Started Free</button>
              <button className="choose-file" onClick={()=>navigate('/signin')}>Sign In</button>
            </div>
            <div style={{display:'flex', gap:20, marginTop:12}}>
              <span className="muted" style={{fontSize:12}}>No credit card required</span>
              <span className="muted" style={{fontSize:12}}>Free forever</span>
            </div>
          </div>
          <div>
            <img src="https://i.ibb.co/HD3WJTSb/Gemini-Generated-Image-qyzsa1qyzsa1qyzs.png" alt="pexels-image" className="hero-image" />
          </div>
        </section>

        <section style={{marginTop:32}}>
          <h2 style={{textAlign:'center', fontSize:28}}>Everything You Need to Succeed</h2>
          <p className="muted" style={{textAlign:'center'}}>Powerful features designed to help you create the perfect resume for every job application</p>
          <div className="feature-grid">
            {[
              {title:'Match Score Analysis', desc:'Get precise matching scores between your resume and job descriptions'},
              {title:'ATS Optimization', desc:'Ensure your resume passes Applicant Tracking Systems with high scores'},
              {title:'AI Suggestions', desc:'Receive intelligent recommendations to improve your resume content'},
              {title:'Instant Results', desc:'Get comprehensive analysis in seconds, not hours'},
              {title:'Secure & Private', desc:'Your data is encrypted and never shared with third parties'},
              {title:'Skills Gap Analysis', desc:'Identify missing skills and keywords to strengthen your application'},
            ].map((f, i) => (
              <div key={i} className="feature-card">
                <div style={{display:'flex', alignItems:'center', gap:10}}>
                  <div className="brand-icon" style={{background:'#eef2ff'}}><BrandIcon/></div>
                  <strong>{f.title}</strong>
                </div>
                <p className="muted" style={{marginTop:8}}>{f.desc}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="cta">
          <h3 style={{margin:0, fontSize:24}}>Ready to Boost Your Career?</h3>
          <p style={{marginTop:6}}>Join thousands of job seekers who have improved their resumes and landed their dream jobs</p>
          <button className="choose-file" style={{background:'#fff', color:'var(--blue)', fontWeight:600}} onClick={()=>navigate('/analyze')}>Start Analyzing Now</button>
        </section>

        <footer style={{display:'flex', justifyContent:'space-between', alignItems:'center'}}>
          <div className="header">
            <div className="brand-icon"><BrandIcon/></div>
            <span>Smart Resume Ranker</span>
          </div>
          <span className="muted">© 2024 All rights reserved</span>
        </footer>
      </div>
    </div>
  )
}

export default Home