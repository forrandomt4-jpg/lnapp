package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.model.Batch
import com.example.data.model.DayOfWeek
import com.example.data.model.SlotStatus
import com.example.data.repository.TimetableRepository
import com.example.data.security.SecurePreferences
import com.example.notification.LectureNotificationManager
import com.example.ui.widget.LectureAppWidgetProvider
import com.example.ui.widget.LectureGlanceWidget
import java.util.Calendar
import java.util.concurrent.TimeUnit

class LectureSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "LectureSyncWorker executing periodic timetable sync & notification rescheduling...")

        try {
            // Reschedule today's 10-min prior notifications and recess/lunch alerts
            LectureNotificationManager.scheduleTodayReminders(appContext)

            // Also refresh widgets
            try {
                LectureGlanceWidget.updateWidget(appContext)
                LectureAppWidgetProvider.updateAllWidgets(appContext)
            } catch (e: Exception) {
                Log.w(TAG, "Widget update during sync: ${e.message}")
            }

            Log.i(TAG, "LectureSyncWorker completed successfully.")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "LectureSyncWorker failed: ${e.message}", e)
            return Result.retry()
        }
    }

    companion object {
        private const val TAG = "LectureSyncWorker"
        const val WORK_NAME = "LectureSyncWorkerPeriodic"

        fun enqueuePeriodicSync(context: Context) {
            val syncRequest = PeriodicWorkRequestBuilder<LectureSyncWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES // flex interval
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                syncRequest
            )
            Log.i(TAG, "Enqueued periodic timetable sync work (every 15 min)")
        }
    }
}
