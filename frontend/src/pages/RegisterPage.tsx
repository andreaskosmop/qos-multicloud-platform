import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'

export default function RegisterPage() {
  const [form, setForm] = useState({
    username: '', fullName: '', email: '', password: '',
    securityQuestion: '', securityAnswer: '',
  })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    try {
      await api.post('/auth/register', form)
      setSuccess(true)
      setTimeout(() => navigate('/login'), 2000)
    } catch (err: any) {
      setError(err.response?.data?.message ?? 'Σφάλμα εγγραφής')
    }
  }

  if (success) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <p className="text-green-600 text-lg">
          Επιτυχής εγγραφή! Αναμονή έγκρισης από διαχειριστή.
        </p>
      </div>
    )
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-slate-100">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded-lg shadow-md w-96">
        <h1 className="text-2xl font-bold mb-6">Εγγραφή</h1>
        {error && <p className="text-red-600 mb-4">{error}</p>}
        {(['username', 'fullName', 'email', 'password'] as const).map((field) => (
          <input
            key={field}
            type={field === 'password' ? 'password' : 'text'}
            className="w-full border rounded px-3 py-2 mb-3"
            placeholder={field}
            value={form[field]}
            onChange={(e) => setForm({ ...form, [field]: e.target.value })}
          />
        ))}
        <input
          className="w-full border rounded px-3 py-2 mb-3"
          placeholder="Ερώτηση ασφαλείας"
          value={form.securityQuestion}
          onChange={(e) => setForm({ ...form, securityQuestion: e.target.value })}
        />
        <input
          className="w-full border rounded px-3 py-2 mb-4"
          placeholder="Απάντηση ασφαλείας"
          value={form.securityAnswer}
          onChange={(e) => setForm({ ...form, securityAnswer: e.target.value })}
        />
        <button className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700">
          Εγγραφή
        </button>
      </form>
    </div>
  )
}
