import { NextResponse } from 'next/server'
import { getSession } from '@/lib/session'
import prisma from '@/lib/prisma'
import { getCurrentLecture } from '@/lib/timetable'

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url)
  const queryEnrollment = searchParams.get('enrollment')
  
  const sessionEnrollment = await getSession()
  const enrollmentNumber = queryEnrollment || sessionEnrollment
  
  if (!enrollmentNumber) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 })

  const student = await prisma.student.findUnique({ where: { enrollmentNumber } })
  if (!student) return NextResponse.json({ error: 'Student not found' }, { status: 404 })

  const { current, next } = await getCurrentLecture(student.batch, new Date())
  
  return NextResponse.json({ current, next })
}
