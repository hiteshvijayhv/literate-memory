import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client'
import { Link } from '../api/types'

type PublicProfile = {
  slug: string
  title: string
  bio: string
  avatarUrl: string
  themeJson: string
  links: Link[]
}

export default function PublicProfilePage() {
  const { slug } = useParams()
  const [profile, setProfile] = useState<PublicProfile | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!slug) return
    api<PublicProfile>(`/p/${slug}`).then(setProfile).catch((err) => setError((err as Error).message))
  }, [slug])

  if (error) return <div className="container"><div className="card" style={{ color: '#dc2626' }}>{error}</div></div>
  if (!profile) return <div className="container"><div className="card">Loading profile…</div></div>

  return (
    <div className="container">
      <div className="card grid">
        <h1>{profile.title}</h1>
        <p>{profile.bio}</p>
        {profile.links.map((link) => (
          <a key={link.id} className="btn" href={link.url} target="_blank">{link.title}</a>
        ))}
      </div>
    </div>
  )
}
