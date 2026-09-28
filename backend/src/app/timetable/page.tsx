import prisma from '@/lib/prisma'
import Link from 'next/link'
import { ArrowLeft } from 'lucide-react'

function formatTime12h(time24: string) {
  if (!time24) return ''
  const [hourStr, minStr] = time24.split(':')
  let hour = parseInt(hourStr)
  const ampm = hour >= 12 ? 'PM' : 'AM'
  hour = hour % 12
  if (hour === 0) hour = 12
  return `${hour}:${minStr} ${ampm}`
}

export default async function FullTimetable(props: { searchParams: Promise<{ batch?: string }> }) {
  const searchParams = await props.searchParams
  const batch = searchParams.batch as string | undefined

  const where: any = {}
  if (batch) where.batch = { in: [batch, 'ALL'] }

  const entries = await prisma.timetableEntry.findMany({
    where,
    orderBy: [
      { startTime: 'asc' }
    ]
  })

  // Fixed timeslots as per the image
  const timeslots = [
    { start: "10:30", end: "11:30" },
    { start: "11:30", end: "12:30" },
    { start: "13:00", end: "14:00" },
    { start: "14:00", end: "15:00" },
    { start: "15:10", end: "16:10" },
    { start: "16:10", end: "17:10" },
  ]

  const days = [
    { id: 1, name: "MONDAY" },
    { id: 2, name: "TUESDAY" },
    { id: 3, name: "WEDNESDAY" },
    { id: 4, name: "THURSDAY" },
    { id: 5, name: "FRIDAY" },
  ]

  // We need to determine rowspans.
  // We'll maintain a Set of skipped cells (format: `dayId-slotStart`)
  const skipCells = new Set<string>()

  const getCellEntries = (dayId: number, slotStart: string, slotEnd: string) => {
    return entries.filter(e => e.dayOfWeek === dayId && e.startTime === slotStart)
  }

  // Calculate rowSpan for an entry
  const getRowSpan = (entryStart: string, entryEnd: string) => {
    let span = 0
    for (const slot of timeslots) {
      if (slot.start >= entryStart && slot.end <= entryEnd) {
        span++
      }
    }
    return span > 0 ? span : 1
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-900">
      <header className="bg-white border-b border-slate-200 px-4 py-3 flex justify-between items-center sticky top-0 z-10 shadow-sm">
        <div className="flex items-center gap-4">
          <Link href="/" className="text-slate-500 hover:text-teal-600 transition-colors">
            <ArrowLeft size={20} />
          </Link>
          <h1 className="text-xl font-black text-slate-800 tracking-tight">Full Timetable</h1>
        </div>
      </header>

      <main className="flex-1 p-4 mx-auto w-full max-w-7xl mt-4">
        <div className="bg-white p-4 rounded-xl shadow-sm mb-6 border border-slate-200">
          <form className="flex gap-4 items-end max-w-xs">
            <div className="flex-1">
              <label className="block text-xs font-bold text-slate-500 uppercase mb-1.5 tracking-wider">Filter by Batch</label>
              <select name="batch" defaultValue={batch || ''} className="w-full border-2 border-slate-200 rounded-xl p-2.5 outline-none focus:border-teal-500 transition-colors text-sm font-semibold bg-slate-50 text-slate-800">
                <option value="">All Batches (Combined)</option>
                <option value="CP1">CP1 Only</option>
                <option value="CP2">CP2 Only</option>
                <option value="CP3">CP3 Only</option>
              </select>
            </div>
            <button type="submit" className="bg-teal-600 text-white px-5 py-3 rounded-xl font-bold hover:bg-teal-700 transition-colors text-sm shadow-sm">
              Apply
            </button>
          </form>
        </div>

        <div className="overflow-x-auto bg-white rounded-xl shadow-md border-2 border-slate-800">
          <table className="w-full text-center border-collapse min-w-[900px]">
            <thead>
              <tr className="bg-slate-900 text-white">
                <th className="border-b-2 border-r-2 border-slate-800 p-4 font-black w-32 tracking-wider text-sm text-teal-300">TIME</th>
                {days.map(d => (
                  <th key={d.id} className="border-b-2 border-r-2 border-slate-800 p-4 font-black tracking-wider text-sm">{d.name}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {timeslots.map((slot, rowIndex) => {
                return (
                  <tr key={slot.start} className="transition-colors hover:bg-slate-50">
                    <td className="border-b border-r-2 border-slate-800 p-3 font-black whitespace-nowrap bg-slate-100 text-slate-800 text-sm">
                      {formatTime12h(slot.start)} TO {formatTime12h(slot.end)}
                    </td>
                    {days.map(d => {
                      const cellKey = `${d.id}-${slot.start}`
                      if (skipCells.has(cellKey)) {
                        return null 
                      }

                      const cellEntries = getCellEntries(d.id, slot.start, slot.end)
                      
                      let rowSpan = 1
                      if (cellEntries.length > 0) {
                        rowSpan = getRowSpan(cellEntries[0].startTime, cellEntries[0].endTime)
                        if (rowSpan > 1) {
                          let nextSlotIdx = rowIndex + 1
                          while (nextSlotIdx < rowIndex + rowSpan && nextSlotIdx < timeslots.length) {
                            skipCells.add(`${d.id}-${timeslots[nextSlotIdx].start}`)
                            nextSlotIdx++
                          }
                        }
                      }

                      return (
                        <td 
                          key={d.id} 
                          rowSpan={rowSpan}
                          className={`border-b border-r border-slate-200 p-3 align-top text-sm relative ${cellEntries.length > 0 ? 'bg-white' : 'bg-slate-50'}`}
                        >
                          {cellEntries.length === 0 ? (
                            <span className="text-slate-300 font-bold">-</span>
                          ) : (
                            <div className="space-y-4 flex flex-col items-center justify-center h-full">
                              {cellEntries.map((ce, idx) => (
                                <div key={ce.id} className="font-bold text-slate-800 leading-tight w-full px-2">
                                  <div className="text-base">{ce.subject} 
                                    {ce.batch !== 'ALL' && <span className="ml-1.5 px-1.5 py-0.5 rounded text-[10px] bg-teal-100 text-teal-800 tracking-wider align-middle">{ce.batch}</span>} 
                                  </div>
                                  {ce.faculty && <span className="block text-slate-500 font-semibold mt-1 text-xs">Prof. {ce.faculty}</span>} 
                                  {ce.classroom && <span className="block text-slate-400 font-medium text-xs mt-0.5">Room {ce.classroom}</span>}
                                  {idx < cellEntries.length - 1 && <div className="border-b border-slate-100 w-3/4 mx-auto mt-4"></div>}
                                </div>
                              ))}
                            </div>
                          )}
                        </td>
                      )
                    })}
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  )
}
