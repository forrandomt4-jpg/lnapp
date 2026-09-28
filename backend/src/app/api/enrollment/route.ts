import { NextResponse } from 'next/server'
import prisma from '@/lib/prisma'
import { getSession, setSession } from '@/lib/session'

export async function POST(request: Request) {
  const { enrollmentNumber } = await request.json()

  if (!enrollmentNumber) {
    return NextResponse.json({ error: 'Enrollment number is required' }, { status: 400 })
  }

  const student = await prisma.student.findUnique({
    where: { enrollmentNumber }
  })

  if (!student) {
    return NextResponse.json({ error: 'Student not found' }, { status: 404 })
  }

  await setSession(student.enrollmentNumber)

  return NextResponse.json({ student })
}
