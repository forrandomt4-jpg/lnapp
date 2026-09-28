package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class Batch(val displayName: String) {
    ALL("All Batches"),
    CP1("Batch CP1"),
    CP2("Batch CP2"),
    CP3("Batch CP3")
}

enum class DayOfWeek(val displayName: String, val shortName: String) {
    MONDAY("Monday", "Mon"),
    TUESDAY("Tuesday", "Tue"),
    WEDNESDAY("Wednesday", "Wed"),
    THURSDAY("Thursday", "Thu"),
    FRIDAY("Friday", "Fri"),
    SATURDAY("Saturday", "Sat"),
    SUNDAY("Sunday", "Sun")
}

data class Student(
    val id: String,
    val enrollmentNumber: String,
    val name: String,
    val branch: String = "Computer Engineering",
    val semester: Int = 3,
    val batch: Batch,
    val email: String
)

data class Subject(
    val code: String,
    val name: String,
    val shortName: String,
    val color: Color,
    val credits: Int = 4
)

data class Faculty(
    val id: String,
    val name: String,
    val shortCode: String,
    val email: String,
    val department: String = "Computer Engineering"
)

data class Classroom(
    val id: String,
    val code: String,
    val name: String,
    val building: String,
    val type: String, // Classroom, Lab, Auditorium
    val capacity: Int
)

enum class SlotType {
    LECTURE, LAB, TUTORIAL
}

data class TimetableSlot(
    val id: String,
    val day: DayOfWeek,
    val startTime: String, // "09:00"
    val endTime: String,   // "10:00"
    val subjectCode: String,
    val facultyId: String,
    val classroomId: String,
    val batch: Batch = Batch.ALL,
    val type: SlotType = SlotType.LECTURE
)

enum class ChangeType(val label: String) {
    ROOM_CHANGE("Room Change"),
    FACULTY_SUBSTITUTE("Faculty Substitute"),
    CANCELLATION("Lecture Cancelled"),
    TIME_CHANGE("Time Changed"),
    EXTRA_LECTURE("Extra Lecture")
}

data class TimetableChange(
    val id: String,
    val slotId: String,
    val day: DayOfWeek,
    val subjectCode: String,
    val type: ChangeType,
    val newClassroomId: String? = null,
    val newFacultyId: String? = null,
    val newStartTime: String? = null,
    val newEndTime: String? = null,
    val reason: String,
    val active: Boolean = true,
    val createdAt: String = "Today"
)

data class Holiday(
    val id: String,
    val date: String,
    val title: String,
    val description: String
)

enum class SlotStatus {
    UPCOMING,
    ONGOING,
    COMPLETED,
    CANCELLED
}

data class ResolvedLecture(
    val slot: TimetableSlot,
    val subject: Subject,
    val faculty: Faculty,
    val classroom: Classroom,
    val effectiveClassroom: Classroom,
    val effectiveFaculty: Faculty,
    val isChanged: Boolean = false,
    val changeType: ChangeType? = null,
    val changeReason: String? = null,
    val isCancelled: Boolean = false,
    val status: SlotStatus = SlotStatus.UPCOMING,
    val remainingSeconds: Long = 0,
    val untilStartSeconds: Long = 0,
    val formattedRemaining: String = "",
    val formattedUntilStart: String = ""
)

enum class LectureScheduleState(val title: String) {
    ONGOING("Ongoing Lecture"),
    NEXT_UPCOMING("Upcoming Next"),
    BREAK("Short Break"),
    LUNCH("Lunch Break"),
    DAY_COMPLETED("Lectures Completed for Today"),
    SUNDAY("Sunday — Campus Closed"),
    HOLIDAY("Institute Holiday"),
    NO_SCHEDULE("No Lectures Scheduled")
}

data class CurrentLectureResult(
    val state: LectureScheduleState,
    val currentLecture: ResolvedLecture? = null,
    val nextLecture: ResolvedLecture? = null,
    val message: String = "",
    val holidayName: String? = null,
    val todaySchedule: List<ResolvedLecture> = emptyList()
)

data class RoomScheduleResult(
    val classroom: Classroom,
    val currentLecture: ResolvedLecture?,
    val nextLecture: ResolvedLecture?,
    val fullDaySchedule: List<ResolvedLecture>,
    val isUserBatchHereNow: Boolean,
    val userBatchStatusMessage: String
)
