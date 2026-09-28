package com.example.data.seed

import androidx.compose.ui.graphics.Color
import com.example.data.model.Batch
import com.example.data.model.ChangeType
import com.example.data.model.Classroom
import com.example.data.model.DayOfWeek
import com.example.data.model.Faculty
import com.example.data.model.Holiday
import com.example.data.model.SlotType
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.TimetableChange
import com.example.data.model.TimetableSlot

object SeedData {

    val SUBJECTS = listOf(
        Subject(
            code = "DS",
            name = "Data Structures",
            shortName = "DS",
            color = Color(0xFF2563EB), // Blue
            credits = 4
        ),
        Subject(
            code = "DBMS",
            name = "Database Management Systems",
            shortName = "DBMS",
            color = Color(0xFF059669), // Emerald
            credits = 4
        ),
        Subject(
            code = "DF",
            name = "Digital Fundamentals",
            shortName = "DF",
            color = Color(0xFFD97706), // Amber
            credits = 3
        ),
        Subject(
            code = "PCE",
            name = "Professional Communication & Ethics",
            shortName = "PCE",
            color = Color(0xFF0891B2), // Cyan
            credits = 2
        ),
        Subject(
            code = "PS",
            name = "Probability and Statistics",
            shortName = "PS",
            color = Color(0xFF7C3AED), // Purple
            credits = 4
        ),
        Subject(
            code = "IC",
            name = "Indian Constitution",
            shortName = "IC",
            color = Color(0xFF4F46E5), // Indigo
            credits = 2
        ),
        Subject(
            code = "SL_LIB",
            name = "Self Learning / Library",
            shortName = "S.L. / LIB.",
            color = Color(0xFF64748B), // Slate
            credits = 0
        )
    )

    val FACULTIES = listOf(
        Faculty("PGV", "PGV", "PGV", "pgv@gecp.ac.in", "Computer Engineering"),
        Faculty("SDJ", "SDJ", "SDJ", "sdj@gecp.ac.in", "Computer Engineering"),
        Faculty("RS", "RS", "RS", "rs@gecp.ac.in", "Computer Engineering"),
        Faculty("KMG", "KMG", "KMG", "kmg@gecp.ac.in", "Computer Engineering"),
        Faculty("SLM", "SLM", "SLM", "slm@gecp.ac.in", "General / Humanities"),
        Faculty("DAP", "DAP", "DAP", "dap@gecp.ac.in", "Mathematics & Statistics"),
        Faculty("CGP", "CGP", "CGP", "cgp@gecp.ac.in", "General Studies"),
        Faculty("VF", "VF", "VF", "vf@gecp.ac.in", "Computer Engineering"),
        Faculty("NONE", "—", "—", "info@gecp.ac.in", "Library & Self Learning")
    )

    val CLASSROOMS = listOf(
        Classroom("4111", "4111", "Computer Dept Lab 4111", "Department Block", "Lab", 45),
        Classroom("2101", "2101", "Classroom 2101", "Ground Floor Academic Block", "Classroom", 60),
        Classroom("8113", "8113", "Lecture Hall 8113", "Administrative & Main Block", "Lecture Hall", 120),
        Classroom("8114", "8114", "Digital Circuits Lab 8114", "Main Academic Block", "Lab", 40),
        Classroom("8012", "8012", "Classroom 8012", "Science & Humanities Block", "Classroom", 65),
        Classroom("7012", "7012", "Classroom 7012", "First Floor Block", "Classroom", 65),
        Classroom("LIB", "LIB", "Central Library & Reading Room", "Library Building", "Library", 150)
    )

