import { redirect } from 'next/navigation'
import { getSession, setSession } from '@/lib/session'
import prisma from '@/lib/prisma'
import { LoginForm } from '@/components/LoginForm'

export default async function Home() {
  const session = await getSession()
  if (session) {
    redirect('/dashboard')
  }

  async function login(formData: FormData) {
    'use server'
    const enrollmentNumber = formData.get('enrollmentNumber') as string
    if (!enrollmentNumber) return

    const student = await prisma.student.findUnique({
      where: { enrollmentNumber }
    })

    if (student) {
      await setSession(student.enrollmentNumber)
      redirect('/dashboard')
    } else {
      redirect('/?error=invalid')
    }
  }

  return (
    <main className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-4 font-sans selection:bg-teal-200">
      <LoginForm loginAction={login} />
    </main>
  )
}
