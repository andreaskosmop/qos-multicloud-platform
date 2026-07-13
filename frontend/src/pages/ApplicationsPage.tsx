import { useState } from 'react'
import { useApplications, useCreateApplication, useDeleteApplication } from '../hooks/useApplications'

export default function ApplicationsPage() {
  const { data: applications, isLoading } = useApplications()
  const createApp = useCreateApplication()
  const deleteApp = useDeleteApplication()
  const [title, setTitle] = useState('')

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    if (!title.trim()) return
    await createApp.mutateAsync({ title, description: '', version: '1.0' })
    setTitle('')
  }

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">Εφαρμογές</h1>

      <form onSubmit={handleCreate} className="mb-6 flex gap-2">
        <input
          className="border rounded px-3 py-2 flex-1"
          placeholder="Τίτλος νέας εφαρμογής"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
        <button className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
          Δημιουργία
        </button>
      </form>

      {isLoading ? (
        <p>Φόρτωση...</p>
      ) : (
        <div className="grid gap-4">
          {applications?.map((app) => (
            <div key={app.id} className="border rounded-lg p-4 flex justify-between items-center">
              <div>
                <h3 className="font-semibold">{app.title}</h3>
                <span className="text-sm text-slate-500">{app.status}</span>
              </div>
              <button
                onClick={() => deleteApp.mutate(app.id)}
                className="text-red-600 hover:underline"
              >
                Διαγραφή
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
