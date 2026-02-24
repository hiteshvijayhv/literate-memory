import { FormEvent, useState } from 'react'
import { api } from '../api/client'

export default function DashboardSettingsPage() {
  const [slug, setSlug] = useState('')
  const [title, setTitle] = useState('')
  const [bio, setBio] = useState('')
  const [message, setMessage] = useState('')

  async function save(e: FormEvent) {
    e.preventDefault()
    await api('/me/profile', { method: 'PATCH', body: JSON.stringify({ slug, title, bio, avatarUrl: '', themeJson: '{}' }) })
    setMessage('Profile saved.')
  }

  return (
    <form className="card grid" onSubmit={save}>
      <h2>Settings</h2>
      <input className="input" placeholder="slug" value={slug} onChange={(e) => setSlug(e.target.value)} />
      <input className="input" placeholder="display title" value={title} onChange={(e) => setTitle(e.target.value)} />
      <textarea className="input" placeholder="bio" value={bio} onChange={(e) => setBio(e.target.value)} />
      <button className="btn" type="submit">Save profile</button>
      {message && <div style={{ color: '#16a34a' }}>{message}</div>}
    </form>
  )
}
