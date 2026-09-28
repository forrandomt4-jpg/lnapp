import prisma from '@/lib/prisma'
import { revalidatePath } from 'next/cache'

export default async function AdminPage() {
  const changes = await prisma.timetableChange.findMany({
    orderBy: { id: 'desc' }
  })

  async function addChange(formData: FormData) {
    'use server'
    const batch = formData.get('batch') as string
    const date = formData.get('date') as string
    const startTime = formData.get('startTime') as string
    const endTime = formData.get('endTime') as string
    const subject = formData.get('subject') as string
    const faculty = formData.get('faculty') as string
    const classroom = formData.get('classroom') as string
    const isCancelled = formData.get('isCancelled') === 'on'

    await prisma.timetableChange.create({
      data: {
        batch, date, startTime, endTime, 
        subject: subject || null, 
        faculty: faculty || null, 
        classroom: classroom || null, 
        isCancelled
      }
    })

    revalidatePath('/dashboard')
    revalidatePath('/timetable')
    revalidatePath('/admin')
  }

  return (
    <div className="p-6 font-sans max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Admin Panel - Timetable Changes</h1>
      
      <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm mb-8">
        <h2 className="text-lg font-bold mb-4">Add Change</h2>
        <form action={addChange} className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">Batch</label>
            <select name="batch" className="w-full border p-2 rounded" required>
              <option value="ALL">ALL</option>
              <option value="CP1">CP1</option>
              <option value="CP2">CP2</option>
              <option value="CP3">CP3</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Date (YYYY-MM-DD)</label>
            <input type="date" name="date" className="w-full border p-2 rounded" required />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Start Time (HH:MM)</label>
            <input type="time" name="startTime" className="w-full border p-2 rounded" required />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">End Time (HH:MM)</label>
            <input type="time" name="endTime" className="w-full border p-2 rounded" required />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">New Subject (optional)</label>
            <input type="text" name="subject" className="w-full border p-2 rounded" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">New Faculty (optional)</label>
            <input type="text" name="faculty" className="w-full border p-2 rounded" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">New Room (optional)</label>
            <input type="text" name="classroom" className="w-full border p-2 rounded" />
          </div>
          <div className="flex items-center">
            <input type="checkbox" name="isCancelled" id="isCancelled" className="mr-2" />
            <label htmlFor="isCancelled" className="text-sm font-medium">Cancel Lecture</label>
          </div>
          <div className="col-span-2 mt-4">
            <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded font-medium">
              Save Change
            </button>
          </div>
        </form>
      </div>

      <h2 className="text-lg font-bold mb-4">Recent Changes</h2>
      <div className="space-y-4">
        {changes.map(c => (
          <div key={c.id} className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="font-bold">{c.date} | {c.batch} | {c.startTime} - {c.endTime}</div>
            {c.isCancelled ? (
              <span className="text-red-600 font-bold">CANCELLED</span>
            ) : (
              <div className="text-sm text-gray-600 mt-2">
                {c.subject && <div>Subject: {c.subject}</div>}
                {c.faculty && <div>Faculty: {c.faculty}</div>}
                {c.classroom && <div>Room: {c.classroom}</div>}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
