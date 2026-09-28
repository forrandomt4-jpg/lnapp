import { NextResponse } from 'next/server'
import prisma from '@/lib/prisma'
import { getSession } from '@/lib/session'
import { getCurrentLecture } from '@/lib/timetable'

export async function GET() {
  const enrollmentNumber = await getSession()
  if (!enrollmentNumber) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 })

  const student = await prisma.student.findUnique({ where: { enrollmentNumber } })
  if (!student) return NextResponse.json({ error: 'Student not found' }, { status: 404 })

  const { allToday } = await getCurrentLecture(student.batch, new Date())

  return NextResponse.json({ today: allToday })
}
