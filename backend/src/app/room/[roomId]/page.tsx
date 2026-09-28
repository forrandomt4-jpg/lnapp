import { redirect } from 'next/navigation'
import { getSession } from '@/lib/session'
import prisma from '@/lib/prisma'
import { getCurrentLecture } from '@/lib/timetable'
import Link from 'next/link'
import { Clock, MapPin, User, ArrowLeft } from 'lucide-react'

export default async function RoomPage({ params }: { params: { roomId: string } }) {
  const roomId = params.roomId
  const enrollmentNumber = await getSession()
  
  if (!enrollmentNumber) {
    redirect('/')
  }

  const student = await prisma.student.findUnique({ where: { enrollmentNumber } })
  if (!student) {
    redirect('/')
  }

  const now = new Date()
  const { allToday } = await getCurrentLecture(student.batch, now)
  const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`

  // Find the lecture happening in this room for this student right now
  let activeLecture = null
  for (const entry of allToday) {
    if (entry.classroom === roomId && currentTime >= entry.startTime && currentTime < entry.endTime) {
      activeLecture = entry
      break
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col font-sans">
      <header className="bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center sticky top-0 z-10">
        <div className="flex items-center gap-4">
          <Link href="/dashboard" className="text-gray-500 hover:text-gray-900">
            <ArrowLeft size={20} />
          </Link>
          <h1 className="text-xl font-bold text-gray-900">Room {roomId}</h1>
        </div>
      </header>

      <main className="flex-1 p-6 max-w-2xl mx-auto w-full space-y-6">
        {activeLecture ? (
          <div className="bg-green-600 border border-green-700 text-white rounded-2xl p-6 shadow-md">
            <h2 className="text-sm font-bold uppercase tracking-wider mb-4 text-green-200">You are in the right room</h2>
            <h3 className="text-3xl font-bold mb-4">{activeLecture.subject}</h3>
            
            <div className="space-y-3">
              {activeLecture.faculty && (
                <div className="flex items-center gap-2 text-sm">
                  <User size={16} className="text-green-200" />
                  <span>{activeLecture.faculty}</span>
                </div>
              )}
              <div className="flex items-center gap-2 text-sm">
                <Clock size={16} className="text-green-200" />
                <span>{activeLecture.startTime} - {activeLecture.endTime}</span>
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-red-50 border border-red-100 rounded-2xl p-6 text-center text-red-600">
            <MapPin size={48} className="mx-auto mb-4 opacity-50" />
            <h3 className="text-xl font-bold mb-2">No Active Lecture Here</h3>
            <p className="text-sm opacity-80">
              You don't have a scheduled lecture in Room {roomId} at this time.
            </p>
            <div className="mt-6">
              <Link href="/dashboard" className="bg-red-100 text-red-700 px-4 py-2 rounded-lg font-medium hover:bg-red-200 transition-colors">
                Go to Dashboard
              </Link>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
