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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Batch
import com.example.data.model.DayOfWeek
import com.example.data.model.ResolvedLecture
import com.example.ui.theme.StaticCyanDark
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
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Computer Engineering Department • GEC Palanpur",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Grid vs Card View Mode Switcher
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (viewMode == TimetableViewMode.OFFICIAL_GRID) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewMode = TimetableViewMode.OFFICIAL_GRID }
                            .padding(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Grid Table",
                                tint = if (viewMode == TimetableViewMode.OFFICIAL_GRID) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Table",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (viewMode == TimetableViewMode.OFFICIAL_GRID) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (viewMode == TimetableViewMode.DAY_CARDS) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewMode = TimetableViewMode.DAY_CARDS }
                            .padding(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewList,
                                contentDescription = "Day List",
                                tint = if (viewMode == TimetableViewMode.DAY_CARDS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Day",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (viewMode == TimetableViewMode.DAY_CARDS) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search subject, faculty code (RS, PGV, SDJ), or room (4111, 8113)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = StaticCyanDark) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("timetable_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Batch Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )

            listOf(Batch.ALL, Batch.CP1, Batch.CP2, Batch.CP3).forEach { batch ->
                val isSelected = selectedBatch == batch
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onBatchSelected(batch) }
                        .testTag("batch_filter_${batch.name}")
                ) {
                    Text(
                        text = if (batch == Batch.ALL) "All Batches" else batch.name,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Color.White else Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (viewMode == TimetableViewMode.OFFICIAL_GRID) {
            // =========================================================================
            // 1. EXACT OFFICIAL TABLE VIEW MATCHING THE COLLEGE TIMETABLE SHEET
            // =========================================================================
            OfficialCollegeTimetableTable(
                highlightBatch = selectedBatch,
                searchQuery = searchQuery
            )
        } else {
            // =========================================================================
            // 2. DAY-BY-DAY LIST VIEW
            // =========================================================================
            ScrollableTabRow(
                selectedTabIndex = weekDays.indexOf(selectedDay).coerceAtLeast(0),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                weekDays.forEach { day ->
                    val isSelected = selectedDay == day
                    Tab(
                        selected = isSelected,
                        onClick = { onDaySelected(day) },
                        text = {
                            Text(
                                text = day.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
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
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No lectures match '$searchQuery'" else "No classes scheduled for ${selectedDay.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
 * Authentic replica of the GEC Palanpur Computer Engineering Dept Sem III timetable sheet.
 */
@Composable
fun OfficialCollegeTimetableTable(
    highlightBatch: Batch,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GOVERNMENT ENGINEERING COLLEGE, PALANPUR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Computer Engineering Department",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Academic Year : 2026-27 Odd  •  Academic term : 30/7/2026 TO 15/12/2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sem III",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "W.E.F. 13-8-2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(Color(0xFF0F172A))
            )

            // Horizontal Scrollable Matrix Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Column Header (Days)
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9))
                        .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F172A)))
                ) {
                    TableCell(text = "TIME / DAY", width = 110.dp, isHeader = true)
                    TableCell(text = "MONDAY", width = 175.dp, isHeader = true)
                    TableCell(text = "TUESDAY", width = 175.dp, isHeader = true)
                    TableCell(text = "WEDNESDAY", width = 175.dp, isHeader = true)
                    TableCell(text = "THURSDAY", width = 175.dp, isHeader = true)
                    TableCell(text = "FRIDAY", width = 175.dp, isHeader = true)
                }

                // =============================================================
                // ROW 1 & 2: 10:30 TO 12:30 (Morning Labs / Lectures)
                // =============================================================
                Row(
                    modifier = Modifier.border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)))
                ) {
                    // Time cell: 10:30 TO 12:30
                    Column(
                        modifier = Modifier
                            .width(110.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "10:30 TO\n11:30",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "11:30 TO\n12:30",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                    }

                    // MONDAY: 10:30 TO 12:30 Labs
                    TableCellMulti(
                        lines = listOf(
                            Triple("DS CP1 (PGV) (4111)", Batch.CP1, "DS"),
                            Triple("DS CP2 (VF) (4111)", Batch.CP2, "DS"),
                            Triple("DBMS CP3 (RS) (2101)", Batch.CP3, "DBMS")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )

                    // TUESDAY: 10:30 TO 12:30 Labs
                    TableCellMulti(
                        lines = listOf(
                            Triple("CP1 S.L./LIB.", Batch.CP1, "SL_LIB"),
                            Triple("DBMS CP2 (RS) (4111)", Batch.CP2, "DBMS"),
                            Triple("DS CP3 (PGV) (2101)", Batch.CP3, "DS")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )

                    // WEDNESDAY: Split into 10:30-11:30 and 11:30-12:30
                    Column(
                        modifier = Modifier
                            .width(175.dp)
                            .border(androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0)))
                    ) {
                        TableCellSingle(
                            text = "DS (PGV)\n(8113)",
                            batch = Batch.ALL,
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            height = 54.dp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFCBD5E1))
                        )
                        TableCellSingle(
                            text = "DBMS (RS)\n(8113)",
                            batch = Batch.ALL,
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            height = 54.dp
                        )
                    }

                    // THURSDAY: 10:30 TO 12:30 Labs
                    TableCellMulti(
                        lines = listOf(
                            Triple("DS CP1 (SDJ) (4111)", Batch.CP1, "DS"),
                            Triple("DS CP2 (VF) (4111)", Batch.CP2, "DS"),
                            Triple("PCE CP3 (SLM) (2101)", Batch.CP3, "PCE")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )

                    // FRIDAY: 10:30 TO 12:30 Labs
                    TableCellMulti(
                        lines = listOf(
                            Triple("DBMS CP1 (RS) (4111)", Batch.CP1, "DBMS"),
                            Triple("PCE CP2 (SLM) (2101)", Batch.CP2, "PCE"),
                            Triple("CP3 S.L. / LIB.", Batch.CP3, "SL_LIB")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )
                }

                // =============================================================
                // LUNCH BREAK ROW: 12:30 TO 01:00
                // =============================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)))
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "12:30 TO 01:00  —  LUNCH BREAK  🍽️",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFF047857)
                    )
                }

                // =============================================================
                // ROW 3: 01:00 TO 02:00
                // =============================================================
                Row(
                    modifier = Modifier.border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)))
                ) {
                    TableCell(text = "01:00 TO\n02:00", width = 110.dp)
                    TableCellSingle(text = "DS (SDJ)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "DBMS (RS)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "DF (KMG)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "DF (KMG)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "DF (KMG)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                }

                // =============================================================
                // ROW 4: 02:00 TO 03:00
                // =============================================================
                Row(
                    modifier = Modifier.border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)))
                ) {
                    TableCell(text = "02:00 TO\n03:00", width = 110.dp)
                    TableCellSingle(text = "DBMS (RS)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "PCE (SLM)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "PCE (SLM)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "DS (PGV)\n(8113)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                    TableCellSingle(text = "PS (DAP)\n(7012)", batch = Batch.ALL, highlightBatch = highlightBatch, searchQuery = searchQuery, width = 175.dp)
                }

                // =============================================================
                // RECESS BREAK ROW: 03:00 TO 03:10
                // =============================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBEB))
                        .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)))
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "03:00 TO 03:10  —  RECESS BREAK  ☕",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFFB45309)
                    )
                }

                // =============================================================
                // ROW 5 & 6: 03:10 TO 05:10 (Afternoon Labs / Lectures)
                // =============================================================
                Row(
                    modifier = Modifier.border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)))
                ) {
                    // Time cell
                    Column(
                        modifier = Modifier
                            .width(110.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "03:10 TO\n04:10",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "04:10 TO\n05:10",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                    }

                    // MONDAY: 03:10 TO 05:10 Labs
                    TableCellMulti(
                        lines = listOf(
                            Triple("DF CP1 (KMG) (8114)", Batch.CP1, "DF"),
                            Triple("CP2 S.L. / LIB.", Batch.CP2, "SL_LIB"),
                            Triple("DS CP3 (SDJ) (4111)", Batch.CP3, "DS")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )

                    // TUESDAY: 03:10 TO 05:10 Labs
                    TableCellMulti(
                        lines = listOf(
                            Triple("PCE CP1 (SLM) (2101)", Batch.CP1, "PCE"),
                            Triple("DF CP2 (KMG) (8114)", Batch.CP2, "DF"),
                            Triple("DF CP3 (VF) (8114)", Batch.CP3, "DF")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )

                    // WEDNESDAY: 03:10 TO 05:10 Tutorials
                    TableCellMulti(
                        lines = listOf(
                            Triple("PS CP1 (DAP) (8113)", Batch.CP1, "PS"),
                            Triple("PS CP2 (VF) (8113)", Batch.CP2, "PS"),
                            Triple("PS CP3 (VF) (8113)", Batch.CP3, "PS")
                        ),
                        highlightBatch = highlightBatch,
                        searchQuery = searchQuery,
                        width = 175.dp
                    )

                    // THURSDAY: Split into 03:10-04:10 and 04:10-05:10
                    Column(
                        modifier = Modifier
                            .width(175.dp)
                            .border(androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0)))
                    ) {
                        TableCellSingle(
                            text = "PS (DAP)\n(8012)",
                            batch = Batch.ALL,
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            height = 54.dp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFCBD5E1))
                        )
                        TableCellSingle(
                            text = "PS (VF)\n(8012)",
                            batch = Batch.ALL,
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            height = 54.dp
                        )
                    }

                    // FRIDAY: Split into 03:10-04:10 and 04:10-05:10
                    Column(
                        modifier = Modifier
                            .width(175.dp)
                            .border(androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0)))
                    ) {
                        TableCellSingle(
                            text = "IC (CGP)\n(8113)",
                            batch = Batch.ALL,
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            height = 54.dp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFCBD5E1))
                        )
                        TableCellSingle(
                            text = "IC (CGP)\n(8113)",
                            batch = Batch.ALL,
                            highlightBatch = highlightBatch,
                            searchQuery = searchQuery,
                            height = 54.dp
                        )
                    }
                }
            }

            // Table Signatory Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(120.dp).height(1.dp).background(Color(0xFF64748B)))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Head\nComputer Engineering Department",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(120.dp).height(1.dp).background(Color(0xFF64748B)))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Principal\nGEC Palanpur",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(width)
            .border(androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1)))
            .padding(8.dp),
        contentAlignment = if (isHeader) Alignment.Center else Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isHeader) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = if (isHeader) 12.sp else 11.sp
            ),
            color = if (isHeader) Color(0xFF0F172A) else Color(0xFF334155),
            textAlign = if (isHeader) TextAlign.Center else TextAlign.Start
        )
    }
}

