import { Link } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'

export default function Navbar() {
  const { token, username, logout } = useAuthStore()

  return (
    <nav className="bg-slate-800 text-white px-6 py-3 flex justify-between items-center">
      <Link to="/dashboard" className="font-bold text-lg">
        QoS Multi-Cloud Platform
      </Link>
      <div className="flex gap-4 items-center">
        {token ? (
          <>
            <Link to="/applications" className="hover:underline">Εφαρμογές</Link>
            <span className="text-slate-300">{username}</span>
            <button onClick={logout} className="bg-red-600 px-3 py-1 rounded hover:bg-red-700">
              Αποσύνδεση
            </button>
          </>
        ) : (
          <Link to="/login" className="hover:underline">Σύνδεση</Link>
        )}
      </div>
    </nav>
  )
}
