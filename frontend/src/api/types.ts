export type AuthResponse = { accessToken: string; userId: string; email: string }

export type Profile = {
  id: string
  userId: string
  slug: string
  title: string
  bio: string
  avatarUrl: string
  themeJson: string
}

export type Link = {
  id: string
  profileId: string
  title: string
  url: string
  isEnabled: boolean
  position: number
}

export type AnalyticsResponse = {
  totalViews: number
  uniqueVisitors: number
  totalClicks: number
  daily: Array<{ date: string; views: number; clicks: number }>
  topLinks: Array<{ linkId: string; title: string; clicks: number }>
}
