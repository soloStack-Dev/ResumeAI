import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import Home from './Home.tsx'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import SignIn from './SignIn.tsx'
import SignUp from './SignUp.tsx'
import OAuthCallback from './OAuthCallback.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/analyze" element={<App />} />
        <Route path="/signin" element={<SignIn />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/oauth2/callback" element={<OAuthCallback />} />
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)
