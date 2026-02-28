import { FormEvent, useEffect, useState } from 'react'
import { api } from '../api/client'
import { Link } from '../api/types'

export default function DashboardLinksPage() {
  const [links, setLinks] = useState<Link[]>([])
  const [title, setTitle] = useState('')
  const [url, setUrl] = useState('')
  const [error, setError] = useState('')

  async function load() {
    try {
      setLinks(await api<Link[]>('/me/links'))
    } catch (err) {
      setError((err as Error).message)
    }
  }

  useEffect(() => { void load() }, [])

  async function create(e: FormEvent) {
    e.preventDefault()
    setError('')
    try {
      await api('/me/links', { method: 'POST', body: JSON.stringify({ title, url, isEnabled: true }) })
      setTitle(''); setUrl('')
      await load()
    } catch (err) {
      setError((err as Error).message)
    }
  }

  async function remove(id: string) {
    await api(`/me/links/${id}`, { method: 'DELETE' })
    await load()
  }

  return (
    <div className="grid">
      <form className="card grid" onSubmit={create}>
        <h2>Links</h2>
        {error && <div style={{ color: '#dc2626' }}>{error}</div>}
        <input className="input" placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
        <input className="input" placeholder="https://example.com" value={url} onChange={(e) => setUrl(e.target.value)} />
        <button className="btn" type="submit">Add link</button>
      </form>

      <div className="card">
        {links.length === 0 ? <p>No links yet.</p> : links.map((link) => (
          <div className="link-item" key={link.id}>
            <div>
              <strong>{link.title}</strong>
              <div><a href={link.url} target="_blank">{link.url}</a></div>
            </div>
            <button className="btn secondary" onClick={() => void remove(link.id)}>Delete</button>
          </div>
        ))}
      </div>
    </div>
  )
}
