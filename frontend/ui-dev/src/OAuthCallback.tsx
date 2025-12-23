import { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

function OAuthCallback() {
  const [params] = useSearchParams()
  const navigate = useNavigate()

  useEffect(() => {
    const token = params.get('token')
    if (token) {
      localStorage.setItem('token', token)
      navigate('/analyze')
    } else {
      navigate('/signin')
    }
  }, [params, navigate])

  return null
}

export default OAuthCallback