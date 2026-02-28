import { Navigate, Route, Routes } from 'react-router-dom'
import DashboardLayout from './layouts/DashboardLayout'
import DashboardAnalyticsPage from './pages/DashboardAnalyticsPage'
import DashboardAppearancePage from './pages/DashboardAppearancePage'
import DashboardLinksPage from './pages/DashboardLinksPage'
import DashboardSettingsPage from './pages/DashboardSettingsPage'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import PublicProfilePage from './pages/PublicProfilePage'
import RegisterPage from './pages/RegisterPage'

function RequireAuth({ children }: { children: JSX.Element }) {
  const token = localStorage.getItem('accessToken')
  return token ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      <Route
        path="/dashboard"
        element={
          <RequireAuth>
            <DashboardLayout />
          </RequireAuth>
        }
      >
        <Route path="links" element={<DashboardLinksPage />} />
        <Route path="appearance" element={<DashboardAppearancePage />} />
        <Route path="analytics" element={<DashboardAnalyticsPage />} />
        <Route path="settings" element={<DashboardSettingsPage />} />
        <Route index element={<Navigate to="links" replace />} />
      </Route>

      <Route path="/:slug" element={<PublicProfilePage />} />
    </Routes>
  )
}
