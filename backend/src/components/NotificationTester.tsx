'use client'

import { Bell } from 'lucide-react'

export function NotificationTester({ lecture }: { lecture: any }) {
  const triggerNotification = () => {
    if (!('Notification' in window)) {
      alert('This browser does not support desktop notifications')
      return
    }

    if (Notification.permission === 'granted') {
      showNotification()
    } else if (Notification.permission !== 'denied') {
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          showNotification()
        }
      })
    }
  }

  const showNotification = () => {
    if (!lecture) return
    const text = `Next up: ${lecture.subject} in Room ${lecture.classroom || 'TBA'} at ${lecture.startTime}`
    new Notification('Lecture Reminder', {
      body: text,
      icon: '/icon-192x192.png' // Make sure you have a real icon here for the PWA
    })
  }

  return (
    <button
      onClick={triggerNotification}
      className="flex items-center gap-2 bg-slate-900 text-white px-4 py-2.5 rounded-xl text-sm font-bold hover:bg-slate-800 transition-colors shadow-sm"
      title="Demo: Trigger a notification for the next lecture"
    >
      <Bell size={18} className="text-teal-400" />
      <span>Demo Notification</span>
    </button>
  )
}
