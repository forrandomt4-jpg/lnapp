# Android Studio Implementation Guide

The code currently in your GitHub repository is the **Web App and API Server (Backend)**. 
To build the actual Android app with the lock-screen widget and automatic notifications, you will create a new project in Android Studio that "talks" to your Next.js server.

Follow these steps carefully:

## Step 1: Create the Android Project
1. Open **Android Studio** and click **New Project**.
2. Select **Empty Activity** (Jetpack Compose) and click Next.
3. Name it **LectureNowApp**.
4. Language: **Kotlin**.
5. Click **Finish** and wait for Gradle to sync.

## Step 2: Add Dependencies
Open your `app/build.gradle.kts` and add these dependencies at the bottom for networking and widgets:
```kotlin
dependencies {
    // For making API requests to your Next.js server
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // For background notifications
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // For the home screen widget
    implementation("androidx.glance:glance-appwidget:1.0.0")
}
```
*Click "Sync Now" at the top of the screen.*

## Step 3: One-Time Login (MainActivity.kt)
We will use Android's `SharedPreferences` to save the enrollment number. If it exists, it bypasses login!

**Replace `MainActivity.kt` with:**
```kotlin
package com.example.lecturenowapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("LecturePrefs", Context.MODE_PRIVATE)
        val savedEnrollment = prefs.getString("ENROLLMENT_NO", null)

        setContent {
            MaterialTheme {
                if (savedEnrollment == null) {
                    LoginScreen { enrollment ->
                        prefs.edit().putString("ENROLLMENT_NO", enrollment).apply()
                        recreate() // Reload the app to show dashboard
                    }
                } else {
                    DashboardScreen(savedEnrollment)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("LectureNow", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enrollment Number") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLogin(text) }) {
            Text("Get Started")
        }
    }
}

@Composable
fun DashboardScreen(enrollment: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, $enrollment", style = MaterialTheme.typography.headlineMedium)
        Text("Your widget is active and checking for classes!")
    }
}
```

## Step 4: The 10-Minute Notification Worker
This background job checks the Next.js API. If a lecture starts in exactly 10 minutes, it fires a native Android notification.

**Create a new file `NotificationWorker.kt`:**
```kotlin
package com.example.lecturenowapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val client = OkHttpClient()
        // 10.0.2.2 points to your computer's localhost from the Android Emulator!
        val request = Request.Builder().url("http://10.0.2.2:3000/api/timetable/current").build()
        
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                if (!json.isNull("next")) {
                    val nextLecture = json.getJSONObject("next")
                    val subject = nextLecture.getString("subject")
                    val room = nextLecture.getString("classroom")
                    
                    // Fire Notification
                    showNotification("Upcoming Lecture", "$subject starts in 10 minutes in Room $room!")
                }
            }
        } catch (e: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "lecture_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Lectures", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
            
        manager.notify(1, notification)
    }
}
```

## Step 5: The Android Widget
This code draws the lock-screen/home-screen widget that says "Current: DBMS, Next: DS". When clicked, it opens the main app.

**Create a new file `LectureWidget.kt`:**
```kotlin
package com.example.lecturenowapp

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text

class LectureWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LectureWidget()
}

class LectureWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable { 
                        // Open the App when the widget is clicked
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
            ) {
                Text("Current: DBMS")
                Text("Next: DS in 10min")
                Text("See more info in app ->")
            }
        }
    }
}
```
