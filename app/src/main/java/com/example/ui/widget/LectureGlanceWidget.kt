package com.example.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.model.Batch
import com.example.data.model.LectureScheduleState
import com.example.data.model.Student
import com.example.data.repository.TimetableRepository
import com.example.data.security.SecurePreferences

class LectureGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LectureGlanceWidget()
}

class LectureGlanceWidget : GlanceAppWidget() {

    enum class WidgetState {
        ONGOING,
        STARTING_SOON,
        UPCOMING_FAR,
        EMPTY_OR_FREE
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
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

        val widgetState = when {
            ongoing != null -> WidgetState.ONGOING
            next != null && next.untilStartSeconds <= 600 -> WidgetState.STARTING_SOON
            next != null -> WidgetState.UPCOMING_FAR
            else -> WidgetState.EMPTY_OR_FREE
        }

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF)) // Pure Clean White
                    .padding(14.dp)
                    .clickable {
                        val launchIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        context.startActivity(launchIntent)
                    }
            ) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Header Bar: App Name & Batch Pill
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LECTURENOW",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFF0F172A)), // Slate Black
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        Text(
                            text = batch.name,
                            style = TextStyle(
                                color = ColorProvider(Color(0xFF0E7490)), // Static Cyan Dark
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    when (widgetState) {
                        WidgetState.ONGOING -> {
                            val lec = ongoing!!
                            Text(
                                text = "● LIVE NOW",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF4D7C0F)), // Static Lime Dark
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = "${lec.subject.shortName} • Room ${lec.effectiveClassroom.code}",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF0F172A)),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = "Ends in ${lec.formattedRemaining} (${lec.effectiveFaculty.shortCode})",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF334155)),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            if (next != null) {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                                Text(
                                    text = "Up next: ${next.subject.shortName} in ${next.effectiveClassroom.code} (${next.slot.startTime})",
                                    style = TextStyle(
                                        color = ColorProvider(Color(0xFF0E7490)),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        WidgetState.STARTING_SOON -> {
                            val nxt = next!!
                            Text(
                                text = "⚡ STARTING SOON",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFFD97706)), // Amber Warning
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = "${nxt.subject.shortName} in Room ${nxt.effectiveClassroom.code}",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF0F172A)),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = "Starts in ${nxt.formattedUntilStart} • ${nxt.effectiveFaculty.shortCode}",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFFB45309)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        WidgetState.UPCOMING_FAR -> {
                            val nxt = next!!
                            Text(
                                text = "UPCOMING NEXT",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF0E7490)), // Static Cyan
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = "${nxt.subject.shortName} • Room ${nxt.effectiveClassroom.code}",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF0F172A)),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            Text(
                                text = "Starts at ${nxt.slot.startTime} (${nxt.formattedUntilStart})",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF475569)),
                                    fontSize = 12.sp
                                )
                            )
                        }

                        WidgetState.EMPTY_OR_FREE -> {
                            val reason = when (result.state) {
                                LectureScheduleState.LUNCH -> "🍽️ Lunch Break (12:30 - 1:00 PM)"
                                LectureScheduleState.BREAK -> "☕ Recess Break (3:00 - 3:10 PM)"
                                LectureScheduleState.DAY_COMPLETED -> "✨ All lectures completed for today"
                                LectureScheduleState.SUNDAY -> "🏖️ Sunday — Campus closed"
                                LectureScheduleState.HOLIDAY -> "🎉 Holiday — No lectures"
                                else -> "No more scheduled classes"
                            }

                            Text(
                                text = "CAMPUS SCHEDULE",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF475569)),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = reason,
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF0F172A)),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            if (next != null) {
                                Spacer(modifier = GlanceModifier.height(4.dp))
                                Text(
                                    text = "Next: ${next.subject.shortName} (${next.slot.day.shortName} at ${next.slot.startTime})",
                                    style = TextStyle(
                                        color = ColorProvider(Color(0xFF0E7490)),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        suspend fun updateWidget(context: Context) {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(LectureGlanceWidget::class.java)
                for (glanceId in glanceIds) {
                    LectureGlanceWidget().update(context, glanceId)
                }
            } catch (e: Exception) {
                // Glance may not have active widget on screen yet
            }
        }
    }
}
