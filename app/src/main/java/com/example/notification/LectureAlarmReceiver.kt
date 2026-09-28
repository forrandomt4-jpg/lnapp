package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LectureAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            LectureNotificationManager.ACTION_LECTURE_REMINDER -> {
                val lectureId = intent.getStringExtra(LectureNotificationManager.EXTRA_LECTURE_ID) ?: "LECTURE"
                val subject = intent.getStringExtra(LectureNotificationManager.EXTRA_SUBJECT) ?: "Class"
                val room = intent.getStringExtra(LectureNotificationManager.EXTRA_ROOM) ?: "Campus"
                val faculty = intent.getStringExtra(LectureNotificationManager.EXTRA_FACULTY) ?: "Faculty"

                Log.i("LectureAlarmReceiver", "Alarm fired! Showing 10-minute notification for $subject in $room")
                LectureNotificationManager.showNotification(
                    context = context,
                    subject = subject,
                    room = room,
                    faculty = faculty,
                    lectureId = lectureId
                )
            }
            LectureNotificationManager.ACTION_BREAK_REMINDER -> {
                val title = intent.getStringExtra(LectureNotificationManager.EXTRA_BREAK_TITLE) ?: "Campus Break"
                val message = intent.getStringExtra(LectureNotificationManager.EXTRA_BREAK_MESSAGE) ?: "Recess or Lunch Break in progress."

                Log.i("LectureAlarmReceiver", "Break alarm fired! Showing $title")
                LectureNotificationManager.showBreakNotification(
                    context = context,
                    title = title,
                    message = message
                )
            }
        }
    }
}
