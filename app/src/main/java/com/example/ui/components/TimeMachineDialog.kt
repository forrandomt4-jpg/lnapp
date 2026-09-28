package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DayOfWeek
import com.example.data.repository.SimulationTime

data class TimePreset(
    val label: String,
    val day: DayOfWeek,
    val hour: Int,
    val minute: Int,
    val note: String
)

@Composable
fun TimeMachineDialog(
    timeState: SimulationTime,
    onDismiss: () -> Unit,
    onApplySimulation: (DayOfWeek, Int, Int) -> Unit,
    onResetRealTime: () -> Unit
) {
    var selectedDay by remember { mutableStateOf(timeState.simulatedDay) }
    var selectedHour by remember { mutableIntStateOf(timeState.simulatedHour) }
    var selectedMinute by remember { mutableIntStateOf(timeState.simulatedMinute) }

    val presets = remember {
        listOf(
            TimePreset("Mon 09:20 AM", DayOfWeek.MONDAY, 9, 20, "CS301 DSA Lecture Ongoing (Ends in 40m)"),
            TimePreset("Mon 11:05 AM", DayOfWeek.MONDAY, 11, 5, "Short Break (Next: CS304 at 11:15)"),
            TimePreset("Tue 11:30 AM", DayOfWeek.TUESDAY, 11, 30, "Room Change Active (C302 -> C204)"),
            TimePreset("Wed 10:15 AM", DayOfWeek.WEDNESDAY, 10, 15, "Substitute: Prof Kunal Verma for DSA"),
            TimePreset("Thu 01:30 PM", DayOfWeek.THURSDAY, 13, 30, "Campus Lunch Break (1:15 - 2:00 PM)"),
            TimePreset("Fri 02:30 PM", DayOfWeek.FRIDAY, 14, 30, "CS305 Java OOP in LH1 Amphitheatre"),
            TimePreset("Sat 10:00 AM", DayOfWeek.SATURDAY, 10, 0, "Weekend Tutorial Session"),
            TimePreset("Sun 12:00 PM", DayOfWeek.SUNDAY, 12, 0, "Sunday — College Closed")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Time Simulator",
                        tint = Color(0xFFD97706)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Time Simulator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Test timetable behaviors, live countdowns, room substitutions, breaks, and weekends at any time of day.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("preset_${preset.day.name}_${preset.hour}")
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedDay = preset.day
                                    selectedHour = preset.hour
                                    selectedMinute = preset.minute
                                    onApplySimulation(preset.day, preset.hour, preset.minute)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.label,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = preset.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Apply Preset",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Custom Day & Time",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Day selector chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(DayOfWeek.values()) { day ->
                        val isSelected = selectedDay == day
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedDay = day }
                        ) {
                            Text(
                                text = day.shortName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hour Slider
                Text(
                    text = "Hour: ${String.format("%02d:00", selectedHour)} (${if (selectedHour >= 12) "${if (selectedHour == 12) 12 else selectedHour - 12} PM" else "${if (selectedHour == 0) 12 else selectedHour} AM"})",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                Slider(
                    value = selectedHour.toFloat(),
                    onValueChange = { selectedHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )

                // Minute Slider
                Text(
                    text = "Minute: ${String.format("%02d min", selectedMinute)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                Slider(
                    value = selectedMinute.toFloat(),
                    onValueChange = { selectedMinute = (it / 5).toInt() * 5 },
                    valueRange = 0f..55f,
                    steps = 10
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplySimulation(selectedDay, selectedHour, selectedMinute)
                },
                modifier = Modifier.testTag("apply_simulation_button")
            ) {
                Text("Apply Time")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onResetRealTime,
                modifier = Modifier.testTag("reset_real_time_button")
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Use Real Clock")
            }
        }
    )
}
