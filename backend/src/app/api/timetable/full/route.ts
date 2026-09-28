import { NextResponse } from 'next/server'
import prisma from '@/lib/prisma'

export async function GET() {
  const entries = await prisma.timetableEntry.findMany({
    orderBy: [
      { dayOfWeek: 'asc' },
      { startTime: 'asc' }
    ]
  })

  return NextResponse.json({ entries })
}
