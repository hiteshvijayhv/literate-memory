import { useState } from 'react'
import { api } from '../api/client'
import { AnalyticsResponse } from '../api/types'

export default function DashboardAnalyticsPage() {
  const [data, setData] = useState<AnalyticsResponse | null>(null)
  const [error, setError] = useState('')

  async function load() {
    const to = new Date().toISOString()
    const from = new Date(Date.now() - 1000 * 60 * 60 * 24 * 7).toISOString()
    try {
      setData(await api<AnalyticsResponse>(`/me/analytics?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`))
      setError('')
    } catch (err) {
      setError((err as Error).message)
    }
  }

  return (
    <div className="grid">
      <div className="card">
        <h2>Analytics</h2>
        <button className="btn" onClick={() => void load()}>Load last 7 days</button>
        {error && <p style={{ color: '#dc2626' }}>{error}</p>}
      </div>
      {data && (
        <div className="grid grid-2">
          <div className="card"><h3>Total Views</h3><p>{data.totalViews}</p></div>
          <div className="card"><h3>Unique Visitors</h3><p>{data.uniqueVisitors}</p></div>
          <div className="card"><h3>Total Clicks</h3><p>{data.totalClicks}</p></div>
          <div className="card"><h3>Top Links</h3>{data.topLinks.map((l) => <div key={l.linkId}>{l.title} <span className="badge">{l.clicks}</span></div>)}</div>
        </div>
      )}
    </div>
  )
}
