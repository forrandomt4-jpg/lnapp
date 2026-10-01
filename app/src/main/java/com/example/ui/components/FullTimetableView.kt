package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Batch
import com.example.data.model.DayOfWeek
import com.example.data.model.ResolvedLecture

enum class TimetableViewMode {
    OFFICIAL_GRID,
    DAY_CARDS
}

data class SubjectTheme(
    val bg: Color,
    val text: Color,
    val border: Color
)

fun getSubjectTheme(code: String): SubjectTheme {
    return when (code.trim().uppercase()) {
        "DS" -> SubjectTheme(
            bg = Color(0xFFEFF6FF), // soft pastel blue
            text = Color(0xFF1D4ED8), // rich blue
            border = Color(0xFFBFDBFE)
        )
        "DBMS" -> SubjectTheme(
            bg = Color(0xFFECFDF5), // soft pastel emerald
            text = Color(0xFF047857), // rich emerald
            border = Color(0xFFA7F3D0)
        )
        "DF" -> SubjectTheme(
            bg = Color(0xFFFFFBEB), // soft pastel amber
            text = Color(0xFFB45309), // rich amber
            border = Color(0xFFFDE68A)
        )
        "PCE" -> SubjectTheme(
            bg = Color(0xFFECFEFF), // soft pastel cyan
            text = Color(0xFF0E7490), // rich cyan
            border = Color(0xFFA5F3FC)
        )
        "PS" -> SubjectTheme(
            bg = Color(0xFFFAF5FF), // soft pastel violet
            text = Color(0xFF6B21A8), // rich violet
            border = Color(0xFFE9D5FF)
        )
        "IC" -> SubjectTheme(
            bg = Color(0xFFEEF2FF), // soft pastel indigo
            text = Color(0xFF4338CA), // rich indigo
            border = Color(0xFFC7D2FE)
        )
        else -> SubjectTheme(
            bg = Color(0xFFF8FAFC), // soft slate
            text = Color(0xFF475569), // rich slate
            border = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun FullTimetableView(
    selectedDay: DayOfWeek,
    selectedBatch: Batch,
    searchQuery: String,
    fullSchedule: List<ResolvedLecture>,
    onDaySelected: (DayOfWeek) -> Unit,
    onBatchSelected: (Batch) -> Unit,
    onSearchChanged: (String) -> Unit,
    onSlotRoomClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(TimetableViewMode.OFFICIAL_GRID) }

    val weekDays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Semester 3 Timetable",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Computer Engineering • GEC Palanpur",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }

            // Grid vs Day Card View Mode Switcher
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (viewMode == TimetableViewMode.OFFICIAL_GRID) Color(0xFF0F172A) else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewMode = TimetableViewMode.OFFICIAL_GRID }
                            .padding(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Table View",
                                tint = if (viewMode == TimetableViewMode.OFFICIAL_GRID) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Table",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (viewMode == TimetableViewMode.OFFICIAL_GRID) Color.White else Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (viewMode == TimetableViewMode.DAY_CARDS) Color(0xFF0F172A) else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewMode = TimetableViewMode.DAY_CARDS }
                            .padding(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ViewList,
                                contentDescription = "Day View",
                                tint = if (viewMode == TimetableViewMode.DAY_CARDS) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Day",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (viewMode == TimetableViewMode.DAY_CARDS) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Batch Filter Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val batches = listOf(Batch.ALL, Batch.CP1, Batch.CP2, Batch.CP3)
            batches.forEach { batch ->
                val isSelected = selectedBatch == batch
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                    modifier = Modifier
                        .clickable { onBatchSelected(batch) }
                        .testTag("batch_chip_${batch.name}")
                ) {
                    Text(
                        text = if (batch == Batch.ALL) "All Batches" else "Batch ${batch.name}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Filter by subject, faculty or room...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF06B6D4),
                unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("timetable_search_field")
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (viewMode == TimetableViewMode.OFFICIAL_GRID) {
            OfficialCollegeTimetableTable(
                highlightBatch = selectedBatch,
                searchQuery = searchQuery
            )
        } else {
            // Day List View
            ScrollableTabRow(
                selectedTabIndex = weekDays.indexOf(selectedDay).coerceAtLeast(0),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                weekDays.forEach { day ->
                    val isSelected = day == selectedDay
                    Tab(
                        selected = isSelected,
                        onClick = { onDaySelected(day) },
                        text = {
                            Text(
                                text = day.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF64748B)
                            )
                        },
                        modifier = Modifier.testTag("day_tab_${day.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val filteredSchedule = fullSchedule.filter { item ->
                if (searchQuery.isBlank()) true
                else {
                    val q = searchQuery.trim().lowercase()
                    item.subject.name.lowercase().contains(q) ||
                            item.subject.code.lowercase().contains(q) ||
                            item.effectiveFaculty.name.lowercase().contains(q) ||
                            item.effectiveFaculty.shortCode.lowercase().contains(q) ||
                            item.effectiveClassroom.name.lowercase().contains(q) ||
                            item.effectiveClassroom.code.lowercase().contains(q)
                }
            }

            if (filteredSchedule.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No lectures match '$searchQuery'" else "No classes scheduled for ${selectedDay.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredSchedule.forEach { lecture ->
                        TimelineSlotCard(
                            lecture = lecture,
                            onSlotRoomClick = onSlotRoomClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * 1:1 Authentic replica of the GEC Palanpur Computer Engineering Dept Sem III timetable sheet.
 * Features:
 * - Strict mathematical column grid: Every day column is strictly locked to dayColWidth (180.dp).
 * - No bleeding or drifting between days.
 * - Horizontal aligned time regulation without cramped vertical stacking.
 * - Clean light static pastel colors for each subject and day header.
 * - Distinct, perfectly aligned Recess and Lunch Break rows spanning the entire table width.
 * - Signatures removed as requested.
 */
@Composable
fun OfficialCollegeTimetableTable(
    highlightBatch: Batch,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    val timeColWidth = 125.dp
    val dayColWidth = 180.dp
    val totalTableWidth = timeColWidth + (dayColWidth * 5) // 1025.dp
    val gridBorderColor = Color(0xFFCBD5E1) // Clean crisp subtle slate border

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header matching Photo exactly
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GOVERNMENT ENGINEERING COLLEGE,PALANPUR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Computer Engineering Department",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Academic Year: 2026-27 Odd",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Academic term : 30/7/2026 TO 15/12/2026",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Sem III",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                // W.E.F. aligned to the right, just like the photo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "W.E.F. 13-8-2026",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Horizontally Scrollable Official Table Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .border(1.5.dp, gridBorderColor, RoundedCornerShape(6.dp))
            ) {
                Column(modifier = Modifier.width(totalTableWidth)) {
                    // =========================================================
                    // HEADER ROW: TIME & DAYS (with light static pastel headers)
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(44.dp)
                    ) {
                        // TIME cell
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(44.dp)
                                .background(Color(0xFFF1F5F9))
                                .border(0.5.dp, gridBorderColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TIME",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                ),
                                color = Color(0xFF334155)
                            )
                        }

                        // Day headers with light pastel colors
                        val dayHeaders = listOf(
                            Triple("MONDAY", Color(0xFFEEF2FF), Color(0xFF3730A3)),
                            Triple("TUESDAY", Color(0xFFECFEFF), Color(0xFF0E7490)),
                            Triple("WEDNESDAY", Color(0xFFECFDF5), Color(0xFF065F46)),
                            Triple("THURSDAY", Color(0xFFFFFBEB), Color(0xFF92400E)),
                            Triple("FRIDAY", Color(0xFFFAF5FF), Color(0xFF6B21A8))
                        )

                        dayHeaders.forEach { (name, bgColor, textColor) ->
                            Box(
                                modifier = Modifier
                                    .width(dayColWidth)
                                    .height(44.dp)
                                    .background(bgColor)
                                    .border(0.5.dp, gridBorderColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = textColor
                                )
                            }
                        }
                    }

                    // =========================================================
                    // ROW 1 & 2: 10:30 TO 12:30 (Morning Lab / Lecture Block)
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(128.dp)
                    ) {
                        // Time Column (Horizontal format on single clean line)
                        Column(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(128.dp)
                                .background(Color(0xFFF8FAFC))
                                .border(0.5.dp, gridBorderColor)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .border(0.5.dp, gridBorderColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "10:30 TO 11:30",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .border(0.5.dp, gridBorderColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "11:30 TO 12:30",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // MONDAY: 10:30-12:30 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("DS CP1 (PGV) (4111)", Batch.CP1, "DS"),
                                Triple("DS CP2 (VF) (4111)", Batch.CP2, "DS"),
                                Triple("DBMS CP3 (RS) (2101)", Batch.CP3, "DBMS")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )

                        // TUESDAY: 10:30-12:30 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("CP1 S.L./LIB.", Batch.CP1, "SL_LIB"),
                                Triple("DBMS CP2 (RS) (4111)", Batch.CP2, "DBMS"),
                                Triple("DS CP3 (PGV) (2101)", Batch.CP3, "DS")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )

                        // WEDNESDAY: Divided into two single periods (64.dp each)
                        Column(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(128.dp)
                        ) {
                            TableSingleEntryCell(
                                mainText = "DS (PGV)",
                                subText = "(8113)",
                                subjectCode = "DS",
                                batch = Batch.ALL,
                                highlightBatch = highlightBatch,
                                searchQuery = searchQuery,
                                width = dayColWidth,
                                height = 64.dp,
                                borderColor = gridBorderColor
                            )
                            TableSingleEntryCell(
                                mainText = "DBMS (RS)",
                                subText = "(8113)",
                                subjectCode = "DBMS",
                                batch = Batch.ALL,
                                highlightBatch = highlightBatch,
                                searchQuery = searchQuery,
                                width = dayColWidth,
                                height = 64.dp,
                                borderColor = gridBorderColor
                            )
                        }

                        // THURSDAY: 10:30-12:30 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("DS CP1 (SDJ) (4111)", Batch.CP1, "DS"),
                                Triple("DS CP2 (VF) (4111)", Batch.CP2, "DS"),
                                Triple("PCE CP3 (SLM) (2101)", Batch.CP3, "PCE")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )

                        // FRIDAY: 10:30-12:30 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("DBMS CP1 (RS) (4111)", Batch.CP1, "DBMS"),
                                Triple("PCE CP2 (SLM) (2101)", Batch.CP2, "PCE"),
                                Triple("CP3 S.L. / LIB.", Batch.CP3, "SL_LIB")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )
                    }

                    // =========================================================
                    // LUNCH BREAK ROW: 12:30 TO 01:00 (Properly Aligned Across Grid)
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(36.dp)
                            .background(Color(0xFFECFDF5))
                            .border(0.5.dp, gridBorderColor),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🍽️  12:30 TO 01:00  —  LUNCH BREAK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFF047857)
                        )
                    }

                    // =========================================================
                    // ROW 3: 01:00 TO 02:00
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(64.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(64.dp)
                                .background(Color(0xFFF8FAFC))
                                .border(0.5.dp, gridBorderColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "01:00 TO 02:00",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color(0xFF1E293B),
                                textAlign = TextAlign.Center
                            )
                        }

                        TableSingleEntryCell("DS (SDJ)", "(8113)", "DS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("DBMS (RS)", "(8113)", "DBMS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("DF (KMG)", "(8113)", "DF", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("DF (KMG)", "(8113)", "DF", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("DF (KMG)", "(8113)", "DF", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                    }

                    // =========================================================
                    // ROW 4: 02:00 TO 03:00
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(64.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(64.dp)
                                .background(Color(0xFFF8FAFC))
                                .border(0.5.dp, gridBorderColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "02:00 TO 03:00",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color(0xFF1E293B),
                                textAlign = TextAlign.Center
                            )
                        }

                        TableSingleEntryCell("DBMS (RS)", "(8113)", "DBMS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("PCE (SLM)", "(8113)", "PCE", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("PCE (SLM)", "(8113)", "PCE", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("DS (PGV)", "(8113)", "DS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        TableSingleEntryCell("PS (DAP)", "(7012)", "PS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                    }

                    // =========================================================
                    // RECESS BREAK ROW: 03:00 TO 03:10 (Properly Aligned Across Grid)
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(36.dp)
                            .background(Color(0xFFFFFBEB))
                            .border(0.5.dp, gridBorderColor),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "☕  03:00 TO 03:10  —  RECESS BREAK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFFB45309)
                        )
                    }

                    // =========================================================
                    // ROW 5 & 6: 03:10 TO 05:10 (Afternoon Lab / Lecture Block)
                    // =========================================================
                    Row(
                        modifier = Modifier
                            .width(totalTableWidth)
                            .height(128.dp)
                    ) {
                        // Time Column (Horizontal format on single clean line)
                        Column(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(128.dp)
                                .background(Color(0xFFF8FAFC))
                                .border(0.5.dp, gridBorderColor)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .border(0.5.dp, gridBorderColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "03:10 TO 04:10",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .border(0.5.dp, gridBorderColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "04:10 TO 05:10",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // MONDAY: 03:10-05:10 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("DF CP1 (KMG) (8114)", Batch.CP1, "DF"),
                                Triple("CP2 S.L. / LIB.", Batch.CP2, "SL_LIB"),
                                Triple("DS CP3 (SDJ) (4111)", Batch.CP3, "DS")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )

                        // TUESDAY: 03:10-05:10 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("PCE CP1 (SLM) (2101)", Batch.CP1, "PCE"),
                                Triple("DF CP2 (KMG) (8114)", Batch.CP2, "DF"),
                                Triple("DF CP3 (VF) (8114)", Batch.CP3, "DF")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )

                        // WEDNESDAY: 03:10-05:10 Tutorials
                        TableMultiEntryCell(
                            entries = listOf(
                                Triple("PS CP1 (DAP) (8113)", Batch.CP1, "PS"),
                                Triple("PS CP2 (VF) (8113)", Batch.CP2, "PS"),
                                Triple("PS CP3 (VF) (8113)", Batch.CP3, "PS")
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 128.dp,
                            borderColor = gridBorderColor
                        )

                        // THURSDAY: Divided into two periods (64.dp each)
                        Column(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(128.dp)
                        ) {
                            TableSingleEntryCell("PS (DAP)", "(8012)", "PS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                            TableSingleEntryCell("PS (VF)", "(8012)", "PS", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        }

                        // FRIDAY: Divided into two periods (64.dp each)
                        Column(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(128.dp)
                        ) {
                            TableSingleEntryCell("IC (CGP)", "(8113)", "IC", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                            TableSingleEntryCell("IC (CGP)", "(8113)", "IC", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 64.dp, gridBorderColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TableSingleEntryCell(
    mainText: String,
    subText: String,
    subjectCode: String,
    batch: Batch,
    highlightBatch: Batch,
    searchQuery: String,
    width: Dp,
    height: Dp,
    borderColor: Color
) {
    val theme = getSubjectTheme(subjectCode)
    val isHighlighted = highlightBatch != Batch.ALL && (batch == highlightBatch || batch == Batch.ALL)
    val matchesSearch = searchQuery.isNotBlank() && (mainText.contains(searchQuery, true) || subText.contains(searchQuery, true))

    val bgColor = when {
        matchesSearch -> Color(0xFFFEF08A) // Yellow search match
        else -> theme.bg
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(bgColor)
            .border(0.5.dp, borderColor)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mainText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = theme.text,
                textAlign = TextAlign.Center
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = theme.text.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TableMultiEntryCell(
    entries: List<Triple<String, Batch, String>>, // Text, Batch, SubjectCode
    highlightBatch: Batch,
    searchQuery: String,
    width: Dp,
    height: Dp,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .border(0.5.dp, borderColor)
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            entries.forEach { (text, batch, subjectCode) ->
                val theme = getSubjectTheme(subjectCode)
                val isBatchSelected = highlightBatch != Batch.ALL && batch == highlightBatch
                val matchesSearch = searchQuery.isNotBlank() && text.contains(searchQuery, ignoreCase = true)

                val itemBg = when {
                    matchesSearch -> Color(0xFFFEF08A)
                    isBatchSelected -> theme.bg
                    else -> theme.bg.copy(alpha = 0.65f)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = itemBg,
                    border = if (isBatchSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF06B6D4)) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.5.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isBatchSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = theme.text,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
