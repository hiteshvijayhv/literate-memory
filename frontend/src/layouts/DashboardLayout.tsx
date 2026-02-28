import { NavLink, Outlet } from 'react-router-dom'

export default function DashboardLayout() {
  return (
    <div className="container">
      <h1>Dashboard</h1>
      <div className="nav">
        <NavLink to="/dashboard/links">Links</NavLink>
        <NavLink to="/dashboard/appearance">Appearance</NavLink>
        <NavLink to="/dashboard/analytics">Analytics</NavLink>
        <NavLink to="/dashboard/settings">Settings</NavLink>
      </div>
      <Outlet />
    </div>
  )
}
