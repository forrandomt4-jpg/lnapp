'use client'

import Link from 'next/link'
import { motion } from 'framer-motion'
import { Clock, MapPin, User, LogOut } from 'lucide-react'
import { Countdown } from '@/components/Countdown' 
import { NotificationTester } from '@/components/NotificationTester'

export function DashboardUI({ student, current, next, logoutAction }: any) {
  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  }

  const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0, transition: { type: "spring" as const, stiffness: 300, damping: 24 } }
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans selection:bg-teal-200 text-slate-900">
      <header className="bg-white border-b border-slate-200 px-6 py-4 flex justify-between items-center sticky top-0 z-10 shadow-sm">
        <div>
          <h1 className="text-2xl font-black text-slate-800 tracking-tight">LectureNow</h1>
          <p className="text-sm font-bold text-slate-500">Sem 3 &bull; CE &bull; {student.batch}</p>
        </div>
        <div className="flex items-center gap-6">
          <Link href="/timetable" className="text-sm font-bold text-teal-600 hover:text-teal-700 transition-colors">
            Full Timetable
          </Link>
          <form action={logoutAction}>
            <button type="submit" className="text-slate-400 hover:text-red-500 transition-colors" title="Sign out">
              <LogOut size={20} />
            </button>
          </form>
        </div>
      </header>

      <motion.main 
        variants={container}
        initial="hidden"
        animate="show"
        className="flex-1 p-4 md:p-6 max-w-4xl mx-auto w-full space-y-8 mt-2"
      >
        <motion.section variants={item} className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
            <div className="flex items-center gap-5">
              <div className="w-16 h-16 bg-teal-50 border border-teal-100 text-teal-600 rounded-2xl flex items-center justify-center font-black text-3xl shadow-sm">
                {student.name.charAt(0)}
              </div>
              <div>
                <h2 className="text-2xl font-bold text-slate-800">{student.name}</h2>
                <p className="text-slate-500 font-medium tracking-wide">{student.enrollmentNumber}</p>
              </div>
            </div>
          </div>
        </motion.section>

        <div className="grid gap-8 md:grid-cols-2">
          <motion.section variants={item}>
            <div className="flex items-center gap-2 mb-4">
              <div className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse shadow-[0_0_8px_rgba(16,185,129,0.5)]"></div>
              <h3 className="text-sm font-black text-slate-500 uppercase tracking-wider">
                Current Lecture
              </h3>
            </div>
            {current ? (
              <LectureCard lecture={current} type="current" />
            ) : (
              <div className="bg-slate-100 border-2 border-dashed border-slate-200 rounded-2xl p-8 text-center text-slate-500 font-bold h-64 flex items-center justify-center">
                No lecture currently active.
              </div>
            )}
          </motion.section>

          <motion.section variants={item}>
            <h3 className="text-sm font-black text-slate-500 uppercase tracking-wider mb-4">
              Next Lecture
            </h3>
            {next ? (
              <LectureCard lecture={next} type="next" />
            ) : (
              <div className="bg-slate-100 border-2 border-dashed border-slate-200 rounded-2xl p-8 text-center text-slate-500 font-bold h-64 flex items-center justify-center">
                No more lectures today!
              </div>
            )}
          </motion.section>
        </div>
      </motion.main>
    </div>
  )
}

function LectureCard({ lecture, type }: { lecture: any; type: 'current' | 'next' }) {
  const isCurrent = type === 'current'
  
  return (
    <motion.div 
      whileHover={{ y: -4 }}
      className={`rounded-2xl p-6 border-2 flex flex-col h-full ${isCurrent ? 'bg-slate-900 border-slate-800 text-white shadow-xl' : 'bg-white border-slate-200 text-slate-900 shadow-sm'}`}
    >
      <div className="flex justify-between items-start mb-6">
        <h4 className={`text-3xl font-black tracking-tight ${isCurrent ? 'text-teal-300' : 'text-slate-800'}`}>{lecture.subject}</h4>
        {lecture.isChanged && (
          <span className={`text-xs font-bold px-2.5 py-1 rounded-md uppercase tracking-wider ${isCurrent ? 'bg-rose-500/20 text-rose-300 border border-rose-500/30' : 'bg-rose-50 text-rose-600 border border-rose-100'}`}>
            Updated
          </span>
        )}
      </div>
      
      <div className="space-y-4 mb-8 flex-1">
        {lecture.classroom && (
          <div className="flex items-center gap-4 font-medium">
            <div className={`p-2.5 rounded-xl ${isCurrent ? 'bg-slate-800 text-teal-400' : 'bg-teal-50 text-teal-600'}`}>
              <MapPin size={20} />
            </div>
            <span className="text-lg">Room {lecture.classroom}</span>
          </div>
        )}
        {lecture.faculty && (
          <div className="flex items-center gap-4 font-medium">
            <div className={`p-2.5 rounded-xl ${isCurrent ? 'bg-slate-800 text-teal-400' : 'bg-teal-50 text-teal-600'}`}>
              <User size={20} />
            </div>
            <span className="text-lg">{lecture.faculty}</span>
          </div>
        )}
        <div className="flex items-center gap-4 font-medium">
          <div className={`p-2.5 rounded-xl ${isCurrent ? 'bg-slate-800 text-teal-400' : 'bg-teal-50 text-teal-600'}`}>
            <Clock size={20} />
          </div>
          <span className="text-lg">{lecture.startTime} - {lecture.endTime}</span>
        </div>
      </div>
      
      <div className={`pt-5 border-t-2 mt-auto ${isCurrent ? 'border-slate-800' : 'border-slate-100'}`}>
        <Countdown 
          targetTime={isCurrent ? lecture.endTime : lecture.startTime} 
          isCurrent={isCurrent} 
          targetDayOfWeek={lecture.dayOfWeek}
        />
      </div>
    </motion.div>
  )
}