    val STUDENTS = listOf(
        // ===================== BATCH CP1 (26 students) =====================
        Student("STU_001", "250610107001", "Bochal Khushiben Jamsubhai", "Computer Engineering", 3, Batch.CP1, "250610107001@gecp.ac.in"),
        Student("STU_002", "250610107002", "Borse Riya Mukesh", "Computer Engineering", 3, Batch.CP1, "250610107002@gecp.ac.in"),
        Student("STU_003", "250610107003", "Chaudhari Akansha Vishnubhai", "Computer Engineering", 3, Batch.CP1, "250610107003@gecp.ac.in"),
        Student("STU_004", "250610107004", "Chaudhary Hiteshbhai Bhvabhai", "Computer Engineering", 3, Batch.CP1, "250610107004@gecp.ac.in"),
        Student("STU_005", "250610107005", "Chaudhary Krishkumar Faljibhai", "Computer Engineering", 3, Batch.CP1, "250610107005@gecp.ac.in"),
        Student("STU_006", "250610107006", "Chaudhary Niruben Rameshbhai", "Computer Engineering", 3, Batch.CP1, "250610107006@gecp.ac.in"),
        Student("STU_007", "250610107007", "Chaudhary Pirabhai Harsengbhai", "Computer Engineering", 3, Batch.CP1, "250610107007@gecp.ac.in"),
        Student("STU_008", "250610107008", "Chaudhary Rajeshbhai Ajababhai", "Computer Engineering", 3, Batch.CP1, "250610107008@gecp.ac.in"),
        Student("STU_009", "250610107009", "Desai Angel Amitbhai", "Computer Engineering", 3, Batch.CP1, "250610107009@gecp.ac.in"),
        Student("STU_010", "250610107010", "Dubey Piyushkumar Udayshankar", "Computer Engineering", 3, Batch.CP1, "250610107010@gecp.ac.in"),
        Student("STU_012", "250610107012", "Gusai Kavypuri", "Computer Engineering", 3, Batch.CP1, "250610107012@gecp.ac.in"),
        Student("STU_014", "250610107014", "Jiya Pravinbhai Vachhani", "Computer Engineering", 3, Batch.CP1, "250610107014@gecp.ac.in"),
        Student("STU_015", "250610107015", "Joshi Raghav Rajeshkumar", "Computer Engineering", 3, Batch.CP1, "250610107015@gecp.ac.in"),
        Student("STU_016", "250610107016", "Kapadiya Manav Bharatkumar", "Computer Engineering", 3, Batch.CP1, "250610107016@gecp.ac.in"),
        Student("STU_017", "250610107017", "Kherala Meetkumar Shaileshbhai", "Computer Engineering", 3, Batch.CP1, "250610107017@gecp.ac.in"),
        Student("STU_018", "250610107018", "Khushbuben Patel", "Computer Engineering", 3, Batch.CP1, "250610107018@gecp.ac.in"),
        Student("STU_019", "250610107019", "Makwana Kavy Dilipkumar", "Computer Engineering", 3, Batch.CP1, "250610107019@gecp.ac.in"),
        Student("STU_020", "250610107020", "Makwana Naincy Jagdishbhai", "Computer Engineering", 3, Batch.CP1, "250610107020@gecp.ac.in"),
        Student("STU_021", "250610107021", "Mayank Kumawat", "Computer Engineering", 3, Batch.CP1, "250610107021@gecp.ac.in"),
        Student("STU_022", "250610107022", "Meetrajsinh Rana", "Computer Engineering", 3, Batch.CP1, "250610107022@gecp.ac.in"),
        Student("STU_TMP1", "TMP 1", "MAKNOJIYA ARMAN IMRANBHAI", "Computer Engineering", 3, Batch.CP1, "tmp1@gecp.ac.in"),
        Student("STU_TMP2", "TMP 2", "ANSARI MOHAMMAD SADIK ANSAR AHMED", "Computer Engineering", 3, Batch.CP1, "tmp2@gecp.ac.in"),
        Student("STU_TMP3", "TMP 3", "CHANDAN", "Computer Engineering", 3, Batch.CP1, "tmp3@gecp.ac.in"),
        Student("STU_TMP4", "TMP 4", "PRAJAPATI DIXIT KIRTIBHAI", "Computer Engineering", 3, Batch.CP1, "tmp4@gecp.ac.in"),
        Student("STU_TMP5", "TMP 5", "PATEL SATYADAS ARVINDBHAI", "Computer Engineering", 3, Batch.CP1, "tmp5@gecp.ac.in"),
        Student("STU_TMP6", "TMP 6", "DARJI HARDIK DILIPBHAI", "Computer Engineering", 3, Batch.CP1, "tmp6@gecp.ac.in"),

        // ===================== BATCH CP2 (27 students) =====================
        Student("STU_023", "250610107023", "Mehsaniya Ikram Yunus", "Computer Engineering", 3, Batch.CP2, "250610107023@gecp.ac.in"),
        Student("STU_024", "250610107024", "Mehta Ishteyaqali Mehdihasan", "Computer Engineering", 3, Batch.CP2, "250610107024@gecp.ac.in"),
        Student("STU_026", "250610107026", "Nangash Bhara Somabhai", "Computer Engineering", 3, Batch.CP2, "250610107026@gecp.ac.in"),
        Student("STU_027", "250610107027", "Nasit Prisha Mukesh", "Computer Engineering", 3, Batch.CP2, "250610107027@gecp.ac.in"),
        Student("STU_028", "250610107028", "Nayi Shreejaben Sanjaybhai", "Computer Engineering", 3, Batch.CP2, "250610107028@gecp.ac.in"),
        Student("STU_029", "250610107029", "Nimavat Krish Dineshbhai", "Computer Engineering", 3, Batch.CP2, "250610107029@gecp.ac.in"),
        Student("STU_030", "250610107030", "Panda Roshon Kalia", "Computer Engineering", 3, Batch.CP2, "250610107030@gecp.ac.in"),
        Student("STU_031", "250610107031", "Pandya Divyam Rameshbhai", "Computer Engineering", 3, Batch.CP2, "250610107031@gecp.ac.in"),
        Student("STU_032", "250610107032", "Parikh Marmik Naginbhai", "Computer Engineering", 3, Batch.CP2, "250610107032@gecp.ac.in"),
        Student("STU_034", "250610107034", "Parmar Pujaben Kiransinh", "Computer Engineering", 3, Batch.CP2, "250610107034@gecp.ac.in"),
        Student("STU_035", "250610107035", "Parmar Riteshkumar Kantibhai", "Computer Engineering", 3, Batch.CP2, "250610107035@gecp.ac.in"),
        Student("STU_036", "250610107036", "Parthiv Pravin Sorathia", "Computer Engineering", 3, Batch.CP2, "250610107036@gecp.ac.in"),
        Student("STU_037", "250610107037", "Patel Dax Pravinbhai", "Computer Engineering", 3, Batch.CP2, "250610107037@gecp.ac.in"),
        Student("STU_038", "250610107038", "Patel Lav Ashokkumar", "Computer Engineering", 3, Batch.CP2, "250610107038@gecp.ac.in"),
        Student("STU_040", "250610107040", "Patel Rudra", "Computer Engineering", 3, Batch.CP2, "250610107040@gecp.ac.in"),
        Student("STU_041", "250610107041", "Patel Shreya Bharatkumar", "Computer Engineering", 3, Batch.CP2, "250610107041@gecp.ac.in"),
        Student("STU_042", "250610107042", "Patel Yugkumar Rakeshbhai", "Computer Engineering", 3, Batch.CP2, "250610107042@gecp.ac.in"),
        Student("STU_043", "250610107043", "Patni Keval Kishanbhai", "Computer Engineering", 3, Batch.CP2, "250610107043@gecp.ac.in"),
        Student("STU_044", "250610107044", "Prajapati Amiben Bhurabhai", "Computer Engineering", 3, Batch.CP2, "250610107044@gecp.ac.in"),
        Student("STU_045", "250610107045", "Prajapati Chiragkumar", "Computer Engineering", 3, Batch.CP2, "250610107045@gecp.ac.in"),
        Student("STU_TMP7", "TMP 7", "MODH VANSH BHARATKUMAR", "Computer Engineering", 3, Batch.CP2, "tmp7@gecp.ac.in"),
        Student("STU_TMP8", "TMP 8", "MEVADA HASIT RUPESHKUMAR", "Computer Engineering", 3, Batch.CP2, "tmp8@gecp.ac.in"),
        Student("STU_TMP9", "TMP 9", "PATEL MITRA DINESHKUMAR", "Computer Engineering", 3, Batch.CP2, "tmp9@gecp.ac.in"),
        Student("STU_TMP10", "TMP 10", "PATEL DAX BHARATBHAI", "Computer Engineering", 3, Batch.CP2, "tmp10@gecp.ac.in"),
        Student("STU_TMP11", "TMP 11", "PATEL PRINCE PRAVINBHAI", "Computer Engineering", 3, Batch.CP2, "tmp11@gecp.ac.in"),
        Student("STU_TMP12", "TMP 12", "RAJPUT SUSHANT RAJESHBHAI", "Computer Engineering", 3, Batch.CP2, "tmp12@gecp.ac.in"),
        Student("STU_TMP13", "TMP 13", "PATEL SIDDH JIGNESHKUMAR", "Computer Engineering", 3, Batch.CP2, "tmp13@gecp.ac.in"),

        // ===================== BATCH CP3 (26 students) =====================
        Student("STU_046", "250610107046", "Prajapati Dhrumilkumar Anilkumar", "Computer Engineering", 3, Batch.CP3, "250610107046@gecp.ac.in"),
        Student("STU_047", "250610107047", "Prajapati Dhrumitkumar Bharatbhai", "Computer Engineering", 3, Batch.CP3, "250610107047@gecp.ac.in"),
        Student("STU_048", "250610107048", "Prajapati Mohit Vijaybhai", "Computer Engineering", 3, Batch.CP3, "250610107048@gecp.ac.in"),
        Student("STU_049", "250610107049", "Prajapati Saloni Prakashbhai", "Computer Engineering", 3, Batch.CP3, "250610107049@gecp.ac.in"),
        Student("STU_050", "250610107050", "Prajapati Taruna Sureshbhai", "Computer Engineering", 3, Batch.CP3, "250610107050@gecp.ac.in"),
        Student("STU_051", "250610107051", "Prajapati Yash Nareshbhai", "Computer Engineering", 3, Batch.CP3, "250610107051@gecp.ac.in"),
        Student("STU_053", "250610107053", "Purohit Anilkumar Jagdishbhai", "Computer Engineering", 3, Batch.CP3, "250610107053@gecp.ac.in"),
        Student("STU_054", "250610107054", "Rathod Saumy Rajendrakumar", "Computer Engineering", 3, Batch.CP3, "250610107054@gecp.ac.in"),
        Student("STU_055", "250610107055", "Raval Jaykumar Bharatbhai", "Computer Engineering", 3, Batch.CP3, "250610107055@gecp.ac.in"),
        Student("STU_056", "250610107056", "Ribadiya Aryan Nileshbhai", "Computer Engineering", 3, Batch.CP3, "250610107056@gecp.ac.in"),
        Student("STU_057", "250610107057", "Sardhara Tiya Arunbhai", "Computer Engineering", 3, Batch.CP3, "250610107057@gecp.ac.in"),
        Student("STU_058", "250610107058", "Shah Ankit Vinod", "Computer Engineering", 3, Batch.CP3, "250610107058@gecp.ac.in"),
        Student("STU_059", "250610107059", "Shatish Rout", "Computer Engineering", 3, Batch.CP3, "250610107059@gecp.ac.in"),
        Student("STU_060", "250610107060", "Shrimali Dev Ashokbhai", "Computer Engineering", 3, Batch.CP3, "250610107060@gecp.ac.in"),
        Student("STU_061", "250610107061", "Shukla Himanshu Sushilkumar", "Computer Engineering", 3, Batch.CP3, "250610107061@gecp.ac.in"),
        Student("STU_063", "250610107063", "Solanki Prince Pradipkumar", "Computer Engineering", 3, Batch.CP3, "250610107063@gecp.ac.in"),
        Student("STU_066", "250610107066", "Suthar Jay Ashvinkumar", "Computer Engineering", 3, Batch.CP3, "250610107066@gecp.ac.in"),
        Student("STU_067", "250610107067", "Vaghela Hassan Nasirhusen", "Computer Engineering", 3, Batch.CP3, "250610107067@gecp.ac.in"),
        Student("STU_069", "250610107069", "Zala Tusharkumar Ishwarbhai", "Computer Engineering", 3, Batch.CP3, "250610107069@gecp.ac.in"),
        Student("STU_TMP14", "TMP 14", "PANCHAL AMIT MAHESHBHAI", "Computer Engineering", 3, Batch.CP3, "tmp14@gecp.ac.in"),
        Student("STU_TMP15", "TMP 15", "MEGHNATHI MANAV MAHESHGIRI", "Computer Engineering", 3, Batch.CP3, "tmp15@gecp.ac.in"),
        Student("STU_TMP16", "TMP 16", "THAKOR KAJALBEN BAKULJI", "Computer Engineering", 3, Batch.CP3, "tmp16@gecp.ac.in"),
        Student("STU_TMP17", "TMP 17", "VERMA ANJALI CHANDRABHAN", "Computer Engineering", 3, Batch.CP3, "tmp17@gecp.ac.in"),
        Student("STU_TMP18", "TMP 18", "PATEL RIYA PARESHKUMAR", "Computer Engineering", 3, Batch.CP3, "tmp18@gecp.ac.in"),
        Student("STU_TMP19", "TMP 19", "NIMJE MAHEK GIRISH", "Computer Engineering", 3, Batch.CP3, "tmp19@gecp.ac.in"),
        Student("STU_TMP20", "TMP 20", "BLOCH MARVA ABDULRAZAK", "Computer Engineering", 3, Batch.CP3, "tmp20@gecp.ac.in")
    )

