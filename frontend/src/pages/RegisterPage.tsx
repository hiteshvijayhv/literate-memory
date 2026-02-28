import { FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import { AuthResponse } from '../api/types'

export default function RegisterPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    try {
      const res = await api<AuthResponse>('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ email, password })
      })
      localStorage.setItem('accessToken', res.accessToken)
      navigate('/dashboard/settings')
    } catch (err) {
      setError((err as Error).message)
    }
  }

  return (
    <div className="container">
      <form className="card grid" onSubmit={onSubmit}>
        <h2>Create account</h2>
        {error && <div style={{ color: '#dc2626' }}>{error}</div>}
        <input className="input" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input className="input" type="password" placeholder="Password (min 8 chars)" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button className="btn" type="submit">Register</button>
      </form>
    </div>
  )
}
