import { redirect } from 'next/navigation'
import { getSession, clearSession } from '@/lib/session'
import prisma from '@/lib/prisma'
import { getCurrentLecture } from '@/lib/timetable'
import { DashboardUI } from '@/components/DashboardUI'

export default async function Dashboard() {
  const enrollmentNumber = await getSession()
  if (!enrollmentNumber) redirect('/')

  const student = await prisma.student.findUnique({ where: { enrollmentNumber } })
  if (!student) {
    await clearSession()
    redirect('/')
  }

  const now = new Date()
  const { current, next } = await getCurrentLecture(student.batch, now)

  async function logout() {
    'use server'
    await clearSession()
    redirect('/')
  }

  return (
    <DashboardUI 
      student={student} 
      current={current} 
      next={next} 
      logoutAction={logout} 
    />
  )
}
