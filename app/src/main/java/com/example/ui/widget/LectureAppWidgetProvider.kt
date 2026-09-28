package com.example.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.model.Batch
import com.example.data.model.LectureScheduleState
import com.example.data.model.Student
import com.example.data.repository.TimetableRepository
import com.example.data.security.SecurePreferences

class LectureAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, LectureAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_lecture)

            val securePrefs = SecurePreferences(context)
            val repository = TimetableRepository(context)

            val batchStr = securePrefs.getSavedBatch() ?: "CP1"
            val batch = try { Batch.valueOf(batchStr) } catch (e: Exception) { Batch.CP1 }
            val student = Student(
                id = securePrefs.getSavedStudentId() ?: "STU001",
                enrollmentNumber = securePrefs.getSavedEnrollmentNumber() ?: "250610107001",
                name = securePrefs.getSavedStudentName() ?: "Student",
                branch = securePrefs.getSavedBranch(),
                semester = securePrefs.getSavedSemester(),
                batch = batch,
                email = "student@gecp.ac.in"
            )

            val result = repository.getCurrentLectureResult(student)
            val ongoing = result.currentLecture
            val next = result.nextLecture

            views.setTextViewText(R.id.widget_batch, batch.name)

            when {
                // 1. Ongoing Lecture -> Live Countdown to End of Period
                ongoing != null -> {
                    views.setTextViewText(R.id.widget_state_badge, "● LIVE NOW")
                    views.setTextColor(R.id.widget_state_badge, Color.parseColor("#4D7C0F")) // Static Lime Dark

                    val remainingMillis = ongoing.remainingSeconds * 1000L
                    val baseTime = SystemClock.elapsedRealtime() + remainingMillis
                    views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
                    views.setChronometer(R.id.widget_chronometer, baseTime, "%s", true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        views.setChronometerCountDown(R.id.widget_chronometer, true)
                    }

                    views.setTextViewText(R.id.widget_subject_room, "${ongoing.subject.shortName} • Room ${ongoing.effectiveClassroom.code}")
                    views.setTextViewText(R.id.widget_time_countdown, "Ends at ${ongoing.slot.endTime} • Faculty: ${ongoing.effectiveFaculty.shortCode}")

                    if (next != null) {
                        views.setTextViewText(R.id.widget_next_sneak_peek, "Up next: ${next.subject.shortName} in ${next.effectiveClassroom.code} (${next.slot.startTime})")
                    } else {
                        views.setTextViewText(R.id.widget_next_sneak_peek, "Final lecture scheduled for today")
                    }
                }

                // 2. Starting Soon (<=10 minutes) -> Amber countdown
                next != null && next.untilStartSeconds <= 600 -> {
                    views.setTextViewText(R.id.widget_state_badge, "⚡ STARTING SOON")
                    views.setTextColor(R.id.widget_state_badge, Color.parseColor("#D97706")) // Amber warning

                    val remainingMillis = next.untilStartSeconds * 1000L
                    val baseTime = SystemClock.elapsedRealtime() + remainingMillis
                    views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
                    views.setChronometer(R.id.widget_chronometer, baseTime, "%s", true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        views.setChronometerCountDown(R.id.widget_chronometer, true)
                    }

                    views.setTextViewText(R.id.widget_subject_room, "${next.subject.shortName} • Room ${next.effectiveClassroom.code}")
                    views.setTextViewText(R.id.widget_time_countdown, "Starts at ${next.slot.startTime} • Faculty: ${next.effectiveFaculty.shortCode}")
                    views.setTextViewText(R.id.widget_next_sneak_peek, "Starts in ${next.formattedUntilStart}")
                }

                // 3. Upcoming Further Out (>10 minutes) -> Static Cyan countdown
                next != null && next.untilStartSeconds > 600 && result.state == LectureScheduleState.NEXT_UPCOMING -> {
                    views.setTextViewText(R.id.widget_state_badge, "UPCOMING NEXT")
                    views.setTextColor(R.id.widget_state_badge, Color.parseColor("#0E7490")) // Static Cyan Dark

                    val remainingMillis = next.untilStartSeconds * 1000L
                    val baseTime = SystemClock.elapsedRealtime() + remainingMillis
                    views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
                    views.setChronometer(R.id.widget_chronometer, baseTime, "%s", true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        views.setChronometerCountDown(R.id.widget_chronometer, true)
                    }

                    views.setTextViewText(R.id.widget_subject_room, "${next.subject.shortName} • Room ${next.effectiveClassroom.code}")
                    views.setTextViewText(R.id.widget_time_countdown, "Starts at ${next.slot.startTime} • Faculty: ${next.effectiveFaculty.shortCode}")
                    views.setTextViewText(R.id.widget_next_sneak_peek, "Starts in ${next.formattedUntilStart}")
                }

                // 4. Breaks / Day Completed / Sunday / Other
                else -> {
                    val statusBadge: String
                    val statusColor: Int
                    val targetSeconds: Long

                    when (result.state) {
                        LectureScheduleState.LUNCH -> {
                            statusBadge = "🍽️ LUNCH BREAK"
                            statusColor = Color.parseColor("#047857")
                            targetSeconds = maxOf(0L, repository.parseTimeSeconds("13:00") - repository.getEffectiveTimeSeconds())
                        }
                        LectureScheduleState.BREAK -> {
                            statusBadge = "☕ RECESS BREAK"
                            statusColor = Color.parseColor("#B45309")
                            targetSeconds = maxOf(0L, repository.parseTimeSeconds("15:10") - repository.getEffectiveTimeSeconds())
                        }
                        LectureScheduleState.DAY_COMPLETED -> {
                            statusBadge = "✨ CLASSES COMPLETED"
                            statusColor = Color.parseColor("#475569")
                            targetSeconds = next?.untilStartSeconds ?: 0L
                        }
                        LectureScheduleState.SUNDAY -> {
                            statusBadge = "🏖️ SUNDAY RECESS"
                            statusColor = Color.parseColor("#475569")
                            targetSeconds = next?.untilStartSeconds ?: 0L
                        }
                        else -> {
                            statusBadge = "CAMPUS SCHEDULE"
                            statusColor = Color.parseColor("#475569")
                            targetSeconds = next?.untilStartSeconds ?: 0L
                        }
                    }

                    views.setTextViewText(R.id.widget_state_badge, statusBadge)
                    views.setTextColor(R.id.widget_state_badge, statusColor)

                    if (targetSeconds > 0) {
                        val baseTime = SystemClock.elapsedRealtime() + (targetSeconds * 1000L)
                        views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
                        views.setChronometer(R.id.widget_chronometer, baseTime, "%s", true)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            views.setChronometerCountDown(R.id.widget_chronometer, true)
                        }
                    } else {
                        views.setViewVisibility(R.id.widget_chronometer, View.GONE)
                    }

                    if (next != null) {
                        views.setTextViewText(R.id.widget_subject_room, "Next: ${next.subject.shortName} • Room ${next.effectiveClassroom.code}")
                        views.setTextViewText(R.id.widget_time_countdown, "${next.slot.day.shortName} at ${next.slot.startTime} (${next.effectiveFaculty.shortCode})")
                        views.setTextViewText(R.id.widget_next_sneak_peek, "Starts in ${next.formattedUntilStart}")
                    } else {
                        views.setTextViewText(R.id.widget_subject_room, "No classes in progress")
                        views.setTextViewText(R.id.widget_time_countdown, "Check Timetable for upcoming sessions")
                        views.setTextViewText(R.id.widget_next_sneak_peek, "Tap to open LectureNow")
                    }
                }
            }

            // Pending intent to open MainActivity on widget tap
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
