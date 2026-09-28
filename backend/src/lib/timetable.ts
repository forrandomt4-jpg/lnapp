import prisma from './prisma'

export async function getCurrentLecture(batch: string, date: Date = new Date()) {
  const dayOfWeek = date.getDay() // 0=Sun, 1=Mon, ..., 6=Sat

  const currentTime = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`

  // Fetch all entries for today and this batch (or ALL)
  const entries = await prisma.timetableEntry.findMany({
    where: {
      dayOfWeek,
      batch: { in: [batch, 'ALL'] }
    },
    orderBy: { startTime: 'asc' }
  })

  // We should also check TimetableChanges, but let's implement base logic first
  // and add changes on top.

  // Fetch changes for today
  const dateString = date.toISOString().split('T')[0]
  const changes = await prisma.timetableChange.findMany({
    where: {
      date: dateString,
      batch: { in: [batch, 'ALL'] }
    }
  })

  // Apply changes to entries
  const adjustedEntries = entries.map(entry => {
    const change = changes.find(c => c.startTime === entry.startTime && c.endTime === entry.endTime && (c.batch === entry.batch || c.batch === 'ALL'))
    if (change) {
      if (change.isCancelled) return null
      return {
        ...entry,
        subject: change.subject || entry.subject,
        faculty: change.faculty || entry.faculty,
        classroom: change.classroom || entry.classroom,
        isChanged: true
      }
    }
    return { ...entry, isChanged: false }
  }).filter(Boolean) as any[]

  // Add completely new entries from changes
  const newEntries = changes.filter(c => !entries.find(e => e.startTime === c.startTime && e.endTime === c.endTime))
  for (const newEntry of newEntries) {
    if (!newEntry.isCancelled) {
      adjustedEntries.push({
        id: `change-${newEntry.id}`,
        batch: newEntry.batch,
        subject: newEntry.subject,
        faculty: newEntry.faculty,
        classroom: newEntry.classroom,
        dayOfWeek,
        startTime: newEntry.startTime,
        endTime: newEntry.endTime,
        isChanged: true
      })
    }
  }

  // Sort again
  adjustedEntries.sort((a, b) => a.startTime.localeCompare(b.startTime))

  let current = null
  let next = null

  for (let i = 0; i < adjustedEntries.length; i++) {
    const entry = adjustedEntries[i]
    if (currentTime >= entry.startTime && currentTime < entry.endTime) {
      current = entry
      if (i + 1 < adjustedEntries.length) {
        next = adjustedEntries[i + 1]
      }
      break
    } else if (currentTime < entry.startTime) {
      next = entry
      break
    }
  }

  // DEMO FALLBACK: If it's a weekend or after hours, just show Monday's first class as "Next" so the UI is testable
  if (!current && !next) {
    const fallbackEntries = await prisma.timetableEntry.findMany({
      where: { dayOfWeek: 1, batch: { in: [batch, 'ALL'] } },
      orderBy: { startTime: 'asc' }
    })
    if (fallbackEntries.length > 0) {
      next = fallbackEntries[0]
    }
  }

  return { current, next, allToday: adjustedEntries }
}
