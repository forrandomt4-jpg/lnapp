'use client'

import { motion } from 'framer-motion'
import Link from 'next/link'

export function LoginForm({ loginAction }: { loginAction: (formData: FormData) => void }) {
  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, ease: "easeOut" }}
      className="max-w-md w-full bg-white rounded-2xl shadow-xl p-8 border border-gray-100"
    >
      <div className="text-center mb-8">
        <motion.div 
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2, type: "spring", stiffness: 200 }}
          className="w-16 h-16 bg-teal-50 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-sm border border-teal-100"
        >
          <span className="text-2xl font-black text-teal-600">LN</span>
        </motion.div>
        <h1 className="text-3xl font-black text-slate-800 tracking-tight">LectureNow</h1>
        <p className="text-slate-500 font-medium mt-1">Semester 3 &bull; Computer Engineering</p>
      </div>
      
      <form action={loginAction} className="space-y-5">
        <div>
          <label htmlFor="enrollmentNumber" className="block text-sm font-bold text-slate-700 mb-1.5 uppercase tracking-wide">
            Enrollment Number
          </label>
          <input
            type="text"
            id="enrollmentNumber"
            name="enrollmentNumber"
            required
            className="w-full px-4 py-3.5 rounded-xl border-2 border-slate-200 focus:ring-0 focus:border-teal-500 outline-none transition-colors font-medium bg-slate-50 text-slate-900"
            placeholder="e.g. 250610107001"
          />
        </div>
        
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          type="submit"
          className="w-full bg-teal-600 text-white font-bold text-lg py-3.5 rounded-xl hover:bg-teal-700 transition-colors shadow-md"
        >
          Get Started
        </motion.button>
      </form>
      
      <div className="mt-8 text-center">
        <Link href="/timetable" className="text-slate-500 hover:text-teal-600 font-bold transition-colors inline-flex items-center gap-2">
          View Full Timetable &rarr;
        </Link>
      </div>
    </motion.div>
  )
}
