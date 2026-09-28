package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Batch
import com.example.data.model.ChangeType
import com.example.data.model.Classroom
import com.example.data.model.CurrentLectureResult
import com.example.data.model.DayOfWeek
import com.example.data.model.ResolvedLecture
import com.example.data.model.RoomScheduleResult
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.TimetableChange
import com.example.data.repository.SimulationTime
import com.example.data.repository.TimetableRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class NavTab(val title: String) {
    DASHBOARD("Live Today"),
    TIMETABLE("Timetable"),
    ADMIN("Admin")
}

data class UiState(
    val currentStudent: Student? = null,
    val currentLectureResult: CurrentLectureResult? = null,
    val timeState: SimulationTime = SimulationTime(),
    val effectiveDay: DayOfWeek = DayOfWeek.MONDAY,
    val selectedDay: DayOfWeek = DayOfWeek.MONDAY,
    val selectedBatch: Batch = Batch.ALL,
    val searchQuery: String = "",
    val fullSchedule: List<ResolvedLecture> = emptyList(),
    val activeTab: NavTab = NavTab.DASHBOARD,
    val selectedRoomId: String = "C302",
    val roomScheduleResult: RoomScheduleResult? = null,
    val changes: List<TimetableChange> = emptyList(),
    val allStudents: List<Student> = emptyList(),
    val allSubjects: List<Subject> = emptyList(),
    val allClassrooms: List<Classroom> = emptyList(),
    val showEnrollDialog: Boolean = false,
    val showTimeMachineDialog: Boolean = false,
    val notificationMessage: String? = null
)

class LectureNowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TimetableRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // Collect repository flows
        viewModelScope.launch {
            repository.currentStudent.collect { student ->
                _uiState.value = _uiState.value.copy(currentStudent = student)
                refreshData()
            }
        }

        viewModelScope.launch {
            repository.timeState.collect { time ->
                _uiState.value = _uiState.value.copy(timeState = time, selectedDay = time.simulatedDay)
                refreshData()
            }
        }

        viewModelScope.launch {
            repository.changes.collect { changes ->
                _uiState.value = _uiState.value.copy(changes = changes)
                refreshData()
            }
        }

        viewModelScope.launch {
            repository.students.collect { students ->
                _uiState.value = _uiState.value.copy(allStudents = students)
            }
        }

        viewModelScope.launch {
            repository.subjects.collect { subjects ->
                _uiState.value = _uiState.value.copy(allSubjects = subjects)
            }
        }

        viewModelScope.launch {
            repository.classrooms.collect { classrooms ->
                _uiState.value = _uiState.value.copy(allClassrooms = classrooms)
            }
        }

        // Ticking loop for 1-second countdown updates
        viewModelScope.launch {
            while (true) {
                delay(1000)
                refreshData()
            }
        }

        refreshData()
    }

    fun refreshData() {
        val student = _uiState.value.currentStudent
        val effectiveDay = repository.getEffectiveDay()
        val currentResult = repository.getCurrentLectureResult(student)
        val roomResult = repository.getRoomSchedule(_uiState.value.selectedRoomId, student)
        val fullSchedule = repository.getFullScheduleForDayAndBatch(
            _uiState.value.selectedDay,
            _uiState.value.selectedBatch
        )

        _uiState.value = _uiState.value.copy(
            effectiveDay = effectiveDay,
            currentLectureResult = currentResult,
            roomScheduleResult = roomResult,
            fullSchedule = fullSchedule
        )
    }

    fun setNavTab(tab: NavTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun setSelectedDay(day: DayOfWeek) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
        refreshData()
    }

    fun setSelectedBatch(batch: Batch) {
        _uiState.value = _uiState.value.copy(selectedBatch = batch)
        refreshData()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectRoom(roomId: String) {
        _uiState.value = _uiState.value.copy(selectedRoomId = roomId)
        val roomResult = repository.getRoomSchedule(roomId, _uiState.value.currentStudent)
        _uiState.value = _uiState.value.copy(roomScheduleResult = roomResult)
    }

    fun enrollStudent(rollNumber: String): Boolean {
        val success = repository.enrollByRollNumber(rollNumber)
        if (success) {
            _uiState.value = _uiState.value.copy(
                showEnrollDialog = false,
                notificationMessage = "Successfully enrolled as ${repository.currentStudent.value?.name} (${repository.currentStudent.value?.batch?.name})!"
            )
            refreshData()
        }
        return success
    }

    fun selectExistingStudent(student: Student) {
        repository.setStudent(student)
        _uiState.value = _uiState.value.copy(
            showEnrollDialog = false,
            notificationMessage = "Switched to ${student.name} (${student.batch.name})"
        )
    }

    fun logoutStudent() {
        repository.setStudent(null)
        _uiState.value = _uiState.value.copy(notificationMessage = "Signed out to Guest Mode")
    }

    fun setShowEnrollDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showEnrollDialog = show)
    }

    fun setShowTimeMachineDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTimeMachineDialog = show)
    }

    fun setTimeSimulation(isSimulated: Boolean, day: DayOfWeek, hour: Int, minute: Int) {
        repository.updateTimeSimulation(isSimulated, day, hour, minute)
        _uiState.value = _uiState.value.copy(showTimeMachineDialog = false)
        refreshData()
    }

    fun resetToRealTime() {
        repository.resetToRealTime()
        _uiState.value = _uiState.value.copy(showTimeMachineDialog = false)
        refreshData()
    }

    fun toggleChange(changeId: String) {
        repository.toggleChange(changeId)
        refreshData()
    }

    fun deleteChange(changeId: String) {
        repository.deleteChange(changeId)
        refreshData()
    }

    fun addChange(
        slotId: String,
        day: DayOfWeek,
        subjectCode: String,
        type: ChangeType,
        newClassroomId: String?,
        newFacultyId: String?,
        reason: String
    ) {
        val newChange = TimetableChange(
            id = "CHG-${System.currentTimeMillis() % 10000}",
            slotId = slotId,
            day = day,
            subjectCode = subjectCode,
            type = type,
            newClassroomId = newClassroomId,
            newFacultyId = newFacultyId,
            reason = reason,
            active = true,
            createdAt = "Just now"
        )
        repository.addChange(newChange)
        refreshData()
    }

    fun resetData() {
        repository.resetToDefaultData()
        refreshData()
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(notificationMessage = null)
    }
}
