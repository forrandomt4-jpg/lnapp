import sqlite3

def init_db():
    conn = sqlite3.connect('dev.db')
    cursor = conn.cursor()

    cursor.execute('''
    CREATE TABLE IF NOT EXISTS "Student" (
        "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
        "enrollmentNumber" TEXT NOT NULL,
        "name" TEXT NOT NULL,
        "branch" TEXT NOT NULL,
        "semester" INTEGER NOT NULL,
        "batch" TEXT NOT NULL
    )
    ''')
    cursor.execute('CREATE UNIQUE INDEX IF NOT EXISTS "Student_enrollmentNumber_key" ON "Student"("enrollmentNumber")')

    cursor.execute('''
    CREATE TABLE IF NOT EXISTS "TimetableEntry" (
        "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
        "batch" TEXT NOT NULL,
        "subject" TEXT NOT NULL,
        "faculty" TEXT,
        "classroom" TEXT,
        "dayOfWeek" INTEGER NOT NULL,
        "startTime" TEXT NOT NULL,
        "endTime" TEXT NOT NULL
    )
    ''')

    cursor.execute('''
    CREATE TABLE IF NOT EXISTS "TimetableChange" (
        "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
        "batch" TEXT NOT NULL,
        "date" TEXT NOT NULL,
        "startTime" TEXT NOT NULL,
        "endTime" TEXT NOT NULL,
        "subject" TEXT,
        "faculty" TEXT,
        "classroom" TEXT,
        "isCancelled" BOOLEAN NOT NULL DEFAULT 0
    )
    ''')

    # Data
    students = [
        ("250610107001", "Bochal Khushiben Jamsubhai", "CP1"),
        ("250610107002", "Borse Riya Mukesh", "CP1"),
        ("250610107003", "Chaudhari Akansha Vishnubhai", "CP1"),
        ("250610107004", "Chaudhary Hiteshbhai Bhvabhai", "CP1"),
        ("250610107005", "Chaudhary Krishkumar Faljibhai", "CP1"),
        ("250610107006", "Chaudhary Niruben Rameshbhai", "CP1"),
        ("250610107007", "Chaudhary Pirabhai Harsengbhai", "CP1"),
        ("250610107008", "Chaudhary Rajeshbhai Ajababhai", "CP1"),
        ("250610107009", "Desai Angel Amitbhai", "CP1"),
        ("250610107010", "Dubey Piyushkumar Udayshankar", "CP1"),
        ("250610107012", "Gusai Kavypuri", "CP1"),
        ("250610107014", "Jiya Pravinbhai Vachhani", "CP1"),
        ("250610107015", "Joshi Raghav Rajeshkumar", "CP1"),
        ("250610107016", "Kapadiya Manav Bharatkumar", "CP1"),
        ("250610107017", "Kherala Meetkumar Shaileshbhai", "CP1"),
        ("250610107018", "Khushbuben Patel", "CP1"),
        ("250610107019", "Makwana Kavy Dilipkumar", "CP1"),
        ("250610107020", "Makwana Naincy Jagdishbhai", "CP1"),
        ("250610107021", "Mayank Kumawat", "CP1"),
        ("250610107022", "Meetrajsinh Rana", "CP1"),
        ("TMP 1", "MAKNOJIYA ARMAN IMRANBHAI", "CP1"),
        ("TMP 2", "ANSARI MOHAMMAD SADIK ANSAR AHMED", "CP1"),
        ("TMP 3", "CHANDAN", "CP1"),
        ("TMP 4", "PRAJAPATI DIXIT KIRTIBHAI", "CP1"),
        ("TMP 5", "PATEL SATYADAS ARVINDBHAI", "CP1"),
        ("TMP 6", "DARJI HARDIK DILIPBHAI", "CP1"),

        ("250610107023", "Mehsaniya Ikram Yunus", "CP2"),
        ("250610107024", "Mehta Ishteyaqali Mehdihasan", "CP2"),
        ("250610107026", "Nangash Bhara Somabhai", "CP2"),
        ("250610107027", "Nasit Prisha Mukesh", "CP2"),
        ("250610107028", "Nayi Shreejaben Sanjaybhai", "CP2"),
        ("250610107029", "Nimavat Krish Dineshbhai", "CP2"),
        ("250610107030", "Panda Roshon Kalia", "CP2"),
        ("250610107031", "Pandya Divyam Rameshbhai", "CP2"),
        ("250610107032", "Parikh Marmik Naginbhai", "CP2"),
        ("250610107034", "Parmar Pujaben Kiransinh", "CP2"),
        ("250610107035", "Parmar Riteshkumar Kantibhai", "CP2"),
        ("250610107036", "Parthiv Pravin Sorathia", "CP2"),
        ("250610107037", "Patel Dax Pravinbhai", "CP2"),
        ("250610107038", "Patel Lav Ashokkumar", "CP2"),
        ("250610107040", "Patel Rudra", "CP2"),
        ("250610107041", "Patel Shreya Bharatkumar", "CP2"),
        ("250610107042", "Patel Yugkumar Rakeshbhai", "CP2"),
        ("250610107043", "Patni Keval Kishanbhai", "CP2"),

        ("250610107044", "Prajapati Amiben Bhurabhai", "CP3"),
        ("250610107045", "Prajapati Chiragkumar", "CP3"),
        ("TMP 7", "MODH VANSH BHARATKUMAR", "CP3"),
        ("TMP 8", "MEVADA HASIT RUPESHKUMAR", "CP3"),
        ("TMP 9", "PATEL MITRA DINESHKUMAR", "CP3"),
        ("TMP 10", "PATEL DAX BHARATBHAI", "CP3"),
        ("TMP 11", "PATEL PRINCE PRAVINBHAI", "CP3"),
        ("TMP 12", "RAJPUT SUSHANT RAJESHBHAI", "CP3"),
        ("TMP 13", "PATEL SIDDH JIGNESHKUMAR", "CP3"),
        ("250610107046", "Prajapati Dhrumilkumar Anilkumar", "CP3"),
        ("250610107047", "Prajapati Dhrumitkumar Bharatbhai", "CP3"),
        ("250610107048", "Prajapati Mohit Vijaybhai", "CP3"),
        ("250610107049", "Prajapati Saloni Prakashbhai", "CP3"),
        ("250610107050", "Prajapati Taruna Sureshbhai", "CP3"),
        ("250610107051", "Prajapati Yash Nareshbhai", "CP3"),
        ("250610107053", "Purohit Anilkumar Jagdishbhai", "CP3"),
        ("250610107054", "Rathod Saumy Rajendrakumar", "CP3"),
        ("250610107055", "Raval Jaykumar Bharatbhai", "CP3"),
        ("250610107056", "Ribadiya Aryan Nileshbhai", "CP3"),
        ("250610107057", "Sardhara Tiya Arunbhai", "CP3"),
        ("250610107058", "Shah Ankit Vinod", "CP3"),
        ("250610107059", "Shatish Rout", "CP3"),
        ("250610107060", "Shrimali Dev Ashokbhai", "CP3"),
        ("250610107061", "Shukla Himanshu Sushilkumar", "CP3"),
        ("250610107063", "Solanki Prince Pradipkumar", "CP3"),
        ("250610107066", "Suthar Jay Ashvinkumar", "CP3"),
        ("250610107067", "Vaghela Hassan Nasirhusen", "CP3"),
        ("250610107069", "Zala Tusharkumar Ishwarbhai", "CP3"),
        ("TMP 14", "PANCHAL AMIT MAHESHBHAI", "CP3"),
        ("TMP 15", "MEGHNATHI MANAV MAHESHGIRI", "CP3"),
        ("TMP 16", "THAKOR KAJALBEN BAKULJI", "CP3"),
        ("TMP 17", "VERMA ANJALI CHANDRABHAN", "CP3"),
        ("TMP 18", "PATEL RIYA PARESHKUMAR", "CP3"),
        ("TMP 19", "NIMJE MAHEK GIRISH", "CP3"),
        ("TMP 20", "BLOCH MARVA ABDULRAZAK", "CP3"),
    ]

    for enroll, name, batch in students:
        cursor.execute('''
        INSERT OR IGNORE INTO Student (enrollmentNumber, name, branch, semester, batch)
        VALUES (?, ?, 'Computer Engineering', 3, ?)
        ''', (enroll, name, batch))
    
    # Let's seed Timetable (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat)
    # TimetableEntry: batch, subject, faculty, classroom, dayOfWeek, startTime, endTime
    timetable_data = [
        # MONDAY
        ("CP1", "DS", "PGV", "4111", 1, "10:30", "12:30"),
        ("CP2", "DS", "VF", "4111", 1, "10:30", "12:30"),
        ("CP3", "DBMS", "RS", "2101", 1, "10:30", "12:30"),
        ("ALL", "DS", "SDJ", "8113", 1, "13:00", "14:00"),
        ("ALL", "DBMS", "RS", "8113", 1, "14:00", "15:00"),
        ("CP1", "DF", "KMG", "8114", 1, "15:10", "17:10"),
        ("CP2", "S.L. / LIB.", None, None, 1, "15:10", "17:10"),
        ("CP3", "DS", "SDJ", "4111", 1, "15:10", "17:10"),

        # TUESDAY
        ("CP1", "S.L. / LIB.", None, None, 2, "10:30", "12:30"),
        ("CP2", "DBMS", "RS", "4111", 2, "10:30", "12:30"),
        ("CP3", "DS", "PGV", "2101", 2, "10:30", "12:30"),
        ("ALL", "DBMS", "RS", "8113", 2, "13:00", "14:00"),
        ("ALL", "PCE", "SLM", "8113", 2, "14:00", "15:00"),
        ("CP1", "PCE", "SLM", "2101", 2, "15:10", "17:10"),
        ("CP2", "DF", "KMG", "8114", 2, "15:10", "17:10"),
        ("CP3", "DF", "VF", "8114", 2, "15:10", "17:10"),

        # WEDNESDAY
        ("ALL", "DS", "PGV", "8113", 3, "10:30", "11:30"),
        ("ALL", "DBMS", "RS", "8113", 3, "11:30", "12:30"),
        ("ALL", "DF", "KMG", "8113", 3, "13:00", "14:00"),
        ("ALL", "PCE", "SLM", "8113", 3, "14:00", "15:00"),
        ("CP1", "PS", "DAP", "8113", 3, "15:10", "17:10"),
        ("CP2", "PS", "VF", "8113", 3, "15:10", "17:10"),
        ("CP3", "PS", "VF", "8113", 3, "15:10", "17:10"),

        # THURSDAY
        ("CP1", "DS", "SDJ", "4111", 4, "10:30", "12:30"),
        ("CP2", "DS", "VF", "4111", 4, "10:30", "12:30"),
        ("CP3", "PCE", "SLM", "2101", 4, "10:30", "12:30"),
        ("ALL", "DF", "KMG", "8113", 4, "13:00", "14:00"),
        ("ALL", "DS", "PGV", "8113", 4, "14:00", "15:00"),
        ("ALL", "PS", "DAP", "8012", 4, "15:10", "16:10"),
        ("ALL", "PS", "VF", "8012", 4, "16:10", "17:10"),

        # FRIDAY
        ("CP1", "DBMS", "RS", "4111", 5, "10:30", "12:30"),
        ("CP2", "PCE", "SLM", "2101", 5, "10:30", "12:30"),
        ("CP3", "S.L. / LIB.", None, None, 5, "10:30", "12:30"),
        ("ALL", "DF", "KMG", "8113", 5, "13:00", "14:00"),
        ("ALL", "PS", "DAP", "7012", 5, "14:00", "15:00"),
        ("ALL", "IC", "CGP", "8113", 5, "15:10", "17:10"),
    ]

    cursor.execute('DELETE FROM TimetableEntry')
    for batch, subject, faculty, classroom, day, start, end in timetable_data:
        cursor.execute('''
        INSERT INTO TimetableEntry (batch, subject, faculty, classroom, dayOfWeek, startTime, endTime)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        ''', (batch, subject, faculty, classroom, day, start, end))

    conn.commit()
    conn.close()
    print("Database seeded!")

if __name__ == "__main__":
    init_db()