    // =========================================================================
    // REAL SEM III TIMETABLE (GEC Palanpur - Computer Engineering Dept)
    // Periods:
    // P1 & P2: 10:30 - 12:30 (Morning block)
    // [Lunch Break: 12:30 - 13:00]
    // P3: 13:00 - 14:00 (1:00 PM to 2:00 PM)
    // P4: 14:00 - 15:00 (2:00 PM to 3:00 PM)
    // [Recess Break: 15:00 - 15:10]
    // P5 & P6: 15:10 - 17:10 (Afternoon block)
    // =========================================================================
    val TIMETABLE_SLOTS = listOf(
        // --- MONDAY ---
        TimetableSlot("MON-01", DayOfWeek.MONDAY, "10:30", "12:30", "DS", "PGV", "4111", Batch.CP1, SlotType.LAB),
        TimetableSlot("MON-02", DayOfWeek.MONDAY, "10:30", "12:30", "DS", "VF", "4111", Batch.CP2, SlotType.LAB),
        TimetableSlot("MON-03", DayOfWeek.MONDAY, "10:30", "12:30", "DBMS", "RS", "2101", Batch.CP3, SlotType.LAB),
        // Lunch 12:30 - 13:00
        TimetableSlot("MON-04", DayOfWeek.MONDAY, "13:00", "14:00", "DS", "SDJ", "8113", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("MON-05", DayOfWeek.MONDAY, "14:00", "15:00", "DBMS", "RS", "8113", Batch.ALL, SlotType.LECTURE),
        // Recess 15:00 - 15:10
        TimetableSlot("MON-06", DayOfWeek.MONDAY, "15:10", "17:10", "DF", "KMG", "8114", Batch.CP1, SlotType.LAB),
        TimetableSlot("MON-07", DayOfWeek.MONDAY, "15:10", "17:10", "SL_LIB", "NONE", "LIB", Batch.CP2, SlotType.TUTORIAL),
        TimetableSlot("MON-08", DayOfWeek.MONDAY, "15:10", "17:10", "DS", "SDJ", "4111", Batch.CP3, SlotType.LAB),

        // --- TUESDAY ---
        TimetableSlot("TUE-01", DayOfWeek.TUESDAY, "10:30", "12:30", "SL_LIB", "NONE", "LIB", Batch.CP1, SlotType.TUTORIAL),
        TimetableSlot("TUE-02", DayOfWeek.TUESDAY, "10:30", "12:30", "DBMS", "RS", "4111", Batch.CP2, SlotType.LAB),
        TimetableSlot("TUE-03", DayOfWeek.TUESDAY, "10:30", "12:30", "DS", "PGV", "2101", Batch.CP3, SlotType.LAB),
        // Lunch 12:30 - 13:00
        TimetableSlot("TUE-04", DayOfWeek.TUESDAY, "13:00", "14:00", "DBMS", "RS", "8113", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("TUE-05", DayOfWeek.TUESDAY, "14:00", "15:00", "PCE", "SLM", "8113", Batch.ALL, SlotType.LECTURE),
        // Recess 15:00 - 15:10
        TimetableSlot("TUE-06", DayOfWeek.TUESDAY, "15:10", "17:10", "PCE", "SLM", "2101", Batch.CP1, SlotType.TUTORIAL),
        TimetableSlot("TUE-07", DayOfWeek.TUESDAY, "15:10", "17:10", "DF", "KMG", "8114", Batch.CP2, SlotType.LAB),
        TimetableSlot("TUE-08", DayOfWeek.TUESDAY, "15:10", "17:10", "DF", "VF", "8114", Batch.CP3, SlotType.LAB),

        // --- WEDNESDAY ---
        TimetableSlot("WED-01", DayOfWeek.WEDNESDAY, "10:30", "11:30", "DS", "PGV", "8113", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("WED-02", DayOfWeek.WEDNESDAY, "11:30", "12:30", "DBMS", "RS", "8113", Batch.ALL, SlotType.LECTURE),
        // Lunch 12:30 - 13:00
        TimetableSlot("WED-03", DayOfWeek.WEDNESDAY, "13:00", "14:00", "DF", "KMG", "8113", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("WED-04", DayOfWeek.WEDNESDAY, "14:00", "15:00", "PCE", "SLM", "8113", Batch.ALL, SlotType.LECTURE),
        // Recess 15:00 - 15:10
        TimetableSlot("WED-05", DayOfWeek.WEDNESDAY, "15:10", "17:10", "PS", "DAP", "8113", Batch.CP1, SlotType.TUTORIAL),
        TimetableSlot("WED-06", DayOfWeek.WEDNESDAY, "15:10", "17:10", "PS", "VF", "8113", Batch.CP2, SlotType.TUTORIAL),
        TimetableSlot("WED-07", DayOfWeek.WEDNESDAY, "15:10", "17:10", "PS", "VF", "8113", Batch.CP3, SlotType.TUTORIAL),

        // --- THURSDAY ---
        TimetableSlot("THU-01", DayOfWeek.THURSDAY, "10:30", "12:30", "DS", "SDJ", "4111", Batch.CP1, SlotType.LAB),
        TimetableSlot("THU-02", DayOfWeek.THURSDAY, "10:30", "12:30", "DS", "VF", "4111", Batch.CP2, SlotType.LAB),
        TimetableSlot("THU-03", DayOfWeek.THURSDAY, "10:30", "12:30", "PCE", "SLM", "2101", Batch.CP3, SlotType.TUTORIAL),
        // Lunch 12:30 - 13:00
        TimetableSlot("THU-04", DayOfWeek.THURSDAY, "13:00", "14:00", "DF", "KMG", "8113", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("THU-05", DayOfWeek.THURSDAY, "14:00", "15:00", "DS", "PGV", "8113", Batch.ALL, SlotType.LECTURE),
        // Recess 15:00 - 15:10
        TimetableSlot("THU-06", DayOfWeek.THURSDAY, "15:10", "16:10", "PS", "DAP", "8012", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("THU-07", DayOfWeek.THURSDAY, "16:10", "17:10", "PS", "VF", "8012", Batch.ALL, SlotType.LECTURE),

        // --- FRIDAY ---
        TimetableSlot("FRI-01", DayOfWeek.FRIDAY, "10:30", "12:30", "DBMS", "RS", "4111", Batch.CP1, SlotType.LAB),
        TimetableSlot("FRI-02", DayOfWeek.FRIDAY, "10:30", "12:30", "PCE", "SLM", "2101", Batch.CP2, SlotType.TUTORIAL),
        TimetableSlot("FRI-03", DayOfWeek.FRIDAY, "10:30", "12:30", "SL_LIB", "NONE", "LIB", Batch.CP3, SlotType.TUTORIAL),
        // Lunch 12:30 - 13:00
        TimetableSlot("FRI-04", DayOfWeek.FRIDAY, "13:00", "14:00", "DF", "KMG", "8113", Batch.ALL, SlotType.LECTURE),
        TimetableSlot("FRI-05", DayOfWeek.FRIDAY, "14:00", "15:00", "PS", "DAP", "7012", Batch.ALL, SlotType.LECTURE),
        // Recess 15:00 - 15:10
        TimetableSlot("FRI-06", DayOfWeek.FRIDAY, "15:10", "17:10", "IC", "CGP", "8113", Batch.ALL, SlotType.LECTURE),

        // --- SATURDAY ---
        TimetableSlot("SAT-01", DayOfWeek.SATURDAY, "10:30", "12:30", "SL_LIB", "NONE", "LIB", Batch.ALL, SlotType.TUTORIAL)
    )

    val INITIAL_CHANGES = listOf(
        TimetableChange(
            id = "CHG-001",
            slotId = "MON-04",
            day = DayOfWeek.MONDAY,
            subjectCode = "DS",
            type = ChangeType.ROOM_CHANGE,
            newClassroomId = "2101",
            reason = "Projector maintenance in Hall 8113; relocated to Room 2101",
            active = true,
            createdAt = "Today"
        ),
        TimetableChange(
            id = "CHG-002",
            slotId = "WED-01",
            day = DayOfWeek.WEDNESDAY,
            subjectCode = "DS",
            type = ChangeType.FACULTY_SUBSTITUTE,
            newFacultyId = "SDJ",
            reason = "PGV on administrative duty; SDJ substituting",
            active = true,
            createdAt = "Today"
        )
    )

    val HOLIDAYS = listOf(
        Holiday("HOL-001", "2026-10-02", "Gandhi Jayanti", "National Holiday"),
        Holiday("HOL-002", "2026-10-20", "Dussehra / Vijayadashami", "Institute Holiday"),
        Holiday("HOL-003", "2026-11-08", "Diwali Vacation", "Academic Festival Vacation")
    )
}
