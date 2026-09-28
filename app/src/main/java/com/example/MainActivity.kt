package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.notification.LectureNotificationManager
import com.example.ui.LectureNowApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LectureNowViewModel
import com.example.ui.viewmodel.NavTab
import com.example.ui.widget.LectureAppWidgetProvider
import com.example.ui.widget.LectureGlanceWidget
import com.example.worker.LectureSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: LectureNowViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Log.d(TAG, "POST_NOTIFICATIONS permission granted: $isGranted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // True Edge-to-Edge: fullscreen status bar & navigation bar merging
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // 1. Setup notification channel
        LectureNotificationManager.createNotificationChannel(this)

        // 2. Request notification permission on Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 3. Enqueue periodic background sync & schedule today's reminders
        LectureNotificationManager.scheduleTodayReminders(this)
        LectureSyncWorker.enqueuePeriodicSync(this)

        // 4. Handle incoming Deep Link
        handleIncomingIntent(intent)

        setContent {
            MyApplicationTheme {
                LectureNowApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Refresh notifications & widgets whenever app is resumed
        try {
            LectureNotificationManager.scheduleTodayReminders(this)
            LectureAppWidgetProvider.updateAllWidgets(this)
            CoroutineScope(Dispatchers.IO).launch {
                LectureGlanceWidget.updateWidget(applicationContext)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Widget update on resume: ${e.message}")
        }
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme?.equals("lecturenow", ignoreCase = true) == true) {
            if (data.host?.equals("timetable", ignoreCase = true) == true) {
                viewModel.setNavTab(NavTab.TIMETABLE)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
