package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.model.Batch
import com.example.data.repository.TimetableRepository
import com.example.data.security.SecurePreferences
import java.util.Calendar
import kotlin.math.abs

object LectureNotificationManager {

    const val CHANNEL_ID = "lecture_reminders"
    const val CHANNEL_NAME = "Lecture Reminders"
    const val CHANNEL_DESC = "Timetable notifications 10 minutes before scheduled lectures & recess"

    const val ACTION_LECTURE_REMINDER = "com.example.lecturenow.ACTION_LECTURE_REMINDER"
    const val ACTION_BREAK_REMINDER = "com.example.lecturenow.ACTION_BREAK_REMINDER"

    const val EXTRA_LECTURE_ID = "extra_lecture_id"
    const val EXTRA_SUBJECT = "extra_subject"
    const val EXTRA_ROOM = "extra_room"
    const val EXTRA_FACULTY = "extra_faculty"
    const val EXTRA_BREAK_TITLE = "extra_break_title"
    const val EXTRA_BREAK_MESSAGE = "extra_break_message"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedules a notification exactly 10 minutes before the lecture begins.
     * Uses AlarmManager.setExactAndAllowWhileIdle with a stable request code.
     */
    fun scheduleLectureReminder(
        context: Context,
        lectureId: String,
        subject: String,
        room: String,
        faculty: String,
        startTimeEpochMillis: Long
    ) {
        val tenMinutesBeforeMillis = startTimeEpochMillis - (10 * 60 * 1000)
        val now = System.currentTimeMillis()

        if (tenMinutesBeforeMillis <= now) {
            Log.d("LectureNotification", "Skipping alarm in past for $subject (alarm: $tenMinutesBeforeMillis, now: $now)")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, LectureAlarmReceiver::class.java).apply {
            action = ACTION_LECTURE_REMINDER
            putExtra(EXTRA_LECTURE_ID, lectureId)
            putExtra(EXTRA_SUBJECT, subject)
            putExtra(EXTRA_ROOM, room)
            putExtra(EXTRA_FACULTY, faculty)
        }

        val requestCode = abs("$lectureId-${startTimeEpochMillis / 86400000}".hashCode())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tenMinutesBeforeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tenMinutesBeforeMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tenMinutesBeforeMillis,
                    pendingIntent
                )
            }
            Log.i("LectureNotification", "Scheduled 10-min alarm for $subject in Room $room at $tenMinutesBeforeMillis")
        } catch (e: SecurityException) {
            Log.w("LectureNotification", "Exact alarm permission missing, falling back to inexact: ${e.message}")
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tenMinutesBeforeMillis,
                pendingIntent
            )
        }
    }

    /**
     * Schedules a notification for Recess (3:00 PM) or Lunch (12:30 PM).
     */
    fun scheduleBreakReminder(
        context: Context,
        breakTitle: String,
        breakMessage: String,
        breakEpochMillis: Long
    ) {
        val now = System.currentTimeMillis()
        if (breakEpochMillis <= now) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, LectureAlarmReceiver::class.java).apply {
            action = ACTION_BREAK_REMINDER
            putExtra(EXTRA_BREAK_TITLE, breakTitle)
            putExtra(EXTRA_BREAK_MESSAGE, breakMessage)
        }

        val requestCode = abs("$breakTitle-${breakEpochMillis / 86400000}".hashCode())

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                breakEpochMillis,
                pendingIntent
            )
            Log.i("LectureNotification", "Scheduled break reminder for $breakTitle at $breakEpochMillis")
        } catch (e: Exception) {
            Log.w("LectureNotification", "Failed to schedule break reminder: ${e.message}")
        }
    }

    /**
     * Reschedules all upcoming lecture (10-min prior) and recess/lunch break notifications for today.
     */
    fun scheduleTodayReminders(context: Context) {
        try {
            val securePrefs = SecurePreferences(context)
            val batchStr = securePrefs.getSavedBatch() ?: "CP1"
            val studentBatch = try { Batch.valueOf(batchStr) } catch (e: Exception) { Batch.CP1 }

            val repository = TimetableRepository(context)
            val todaySlots = repository.getFullScheduleForDayAndBatch(
                repository.getEffectiveDay(),
                studentBatch
            )

            val nowCalendar = Calendar.getInstance()

            // 1. Lecture 10-Minute Prior Alarms
            for (lecture in todaySlots) {
                if (lecture.isCancelled) continue

                val timeParts = lecture.slot.startTime.split(":")
                val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 10
                val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 30

                val lectureStartCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val startEpochMillis = lectureStartCal.timeInMillis
                if (startEpochMillis > nowCalendar.timeInMillis) {
                    scheduleLectureReminder(
                        context = context,
                        lectureId = lecture.slot.id,
                        subject = lecture.subject.shortName,
                        room = lecture.effectiveClassroom.code,
                        faculty = lecture.effectiveFaculty.shortCode,
                        startTimeEpochMillis = startEpochMillis
                    )
                }
            }

            // 2. Lunch Break Alarm (12:30 PM)
            val lunchCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 30)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (lunchCal.timeInMillis > nowCalendar.timeInMillis) {
                scheduleBreakReminder(
                    context = context,
                    breakTitle = "🍽️ Lunch Break (12:30 PM - 1:00 PM)",
                    breakMessage = "Campus lunch break started. Next period begins at 1:00 PM.",
                    breakEpochMillis = lunchCal.timeInMillis
                )
            }

            // 3. Recess Break Alarm (3:00 PM)
            val recessCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 15)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (recessCal.timeInMillis > nowCalendar.timeInMillis) {
                scheduleBreakReminder(
                    context = context,
                    breakTitle = "☕ Recess Break (3:00 PM - 3:10 PM)",
                    breakMessage = "Recess break in progress. Afternoon lab/period starts at 3:10 PM.",
                    breakEpochMillis = recessCal.timeInMillis
                )
            }
        } catch (e: Exception) {
            Log.e("LectureNotification", "Failed to schedule today's reminders: ${e.message}", e)
        }
    }

    /**
     * Renders and dispatches the native NotificationCompat reminder.
     */
    fun showNotification(
        context: Context,
        subject: String,
        room: String,
        faculty: String,
        lectureId: String
    ) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Upcoming Lecture: $subject"
        val message = "$subject begins in 10 minutes in Room $room ($faculty)"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\nTap to open timetable and see room navigation."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = abs(lectureId.hashCode())
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Renders and dispatches break notifications (Recess / Lunch).
     */
    fun showBreakNotification(
        context: Context,
        title: String,
        message: String
    ) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = abs(title.hashCode())
        notificationManager.notify(notificationId, notification)
    }
}
