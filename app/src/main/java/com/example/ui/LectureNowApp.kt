package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AdminView
import com.example.ui.components.FullTimetableView
import com.example.ui.components.HeroCurrentLectureCard
import com.example.ui.components.StudentEnrollmentDialog
import com.example.ui.components.TimeMachineDialog
import com.example.ui.components.TodayTimelineView
import com.example.ui.components.TopAppBarComponent
import com.example.ui.components.WelcomeEnrollmentView
import com.example.ui.viewmodel.LectureNowViewModel
import com.example.ui.viewmodel.NavTab

@Composable
fun LectureNowApp(
    viewModel: LectureNowViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.notificationMessage) {
        state.notificationMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotification()
        }
    }

    // One-Time Student Enrollment Gate
    // If student is not enrolled on this device, show the welcome setup screen
    if (state.currentStudent == null) {
        WelcomeEnrollmentView(
            allStudents = state.allStudents,
            onEnroll = { rollNumber -> viewModel.enrollStudent(rollNumber) },
            onSelectStudent = { student -> viewModel.selectExistingStudent(student) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBarComponent(
                currentStudent = state.currentStudent,
                timeState = state.timeState,
                onStudentClick = { viewModel.setShowEnrollDialog(true) },
                onTimeMachineClick = { viewModel.setShowTimeMachineDialog(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = state.activeTab == NavTab.DASHBOARD,
                    onClick = { viewModel.setNavTab(NavTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Today, contentDescription = "Live Today") },
                    label = { Text("Live Today") },
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = state.activeTab == NavTab.TIMETABLE,
                    onClick = { viewModel.setNavTab(NavTab.TIMETABLE) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Timetable") },
                    label = { Text("Timetable") },
                    modifier = Modifier.testTag("nav_tab_timetable")
                )
                NavigationBarItem(
                    selected = state.activeTab == NavTab.ADMIN,
                    onClick = { viewModel.setNavTab(NavTab.ADMIN) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Admin") },
                    modifier = Modifier.testTag("nav_tab_admin")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = state.activeTab,
                label = "TabCrossfade"
            ) { tab ->
                when (tab) {
                    NavTab.DASHBOARD -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            HeroCurrentLectureCard(
                                result = state.currentLectureResult,
                                onNfcRoomClick = { /* NFC removed */ }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            TodayTimelineView(
                                effectiveDay = state.effectiveDay,
                                todaySchedule = state.currentLectureResult?.todaySchedule ?: emptyList(),
                                onSlotRoomClick = { /* NFC removed */ }
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    NavTab.TIMETABLE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            FullTimetableView(
                                selectedDay = state.selectedDay,
                                selectedBatch = state.selectedBatch,
                                searchQuery = state.searchQuery,
                                fullSchedule = state.fullSchedule,
                                onDaySelected = { viewModel.setSelectedDay(it) },
                                onBatchSelected = { viewModel.setSelectedBatch(it) },
                                onSearchChanged = { viewModel.setSearchQuery(it) },
                                onSlotRoomClick = { /* NFC removed */ }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    NavTab.ADMIN -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            AdminView(
                                changes = state.changes,
                                allSubjects = state.allSubjects,
                                allClassrooms = state.allClassrooms,
                                onToggleChange = { viewModel.toggleChange(it) },
                                onDeleteChange = { viewModel.deleteChange(it) },
                                onAddChange = { slotId, day, subj, type, newRoom, newFac, reason ->
                                    viewModel.addChange(slotId, day, subj, type, newRoom, newFac, reason)
                                },
                                onResetData = { viewModel.resetData() }
                            )
                        }
                    }
                }
            }
        }

        // Student Profile & Switch Modal Dialog
        if (state.showEnrollDialog) {
            StudentEnrollmentDialog(
                currentStudent = state.currentStudent,
                allStudents = state.allStudents,
                onDismiss = { viewModel.setShowEnrollDialog(false) },
                onEnroll = { roll -> viewModel.enrollStudent(roll) },
                onSelectStudent = { student -> viewModel.selectExistingStudent(student) },
                onLogout = { viewModel.logoutStudent() }
            )
        }

        // Time Machine / Simulator Dialog (allows simulating any weekday/period)
        if (state.showTimeMachineDialog) {
            TimeMachineDialog(
                timeState = state.timeState,
                onDismiss = { viewModel.setShowTimeMachineDialog(false) },
                onApplySimulation = { day, hr, min ->
                    viewModel.setTimeSimulation(true, day, hr, min)
                },
                onResetRealTime = { viewModel.resetToRealTime() }
            )
        }
    }
}
