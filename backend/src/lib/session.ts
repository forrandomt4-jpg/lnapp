import { cookies } from 'next/headers'

export async function setSession(enrollmentNumber: string) {
  const cookieStore = await cookies()
  cookieStore.set('session', enrollmentNumber, {
    httpOnly: true,
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax',
    path: '/',
    maxAge: 60 * 60 * 24 * 30, // 30 days
  })
}

export async function getSession() {
  const cookieStore = await cookies()
  const session = cookieStore.get('session')
  return session?.value
}

export async function clearSession() {
  const cookieStore = await cookies()
  cookieStore.delete('session')
}
