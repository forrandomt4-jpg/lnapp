package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.Batch
import com.example.data.model.ChangeType
import com.example.data.model.Classroom
import com.example.data.model.CurrentLectureResult
import com.example.data.model.DayOfWeek
import com.example.data.model.Faculty
import com.example.data.model.LectureScheduleState
import com.example.data.model.ResolvedLecture
import com.example.data.model.RoomScheduleResult
import com.example.data.model.SlotStatus
import com.example.data.model.SlotType
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.TimetableChange
import com.example.data.model.TimetableSlot
import com.example.data.seed.SeedData
import com.example.data.security.SecurePreferences
import com.example.ui.widget.LectureAppWidgetProvider
import com.example.ui.widget.LectureGlanceWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class SimulationTime(
    val isSimulated: Boolean = false, // Defaults to REAL system time
    val simulatedDay: DayOfWeek = DayOfWeek.MONDAY,
    val simulatedHour: Int = 10,
    val simulatedMinute: Int = 45,
    val simulatedSecond: Int = 0
)

class TimetableRepository(private val context: Context) {

    private val securePrefs = SecurePreferences(context)

    private val _subjects = MutableStateFlow(SeedData.SUBJECTS)
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _faculties = MutableStateFlow(SeedData.FACULTIES)
    val faculties: StateFlow<List<Faculty>> = _faculties.asStateFlow()

    private val _classrooms = MutableStateFlow(SeedData.CLASSROOMS)
    val classrooms: StateFlow<List<Classroom>> = _classrooms.asStateFlow()

    private val _students = MutableStateFlow(SeedData.STUDENTS)
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _slots = MutableStateFlow(SeedData.TIMETABLE_SLOTS)
    val slots: StateFlow<List<TimetableSlot>> = _slots.asStateFlow()

    private val _changes = MutableStateFlow(SeedData.INITIAL_CHANGES)
    val changes: StateFlow<List<TimetableChange>> = _changes.asStateFlow()

    private val _currentStudent = MutableStateFlow<Student?>(null)
    val currentStudent: StateFlow<Student?> = _currentStudent.asStateFlow()

    // Simulation time state - defaults to real system time
    private val _timeState = MutableStateFlow(
        SimulationTime(
            isSimulated = false,
            simulatedDay = DayOfWeek.MONDAY,
            simulatedHour = 10,
            simulatedMinute = 45,
            simulatedSecond = 0
        )
    )
    val timeState: StateFlow<SimulationTime> = _timeState.asStateFlow()

    init {
        // Load saved student from EncryptedSharedPreferences (null on first launch)
        val savedEnrollment = securePrefs.getSavedEnrollmentNumber()
        val found = if (!savedEnrollment.isNullOrBlank()) {
            _students.value.firstOrNull {
                it.enrollmentNumber.equals(savedEnrollment, ignoreCase = true) || it.id.equals(savedEnrollment, ignoreCase = true)
            }
        } else {
            null
        }

        _currentStudent.value = found
    }

    fun setStudent(student: Student?) {
        _currentStudent.value = student
        if (student != null) {
            securePrefs.saveStudentEnrollment(
                enrollmentNumber = student.enrollmentNumber,
                studentId = student.id,
                studentName = student.name,
                batch = student.batch.name,
                branch = student.branch,
                semester = student.semester
            )
        } else {
            securePrefs.clearSession()
        }
        triggerWidgetRefresh()
    }

    /**
     * Looks up student in the real dataset by enrollment number.
     * Returns true if found and saved, false if invalid (no inventing records).
     */
    fun enrollByRollNumber(rollNumber: String): Boolean {
        val trimmed = rollNumber.trim().uppercase()
        val student = _students.value.firstOrNull {
            it.enrollmentNumber.equals(trimmed, ignoreCase = true) || it.id.equals(trimmed, ignoreCase = true)
        }
        return if (student != null) {
            setStudent(student)
            true
        } else {
            Log.w("TimetableRepository", "Enrollment lookup failed for: $trimmed. No record exists.")
            false
        }
    }

    fun updateTimeSimulation(isSimulated: Boolean, day: DayOfWeek, hour: Int, minute: Int) {
        _timeState.value = SimulationTime(
            isSimulated = isSimulated,
            simulatedDay = day,
            simulatedHour = hour,
            simulatedMinute = minute,
            simulatedSecond = 0
        )
        triggerWidgetRefresh()
    }

