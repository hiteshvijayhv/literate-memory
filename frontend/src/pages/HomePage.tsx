import { Link } from 'react-router-dom'

export default function HomePage() {
  return (
    <div className="container">
      <div className="card">
        <h1>Linktree Clone</h1>
        <p>Frontend is now implemented with React + Vite and connected to your Spring backend APIs.</p>
        <div style={{ display: 'flex', gap: '.75rem' }}>
          <Link className="btn" to="/register">Get started</Link>
          <Link className="btn secondary" to="/login">Login</Link>
        </div>
      </div>
    </div>
  )
}
