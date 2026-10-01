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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
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
import com.example.ui.theme.StaticLimeContainer
import com.example.ui.theme.StaticLimeDark

enum class TimetableViewMode {
    OFFICIAL_GRID,
    DAY_CARDS
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
                                imageVector = Icons.Default.ViewList,
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
            // Authentic Official Grid matching the Photo 1:1
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
 * - Crisp solid border matching the official printed notice.
 * - Horizontal aligned time regulation without cramped vertical stacking.
 * - Proper cell alignment and padding.
 * - Exact subject, batch, faculty and room placements.
 */
@Composable
fun OfficialCollegeTimetableTable(
    highlightBatch: Batch,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    val timeColWidth = 130.dp
    val dayColWidth = 195.dp
    val borderColor = Color(0xFF1E293B) // Crisp solid dark border matching photo

    Card(
        shape = RoundedCornerShape(8.dp),
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
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Computer Engineering Department",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Academic Year: 2026-27 Odd",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Academic term : 30/7/2026 TO 15/12/2026",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Sem III",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Color.Black,
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
                        color = Color.Black
                    )
                }
            }

            // Horizontally Scrollable Official Table Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .border(2.dp, borderColor)
            ) {
                Column {
                    // Header Row: Days
                    Row(
                        modifier = Modifier
                            .background(Color.White)
                            .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
                    ) {
                        // Top-left empty or title cell
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(42.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TIME",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }

                        // Day headers
                        listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY").forEach { dayName ->
                            Box(
                                modifier = Modifier
                                    .width(dayColWidth)
                                    .height(42.dp)
                                    .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    // =========================================================
                    // ROW 1 & 2: 10:30 TO 12:30
                    // =========================================================
                    Row {
                        // Time Column (Horizontal format on single clean line)
                        Column(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(116.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .border(androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "10:30 TO 11:30",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .border(androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "11:30 TO 12:30",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // MONDAY: 10:30-12:30 Labs (Spanning full height)
                        TableMultiEntryCell(
                            entries = listOf(
                                "DS CP1 (PGV) (4111)" to Batch.CP1,
                                "DS CP2 (VF) (4111)" to Batch.CP2,
                                "DBMS CP3 (RS) (2101)" to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )

                        // TUESDAY: 10:30-12:30 Labs (Spanning full height)
                        TableMultiEntryCell(
                            entries = listOf(
                                "CP1 S.L./LIB." to Batch.CP1,
                                "DBMS CP2 (RS) (4111)" to Batch.CP2,
                                "DS CP3 (PGV) (2101)" to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )

                        // WEDNESDAY: Divided into two single periods
                        Column(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(116.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
                        ) {
                            TableSingleEntryCell(
                                mainText = "DS (PGV)",
                                subText = "(8113)",
                                batch = Batch.ALL,
                                highlightBatch = highlightBatch,
                                searchQuery = searchQuery,
                                height = 58.dp,
                                borderColor = borderColor
                            )
                            TableSingleEntryCell(
                                mainText = "DBMS (RS)",
                                subText = "(8113)",
                                batch = Batch.ALL,
                                highlightBatch = highlightBatch,
                                searchQuery = searchQuery,
                                height = 58.dp,
                                borderColor = borderColor
                            )
                        }

                        // THURSDAY: 10:30-12:30 Labs (Spanning full height)
                        TableMultiEntryCell(
                            entries = listOf(
                                "DS CP1 (SDJ) (4111)" to Batch.CP1,
                                "DS CP2 (VF) (4111)" to Batch.CP2,
                                "PCE CP3 (SLM) (2101)" to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )

                        // FRIDAY: 10:30-12:30 Labs (Spanning full height)
                        TableMultiEntryCell(
                            entries = listOf(
                                "DBMS CP1 (RS) (4111)" to Batch.CP1,
                                "PCE CP2 (SLM) (2101)" to Batch.CP2,
                                "CP3 S.L. / LIB." to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )
                    }

                    // =========================================================
                    // ROW 3: 01:00 TO 02:00
                    // =========================================================
                    Row {
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(60.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "01:00 TO 02:00",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }

                        TableSingleEntryCell("DS (SDJ)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("DBMS (RS)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("DF (KMG)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("DF (KMG)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("DF (KMG)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                    }

                    // =========================================================
                    // ROW 4: 02:00 TO 03:00
                    // =========================================================
                    Row {
                        Box(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(60.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "02:00 TO 03:00",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }

                        TableSingleEntryCell("DBMS (RS)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("PCE (SLM)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("PCE (SLM)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("DS (PGV)", "(8113)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                        TableSingleEntryCell("PS (DAP)", "(7012)", Batch.ALL, highlightBatch, searchQuery, dayColWidth, 60.dp, borderColor)
                    }

                    // =========================================================
                    // ROW 5 & 6: 03:10 TO 05:10
                    // =========================================================
                    Row {
                        // Time Column (Horizontal format on single clean line)
                        Column(
                            modifier = Modifier
                                .width(timeColWidth)
                                .height(116.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .border(androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "03:10 TO 04:10",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .border(androidx.compose.foundation.BorderStroke(0.5.dp, borderColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "04:10 TO 05:10",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // MONDAY: 03:10-05:10 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                "DF CP1 (KMG) (8114)" to Batch.CP1,
                                "CP2 S.L. / LIB." to Batch.CP2,
                                "DS CP3 (SDJ) (4111)" to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )

                        // TUESDAY: 03:10-05:10 Labs
                        TableMultiEntryCell(
                            entries = listOf(
                                "PCE CP1 (SLM) (2101)" to Batch.CP1,
                                "DF CP2 (KMG) (8114)" to Batch.CP2,
                                "DF CP3 (VF) (8114)" to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )

                        // WEDNESDAY: 03:10-05:10 Tutorials
                        TableMultiEntryCell(
                            entries = listOf(
                                "PS CP1 (DAP) (8113)" to Batch.CP1,
                                "PS CP2 (VF) (8113)" to Batch.CP2,
                                "PS CP3 (VF) (8113)" to Batch.CP3
                            ),
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            width = dayColWidth,
                            height = 116.dp,
                            borderColor = borderColor
                        )

                        // THURSDAY: Divided into two periods
                        Column(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(116.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
                        ) {
                            TableSingleEntryCell("PS (DAP)", "(8012)", Batch.ALL, highlightBatch, searchQuery, height = 58.dp, borderColor = borderColor)
                            TableSingleEntryCell("PS (VF)", "(8012)", Batch.ALL, highlightBatch, searchQuery, height = 58.dp, borderColor = borderColor)
                        }

                        // FRIDAY: Divided into two periods
                        Column(
                            modifier = Modifier
                                .width(dayColWidth)
                                .height(116.dp)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
                        ) {
                            TableSingleEntryCell("IC (CGP)", "(8113)", Batch.ALL, highlightBatch, searchQuery, height = 58.dp, borderColor = borderColor)
                            TableSingleEntryCell("IC (CGP)", "(8113)", Batch.ALL, highlightBatch, searchQuery, height = 58.dp, borderColor = borderColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Official Signatory Footer matching photo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(1.5.dp)
                            .background(Color.Black)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Head",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        text = "Computer Engineering Department",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.Black
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(1.5.dp)
                            .background(Color.Black)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Principal",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Text(
                        text = "GEC Palanpur",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun TableSingleEntryCell(
    mainText: String,
    subText: String,
    batch: Batch,
    highlightBatch: Batch,
    searchQuery: String,
    width: Dp? = null,
    height: Dp,
    borderColor: Color
) {
    val isHighlighted = highlightBatch != Batch.ALL && (batch == highlightBatch || batch == Batch.ALL)
    val matchesSearch = searchQuery.isNotBlank() && (mainText.contains(searchQuery, true) || subText.contains(searchQuery, true))

    val bgColor = when {
        matchesSearch -> Color(0xFFFEF08A)
        isHighlighted -> StaticLimeContainer
        else -> Color.White
    }

    var mod = Modifier
        .height(height)
        .background(bgColor)
        .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
        .padding(horizontal = 6.dp, vertical = 4.dp)

    if (width != null) {
        mod = mod.width(width)
    } else {
        mod = mod.fillMaxWidth()
    }

    Box(
        modifier = mod,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mainText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = if (isHighlighted) StaticLimeDark else Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif
                ),
                color = if (isHighlighted) StaticLimeDark else Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TableMultiEntryCell(
    entries: List<Pair<String, Batch>>,
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
            .border(androidx.compose.foundation.BorderStroke(1.dp, borderColor))
            .background(Color.White)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            entries.forEach { (text, batch) ->
                val isBatchSelected = highlightBatch != Batch.ALL && batch == highlightBatch
                val matchesSearch = searchQuery.isNotBlank() && text.contains(searchQuery, ignoreCase = true)

                val bg = when {
                    matchesSearch -> Color(0xFFFEF08A)
                    isBatchSelected -> StaticLimeContainer
                    else -> Color.Transparent
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = bg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isBatchSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = if (isBatchSelected) StaticLimeDark else Color.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