    fun resetToRealTime() {
        val cal = Calendar.getInstance()
        val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            else -> DayOfWeek.SUNDAY
        }
        _timeState.value = SimulationTime(
            isSimulated = false,
            simulatedDay = dayOfWeek,
            simulatedHour = cal.get(Calendar.HOUR_OF_DAY),
            simulatedMinute = cal.get(Calendar.MINUTE),
            simulatedSecond = cal.get(Calendar.SECOND)
        )
        triggerWidgetRefresh()
    }

    fun addChange(change: TimetableChange) {
        _changes.value = listOf(change) + _changes.value
        triggerWidgetRefresh()
    }

    fun toggleChange(changeId: String) {
        _changes.value = _changes.value.map {
            if (it.id == changeId) it.copy(active = !it.active) else it
        }
        triggerWidgetRefresh()
    }

    fun deleteChange(changeId: String) {
        _changes.value = _changes.value.filter { it.id != changeId }
        triggerWidgetRefresh()
    }

    fun resetToDefaultData() {
        _changes.value = SeedData.INITIAL_CHANGES
        _slots.value = SeedData.TIMETABLE_SLOTS
        _students.value = SeedData.STUDENTS
        triggerWidgetRefresh()
    }

    private fun triggerWidgetRefresh() {
        try {
            LectureAppWidgetProvider.updateAllWidgets(context)
            CoroutineScope(Dispatchers.IO).launch {
                LectureGlanceWidget.updateWidget(context)
            }
        } catch (e: Exception) {
            Log.w("TimetableRepository", "Failed to update widgets: ${e.message}")
        }
    }

    fun getEffectiveDay(): DayOfWeek {
        val current = _timeState.value
        return if (current.isSimulated) {
            current.simulatedDay
        } else {
            val cal = Calendar.getInstance()
            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> DayOfWeek.MONDAY
                Calendar.TUESDAY -> DayOfWeek.TUESDAY
                Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
                Calendar.THURSDAY -> DayOfWeek.THURSDAY
                Calendar.FRIDAY -> DayOfWeek.FRIDAY
                Calendar.SATURDAY -> DayOfWeek.SATURDAY
                else -> DayOfWeek.SUNDAY
            }
        }
    }

    fun getEffectiveTimeSeconds(): Long {
        val current = _timeState.value
        return if (current.isSimulated) {
            (current.simulatedHour * 3600L) + (current.simulatedMinute * 60L) + current.simulatedSecond
        } else {
            val cal = Calendar.getInstance()
            (cal.get(Calendar.HOUR_OF_DAY) * 3600L) + (cal.get(Calendar.MINUTE) * 60L) + cal.get(Calendar.SECOND)
        }
    }

    fun parseTimeToSeconds(timeStr: String): Long {
        val parts = timeStr.split(":")
        val h = parts.getOrNull(0)?.toLongOrNull() ?: 0L
        val m = parts.getOrNull(1)?.toLongOrNull() ?: 0L
        return (h * 3600L) + (m * 60L)
    }

    fun parseTimeSeconds(timeStr: String): Long = parseTimeToSeconds(timeStr)

    fun formatSecondsToTime(seconds: Long): String {
        val days = seconds / 86400
        val remSec = seconds % 86400
        val hrs = remSec / 3600
        val mins = (remSec % 3600) / 60
        val secs = remSec % 60
        return when {
            days > 0 -> "${days}d ${hrs}h ${mins}m ${secs}s"
            hrs > 0 -> "${hrs}h ${mins}m ${secs}s"
            mins > 0 -> "${mins}m ${secs}s"
            else -> "${secs}s"
        }
    }

    fun resolveSlot(slot: TimetableSlot, currentSeconds: Long): ResolvedLecture {
        val subject = _subjects.value.firstOrNull { it.code == slot.subjectCode }
            ?: Subject(slot.subjectCode, slot.subjectCode, slot.subjectCode, androidx.compose.ui.graphics.Color.Gray)
        val faculty = _faculties.value.firstOrNull { it.id == slot.facultyId }
            ?: Faculty(slot.facultyId, slot.facultyId, slot.facultyId, "")
        val classroom = _classrooms.value.firstOrNull { it.id == slot.classroomId }
            ?: Classroom(slot.classroomId, slot.classroomId, "Room ${slot.classroomId}", "Academic Block", "Classroom", 60)

        // Changes only apply for the scheduled day — after that day concludes, schedule reverts to normal
        val currentDay = getEffectiveDay()
        val activeChange = _changes.value.firstOrNull {
            it.active && it.day == currentDay && (it.slotId == slot.id || (it.day == slot.day && it.subjectCode == slot.subjectCode))
        }

        var effectiveClassroom = classroom
        var effectiveFaculty = faculty
        var isCancelled = false
        var effectiveStart = slot.startTime
        var effectiveEnd = slot.endTime

        if (activeChange != null) {
            when (activeChange.type) {
                ChangeType.ROOM_CHANGE -> {
                    activeChange.newClassroomId?.let { newRoomId ->
                        _classrooms.value.firstOrNull { it.id == newRoomId }?.let { effectiveClassroom = it }
                    }
                }
                ChangeType.FACULTY_SUBSTITUTE -> {
                    activeChange.newFacultyId?.let { newFacId ->
                        _faculties.value.firstOrNull { it.id == newFacId }?.let { effectiveFaculty = it }
                    }
                }
                ChangeType.CANCELLATION -> {
                    isCancelled = true
                }
                ChangeType.TIME_CHANGE -> {
                    activeChange.newStartTime?.let { effectiveStart = it }
                    activeChange.newEndTime?.let { effectiveEnd = it }
                }
                else -> {}
            }
        }

        val startSec = parseTimeToSeconds(effectiveStart)
        val endSec = parseTimeToSeconds(effectiveEnd)

        val status = when {
            isCancelled -> SlotStatus.CANCELLED
            currentSeconds in startSec until endSec -> SlotStatus.ONGOING
            currentSeconds < startSec -> SlotStatus.UPCOMING
            else -> SlotStatus.COMPLETED
        }

        val remainingSec = if (status == SlotStatus.ONGOING) maxOf(0L, endSec - currentSeconds) else 0L
        val untilStartSec = if (status == SlotStatus.UPCOMING) maxOf(0L, startSec - currentSeconds) else 0L

        return ResolvedLecture(
            slot = slot.copy(startTime = effectiveStart, endTime = effectiveEnd),
            subject = subject,
            faculty = faculty,
            classroom = classroom,
            effectiveClassroom = effectiveClassroom,
            effectiveFaculty = effectiveFaculty,
            isChanged = activeChange != null,
            changeType = activeChange?.type,
            changeReason = activeChange?.reason,
            isCancelled = isCancelled,
            status = status,
            remainingSeconds = remainingSec,
            untilStartSeconds = untilStartSec,
            formattedRemaining = formatSecondsToTime(remainingSec),
            formattedUntilStart = formatSecondsToTime(untilStartSec)
        )
    }

    /**
     * Core logic evaluating:
     * student -> branch -> batch -> semester(=3) -> weekday -> that batch/day's timetable
     * -> compare current time to periods -> apply latest changes -> return current and next lecture
     */
    fun getCurrentLectureResult(student: Student?): CurrentLectureResult {
        val effectiveDay = getEffectiveDay()
        val currentSeconds = getEffectiveTimeSeconds()
        val studentBatch = student?.batch ?: Batch.CP1

        // Accurately calculates seconds remaining until Monday 10:30 AM
        fun calculateSecondsUntilNextMonday1030(): Long {
            return if (!_timeState.value.isSimulated) {
                val nowCal = Calendar.getInstance()
                val targetCal = (nowCal.clone() as Calendar).apply {
                    while (get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 30)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (timeInMillis <= nowCal.timeInMillis) {
                        add(Calendar.DAY_OF_YEAR, 7)
                    }
                }
                maxOf(0L, (targetCal.timeInMillis - nowCal.timeInMillis) / 1000L)
            } else {
                val daysUntilMon = when (_timeState.value.simulatedDay) {
                    DayOfWeek.MONDAY -> if (currentSeconds >= parseTimeToSeconds("10:30")) 7 else 0
                    DayOfWeek.TUESDAY -> 6
                    DayOfWeek.WEDNESDAY -> 5
                    DayOfWeek.THURSDAY -> 4
                    DayOfWeek.FRIDAY -> 3
                    DayOfWeek.SATURDAY -> 2
                    DayOfWeek.SUNDAY -> 1
                }
                if (daysUntilMon == 0) {
                    maxOf(0L, parseTimeToSeconds("10:30") - currentSeconds)
                } else {
                    val secTodayLeft = maxOf(0L, 86400L - currentSeconds)
                    val fullDaysSec = (daysUntilMon - 1) * 86400L
                    val monMorningSec = parseTimeToSeconds("10:30")
                    secTodayLeft + fullDaysSec + monMorningSec
                }
            }
        }

        fun resolveMondayFirstLecture(): Pair<ResolvedLecture?, Long> {
            val mondaySlots = _slots.value.filter {
                it.day == DayOfWeek.MONDAY && (it.batch == Batch.ALL || it.batch == studentBatch)
            }.sortedBy { parseTimeToSeconds(it.startTime) }
            val firstMondaySlot = mondaySlots.firstOrNull()
            val secToMon = calculateSecondsUntilNextMonday1030()
            val resolvedMonday = firstMondaySlot?.let { resolveSlot(it, 0L) }?.copy(
                status = SlotStatus.UPCOMING,
                untilStartSeconds = secToMon,
                formattedUntilStart = formatSecondsToTime(secToMon)
            )
            return Pair(resolvedMonday, secToMon)
        }

        // 1. SUNDAY: Academic Rest Day. Next lecture starts Monday at 10:30 AM!
        if (effectiveDay == DayOfWeek.SUNDAY) {
            val (mondayLecture, secToMon) = resolveMondayFirstLecture()
            return CurrentLectureResult(
                state = LectureScheduleState.SUNDAY,
                nextLecture = mondayLecture,
                message = "Sunday — Academic Recess. First lecture starts Monday at 10:30 AM (in ${formatSecondsToTime(secToMon)}).",
                todaySchedule = emptyList()
            )
        }

        // Get slots matching batch (either ALL or student's specific batch CP1/CP2/CP3)
        val todaySlots = _slots.value.filter {
            it.day == effectiveDay && (it.batch == Batch.ALL || it.batch == studentBatch)
        }.sortedBy { parseTimeToSeconds(it.startTime) }

        if (todaySlots.isEmpty()) {
            val (mondayLecture, secToMon) = resolveMondayFirstLecture()
            return CurrentLectureResult(
                state = LectureScheduleState.NO_SCHEDULE,
                nextLecture = mondayLecture,
                message = "No timetable slots found for today.",
                todaySchedule = emptyList()
            )
        }

        val resolvedList = todaySlots.map { resolveSlot(it, currentSeconds) }

        val ongoing = resolvedList.firstOrNull { it.status == SlotStatus.ONGOING }
        val nextUpcoming = resolvedList.firstOrNull { it.status == SlotStatus.UPCOMING }

        // Real college schedule break periods:
        // Lunch: 12:30 to 13:00 (12:30 PM - 1:00 PM)
        // Recess: 15:00 to 15:10 (3:00 PM - 3:10 PM)
        val lunchStart = parseTimeToSeconds("12:30")
        val lunchEnd = parseTimeToSeconds("13:00")
        val recessStart = parseTimeToSeconds("15:00")
        val recessEnd = parseTimeToSeconds("15:10")

        // SATURDAY after classes (after 12:30 PM): Target Monday 10:30 AM!
        if (effectiveDay == DayOfWeek.SATURDAY && (nextUpcoming == null && ongoing == null)) {
            val (mondayLecture, secToMon) = resolveMondayFirstLecture()
            return CurrentLectureResult(
                state = LectureScheduleState.DAY_COMPLETED,
                nextLecture = mondayLecture,
                message = "Saturday classes completed! Monday lectures begin in ${formatSecondsToTime(secToMon)}.",
                todaySchedule = resolvedList
            )
        }

        val allCompleted = resolvedList.all { it.status == SlotStatus.COMPLETED || it.status == SlotStatus.CANCELLED }

        val state = when {
            ongoing != null -> LectureScheduleState.ONGOING
            currentSeconds in lunchStart until lunchEnd -> LectureScheduleState.LUNCH
            currentSeconds in recessStart until recessEnd -> LectureScheduleState.BREAK
            nextUpcoming != null -> LectureScheduleState.NEXT_UPCOMING
            allCompleted -> LectureScheduleState.DAY_COMPLETED
            else -> LectureScheduleState.NO_SCHEDULE
        }

        // When today's classes are finished, target the next academic day's first lecture
        val effectiveNextLecture: ResolvedLecture? = if (allCompleted && nextUpcoming == null) {
            if (effectiveDay == DayOfWeek.FRIDAY || effectiveDay == DayOfWeek.SATURDAY) {
                resolveMondayFirstLecture().first
            } else {
                val nextDay = when (effectiveDay) {
                    DayOfWeek.MONDAY -> DayOfWeek.TUESDAY
                    DayOfWeek.TUESDAY -> DayOfWeek.WEDNESDAY
                    DayOfWeek.WEDNESDAY -> DayOfWeek.THURSDAY
                    DayOfWeek.THURSDAY -> DayOfWeek.FRIDAY
                    else -> DayOfWeek.MONDAY
                }
                val tomorrowSlots = _slots.value.filter {
                    it.day == nextDay && (it.batch == Batch.ALL || it.batch == studentBatch)
                }.sortedBy { parseTimeToSeconds(it.startTime) }
                tomorrowSlots.firstOrNull()?.let {
                    val secUntilMidnight = maxOf(0L, 86400L - currentSeconds)
                    val secTomorrow = parseTimeToSeconds(it.startTime)
                    val totalSec = secUntilMidnight + secTomorrow
                    resolveSlot(it, 0L).copy(
                        status = SlotStatus.UPCOMING,
                        untilStartSeconds = totalSec,
                        formattedUntilStart = formatSecondsToTime(totalSec)
                    )
                }
            }
        } else {
            nextUpcoming
        }

        val message = when (state) {
            LectureScheduleState.ONGOING -> "Active class in progress."
            LectureScheduleState.NEXT_UPCOMING -> "Next lecture starts in ${nextUpcoming?.formattedUntilStart}."
            LectureScheduleState.BREAK -> "Campus Recess (3:00 PM - 3:10 PM). Next period starts shortly."
            LectureScheduleState.LUNCH -> "Campus Lunch Break (12:30 PM - 1:00 PM). Cafeteria open."
            LectureScheduleState.DAY_COMPLETED -> {
                if (effectiveDay == DayOfWeek.FRIDAY || effectiveDay == DayOfWeek.SATURDAY) {
                    "Weekend ahead! Next classes resume Monday at 10:30 AM."
                } else {
                    "All lectures completed for today! See you tomorrow at 10:30 AM."
                }
            }
            else -> ""
        }

        return CurrentLectureResult(
            state = state,
            currentLecture = ongoing,
            nextLecture = effectiveNextLecture,
            message = message,
            todaySchedule = resolvedList
        )
    }

    fun getRoomSchedule(roomId: String, student: Student?): RoomScheduleResult {
        val effectiveDay = getEffectiveDay()
        val currentSeconds = getEffectiveTimeSeconds()

        val classroom = _classrooms.value.firstOrNull { it.id.equals(roomId, ignoreCase = true) }
            ?: Classroom(roomId, roomId, "Room $roomId", "Academic Complex", "Classroom", 60)

        // Find all slots for this room on effective day
        val roomSlots = _slots.value.filter {
            it.day == effectiveDay && it.classroomId.equals(roomId, ignoreCase = true)
        }.sortedBy { parseTimeToSeconds(it.startTime) }

        val resolved = roomSlots.map { resolveSlot(it, currentSeconds) }
        val current = resolved.firstOrNull { it.status == SlotStatus.ONGOING }
        val next = resolved.firstOrNull { it.status == SlotStatus.UPCOMING }

        // Check if student's batch is here right now
        val studentBatch = student?.batch ?: Batch.CP1
        val isUserBatchHereNow = current != null && (current.slot.batch == Batch.ALL || current.slot.batch == studentBatch)

        val userMessage = when {
            current == null -> "Room is currently vacant/available."
            isUserBatchHereNow -> "YES! Your batch (${studentBatch.name}) is scheduled here right now."
            else -> {
                "Room occupied by ${current.slot.batch.name} (${current.subject.shortName}). Your batch is not scheduled here right now."
            }
        }

        return RoomScheduleResult(
            classroom = classroom,
            currentLecture = current,
            nextLecture = next,
            fullDaySchedule = resolved,
            isUserBatchHereNow = isUserBatchHereNow,
            userBatchStatusMessage = userMessage
        )
    }

    fun getFullScheduleForDayAndBatch(day: DayOfWeek, batchFilter: Batch): List<ResolvedLecture> {
        val currentSeconds = getEffectiveTimeSeconds()
        val isToday = day == getEffectiveDay()

        val filtered = _slots.value.filter { slot ->
            slot.day == day && (batchFilter == Batch.ALL || slot.batch == Batch.ALL || slot.batch == batchFilter)
        }.sortedBy { parseTimeToSeconds(it.startTime) }

        return filtered.map { slot ->
            if (isToday) {
                resolveSlot(slot, currentSeconds)
            } else {
                resolveSlot(slot, 0L).copy(status = SlotStatus.UPCOMING)
            }
        }
    }
}
