'use client'

import { useEffect, useState } from 'react'

export function Countdown({ targetTime, isCurrent, targetDayOfWeek }: { targetTime: string; isCurrent: boolean; targetDayOfWeek: number }) {
  const [timeLeft, setTimeLeft] = useState('')

  useEffect(() => {
    const updateCountdown = () => {
      const now = new Date()
      const [hours, minutes] = targetTime.split(':').map(Number)
      
      let targetDate = new Date(now)
      targetDate.setHours(hours, minutes, 0, 0)
      
      const currentDayOfWeek = now.getDay()
      
      // Calculate how many days to add to get to the target day of week
      let daysToAdd = 0
      if (!isCurrent) {
        if (currentDayOfWeek === targetDayOfWeek) {
          // It's the same day. If the time has already passed, it must be next week
          if (now.getTime() > targetDate.getTime()) {
            daysToAdd = 7
          }
        } else {
          // Different day
          daysToAdd = targetDayOfWeek - currentDayOfWeek
          if (daysToAdd < 0) {
            daysToAdd += 7 // It's next week
          }
        }
      }
      
      targetDate.setDate(targetDate.getDate() + daysToAdd)
      
      let diff = targetDate.getTime() - now.getTime()

      if (diff < 0 && isCurrent) {
        setTimeLeft('Ended')
        return
      }

      const d = Math.floor(diff / (1000 * 60 * 60 * 24))
      const h = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      const m = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      const s = Math.floor((diff % (1000 * 60)) / 1000)

      let res = []
      if (d > 0) res.push(`${d}d`)
      if (h > 0 || d > 0) res.push(`${h}h`)
      if (m > 0 || h > 0 || d > 0) res.push(`${m}m`)
      res.push(`${s}s`)
      
      setTimeLeft(res.join(' '))
    }

    updateCountdown()
    const interval = setInterval(updateCountdown, 1000)
    return () => clearInterval(interval)
  }, [targetTime, isCurrent, targetDayOfWeek])

  return (
    <div className="flex items-center gap-2">
      <span className={isCurrent ? 'text-blue-100 uppercase text-xs font-bold tracking-wider' : 'text-gray-400 uppercase text-xs font-bold tracking-wider'}>
        {isCurrent ? 'Ends in' : 'Starts in'}
      </span>
      <span className={`font-mono text-lg font-semibold ${isCurrent ? 'text-white' : 'text-gray-900'}`}>
        {timeLeft || '...'}
      </span>
    </div>
  )
}