@Composable
private fun TableCellSingle(
    text: String,
    batch: Batch,
    highlightBatch: Batch,
    searchQuery: String,
    width: androidx.compose.ui.unit.Dp? = null,
    height: androidx.compose.ui.unit.Dp? = null
) {
    val isHighlighted = highlightBatch != Batch.ALL && (batch == highlightBatch || batch == Batch.ALL)
    val matchesSearch = searchQuery.isNotBlank() && text.contains(searchQuery, ignoreCase = true)

    val bgColor = when {
        matchesSearch -> Color(0xFFFEF08A) // Yellow highlight for search
        isHighlighted -> StaticLimeContainer
        else -> Color.White
    }

    val textColor = when {
        isHighlighted -> StaticLimeDark
        else -> Color(0xFF0F172A)
    }

    var mod = Modifier
        .background(bgColor)
        .border(androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1)))
        .padding(6.dp)

    if (width != null) mod = mod.width(width)
    if (height != null) mod = mod.height(height)

    Box(
        modifier = mod,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif
            ),
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TableCellMulti(
    lines: List<Triple<String, Batch, String>>, // text, batch, subjectCode
    highlightBatch: Batch,
    searchQuery: String,
    width: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .width(width)
            .border(androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1)))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        lines.forEach { (text, batch, _) ->
            val isBatchSelected = highlightBatch != Batch.ALL && batch == highlightBatch
            val matchesSearch = searchQuery.isNotBlank() && text.contains(searchQuery, ignoreCase = true)

            val bg = when {
                matchesSearch -> Color(0xFFFEF08A)
                isBatchSelected -> StaticLimeContainer
                else -> Color.Transparent
            }

            val fg = when {
                isBatchSelected -> StaticLimeDark
                else -> Color(0xFF0F172A)
            }

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = bg,
                border = if (isBatchSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF84CC16)) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isBatchSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 10.5.sp
                    ),
                    color = fg,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
