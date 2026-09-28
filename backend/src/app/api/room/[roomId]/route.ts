import { NextResponse } from 'next/server'
import prisma from '@/lib/prisma'
import { getSession } from '@/lib/session'
import { getCurrentLecture } from '@/lib/timetable'

export async function GET(request: Request, { params }: { params: Promise<{ roomId: string }> }) {
  const { roomId } = await params
  const enrollmentNumber = await getSession()
  
  if (!enrollmentNumber) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 })

  const student = await prisma.student.findUnique({ where: { enrollmentNumber } })
  if (!student) return NextResponse.json({ error: 'Student not found' }, { status: 404 })

  const now = new Date()
  const { allToday } = await getCurrentLecture(student.batch, now)
  const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`

  let activeLecture = null
  for (const entry of allToday) {
    if (entry.classroom === roomId && currentTime >= entry.startTime && currentTime < entry.endTime) {
      activeLecture = entry
      break
    }
  }

  return NextResponse.json({ activeLecture })
}
