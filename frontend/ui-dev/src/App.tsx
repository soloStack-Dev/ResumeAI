import { useState } from 'react'
import './index.css'
import axios from 'axios'

// App component
function App() {

  // Evaluation result type
  type EvalResult = {
    id: number
    matchPercentage: number
    atsScore: number
    missingSkills: string[]
    suggestions: string[]
    rewrittenBullets: string[]
    resumeText: string
    jobDescription: string
  }

  // State variables
  const [resumeFile, setResumeFile] = useState<File | null>(null)
  const [resumeText, setResumeText] = useState('')
  const [jobDescription, setJobDescription] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState<EvalResult | null>(null)
  // Tab state for resume input method
  const [resumeTab, setResumeTab] = useState<'upload' | 'paste'>('upload')

  // Helper function to clamp a number between 0 and 100
  // This is used to ensure that matchPercentage and atsScore are always valid percentages
  const clamp = (n: any) => {
    const x = typeof n === 'number' && Number.isFinite(n) ? n : 0
    return Math.max(0, Math.min(100, Math.round(x)))
  }

  // Helper function to filter and normalize strings in an array
  // This is used to ensure that missingSkills, suggestions, and rewrittenBullets are valid arrays of strings
  //normalize means to make sure that the data is in the correct format
  const filterStrings = (arr: any) => {
    if (!Array.isArray(arr)) return []
    return arr.map((s) => String(s).trim()).filter((s) => s.length > 0)
  }

  // Helper function to remove duplicate strings from an array
  // This is used to ensure that suggestions and rewrittenBullets are unique
  const unique = (arr: string[]) => Array.from(new Set(arr));


  const normalize = (data: EvalResult): EvalResult => ({
    // Normalize the data to ensure it has the correct format
    // This includes clamping matchPercentage and atsScore to valid percentages
    // Filtering and normalizing missingSkills, suggestions, and rewrittenBullets
    // Removing duplicate strings from suggestions and rewrittenBullets

    id: Number((data as any)?.id || 0),
    matchPercentage: clamp((data as any)?.matchPercentage),
    atsScore: clamp((data as any)?.atsScore),
    missingSkills: filterStrings((data as any)?.missingSkills),
    suggestions: unique(filterStrings((data as any)?.suggestions)),
    rewrittenBullets: filterStrings((data as any)?.rewrittenBullets),
    resumeText: String((data as any)?.resumeText || ''),
    jobDescription: String((data as any)?.jobDescription || ''),
  })

  // Helper function to generate fallback suggestions
  // This is used when no suggestions are provided by the model
  const makeFallbackSuggestions = (skills: string[], jd: string) => {
    const base = skills.slice(0, 10)
    const out: string[] = []
    // Generate fallback suggestions based on missing skills
    base.forEach((s) => {
      out.push(`Include ${s} explicitly in resume and summary`)
      out.push(`Add quantified bullet highlighting ${s} impact`)
    })
    // Add a suggestion to align keywords and phrasing with job description if it's long enough
    if (jd.length > 20) out.push('Align keywords and phrasing with job description')
    return unique(out).slice(0, 10)
  }
  // Helper function to generate fallback rewritten bullets
  // This is used when no rewritten bullets are provided by the model
  const makeFallbackBullets = (skills: string[]) => {
    const base = skills.slice(0, 6)
    const out = base.map((s) => `Delivered measurable outcomes using ${s}, improving KPIs and efficiency`)
    return unique(out)
  }


  // Helper function to submit the resume and job description for evaluation
  // This is used when the user clicks the "Evaluate" button
  const submit = async () => {
    //Validate input field
    setError('')

    setLoading(true)
    try {
      const fd = new FormData()
      if (resumeFile) fd.append('resumeFile', resumeFile)
      if (resumeText.trim()) fd.append('resumeText', resumeText)
      fd.append('jobDescription', jobDescription)

      // Make a POST request to the /api/rank endpoint with the resume and job description
      // Include the Authorization header with a Bearer token if available
      // This is used to authenticate the request with the server
      // If no token is available, the request is made without the Authorization header 
      // This allows unauthenticated users to use the basic features of the application
      const token = localStorage.getItem('token')
      const { data } = await axios.post('/api/rank', fd, { headers: { 'Content-Type': 'multipart/form-data', ...(token ? { Authorization: `Bearer ${token}` } : {}) } })
      setResult(normalize(data))
    } catch (e: any) {
      setError(e?.message || 'Request failed')
    } finally {
      setLoading(false)
    }
  }

  // Helper function to handle file drag and drop
  // This is used when the user drags a file onto the resume upload area
  const onDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault()
    const f = e.dataTransfer.files?.[0]
    if (f) setResumeFile(f)
  }

  // Helper function to render the upload icon
  // This is used in the resume upload area to indicate that a file can be dropped
  const UploadIcon = () => (
    <svg className="w-10 h-10 text-gray-400" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 16V8M8.5 11.5L12 8l3.5 3.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
      <path d="M5 16.5v1A2.5 2.5 0 007.5 20h9a2.5 2.5 0 002.5-2.5v-1" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  )

  // Helper function to render the document icon
  // This is used in the header to indicate that the application is a resume ranker
  const DocIcon = () => (
    <svg className="w-6 h-6 text-brand-blue" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M7 3.5h6l4 4V20.5a1 1 0 01-1 1H7a1 1 0 01-1-1V4.5a1 1 0 011-1z" stroke="currentColor" strokeWidth="1.5"/>
      <path d="M13 3.5v4h4" stroke="currentColor" strokeWidth="1.5"/>
    </svg>
  )

  return (
    <div className="page">
      <div className="container">
        <div className="header">
          <div className="brand-icon">
            <DocIcon/>
          </div>
          <div>
            <h1>Smart Resume Ranker</h1>
            <div className="subtitle">AI-Powered Analysis</div>
          </div>
        </div>
        <p className="muted">Upload your resume and paste the job description. Get instant match score, missing skills, ATS score, and AI-powered suggestions to improve your application.</p>

        <div className="grid" style={{marginTop:24}}>
          <div className="card">
            <div className="card-title">
              <div>
                <img style={{width:80, height:80, borderRadius:8}} src="https://i.ibb.co/LX3QK6Ds/pexels-cottonbro-5989943.jpg" alt="" />
              </div>
              <div>
                <h2>Resume</h2>
              </div>
              </div>
            <p className="muted" style={{marginTop:4}}>Upload your resume file or paste the text</p>
            <div className="tabs">
              {/* Tabs to switch between uploading a file or pasting text */}
              <button className={`tab ${resumeTab==='upload'?'tab-active':''}`} onClick={()=>setResumeTab('upload')}>Upload File</button>
              {/* Tab to switch to pasting text */}
              <button className={`tab ${resumeTab==='paste'?'tab-active':''}`} onClick={()=>setResumeTab('paste')}>Paste Text</button>
            </div>
            {/* Render the upload area or textarea based on the selected tab */}
            {resumeTab==='upload' ? (
              
              <div className="dropzone" onDragOver={(e)=>e.preventDefault()} onDrop={onDrop}>
                <div className="upload-icon">
                  <UploadIcon/>
                </div>
                <div style={{marginTop:8}}>Drag and drop your resume here</div>
                <div className="muted" style={{marginTop:4}}>or</div>
                <label>
                  <input type="file" accept=".pdf,.txt,.doc,.docx" style={{display:'none'}} onChange={(e)=>setResumeFile(e.target.files?.[0]||null)} />
                  <span className="choose-file">Choose File</span>
                </label>
                <div className="muted" style={{marginTop:8, fontSize:12}}>Supports PDF, DOC, DOCX, TXT</div>
                {resumeFile && <div style={{marginTop:6, fontSize:14}}>Selected: {resumeFile.name}</div>}
              </div>
            ) : (
              <textarea placeholder="Paste resume text here..." className="textarea" value={resumeText} onChange={(e)=>setResumeText(e.target.value)} />
            )}
          </div>

          <div className="card">
            <div className="card-title"><svg className="upload-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 3l2.5 6.5L21 12l-6.5 2.5L12 21l-2.5-6.5L3 12l6.5-2.5L12 3z" stroke="currentColor" strokeWidth="1.5"/></svg><h2>Job Description</h2></div>
            <p className="muted" style={{marginTop:4}}>Paste the job description you're applying for</p>
            <div className="textarea" style={{height:140}}>
              <div>Paste the job description here...</div>
              <div style={{marginTop:10}}>Example:</div>
              <div className="muted">We are looking for a Senior Software Engineer with 5+ years of experience in React, Node.js, and TypeScript. The ideal candidate should have strong problem-solving skills and experience with cloud platforms...</div>
            </div>
            <textarea className="textarea" placeholder="Paste job description here" value={jobDescription} onChange={(e)=>setJobDescription(e.target.value)} />
              {/* Display the character count for the job description */}
            <div className="muted" style={{marginTop:6, fontSize:12}}>{jobDescription.length} characters</div>
          </div>
        </div>

        <div className="actions">
          {/* Button to trigger the resume analysis */}
          <button className="btn-primary" disabled={loading || (!resumeFile && !resumeText) || !jobDescription} onClick={submit}>{loading ? 'Analyzing...' : 'Analyze Resume'}</button>
          {error && <span className="error">{error}</span>}
        </div>

        {result && (() => {
          // Normalize the result data to ensure consistent formatting
          const safe = normalize(result)
          return (
          <div className="result-grid">
            <div className="result-card" style={{borderColor: 'var(--red)'}}>
              <h3 style={{color:'var(--red)'}}>Missing Skills</h3>
              <ul className="list">
                {safe.missingSkills.length > 0 ? safe.missingSkills.map((s, i) => (
                  <li key={i} style={{marginBottom:4}}>{s}</li>
                )) : <li style={{color:'var(--muted)'}}>No missing skills detected!</li>}
              </ul>
            </div>
            <div className="result-card" style={{gridColumn:'span 2', borderColor: 'var(--blue)'}}>
              <h3 style={{color:'var(--blue)'}}>AI Suggestions</h3>
              <ul style={{marginTop:12}}>
                {safe.suggestions.length > 0 ? safe.suggestions.map((s, i) => (
                  <li key={i} className="chip" style={{marginBottom:8, backgroundColor:'#eff6ff', color:'var(--blue)', border:'1px solid #dbeafe'}}>{s}</li>
                )) : <li style={{color:'var(--muted)'}}>No specific suggestions. Good job! {makeFallbackSuggestions(safe.missingSkills,safe.jobDescription)}</li>}
              </ul>
            </div>
            <div className="result-card" style={{gridColumn:'span 3', borderColor: 'var(--emerald)'}}>
              <h3 style={{color:'var(--emerald)'}}>Rewritten Bullet Points</h3>
              <ul style={{marginTop:12, paddingLeft:0, listStyle:'none'}}>
                {safe.rewrittenBullets.length > 0 ? safe.rewrittenBullets.map((s, i) => (
                  <li key={i} style={{border:'1px solid #d1fae5', background:'#ecfdf5', borderRadius:10, padding:16, marginBottom:12, color:'#065f46'}}>
                    <div style={{fontWeight:600, marginBottom:4, fontSize:14, textTransform:'uppercase', color:'var(--emerald)'}}>Option {i+1}</div>
                    {s}
                  </li>
                )) : <li style={{color:'var(--muted)'}}>No bullet point rewrites available. {makeFallbackBullets(safe.missingSkills)}</li>}
              </ul>
            </div>
          </div>
          )
        })()}
      </div>
    </div>
  )
}

export default App
