package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Student::class,
        Teacher::class,
        Attendance::class,
        PaymentInstallment::class,
        TransportRoute::class,
        StudentGrade::class,
        Homework::class,
        Notice::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun paymentDao(): PaymentDao
    abstract fun transportRouteDao(): TransportRouteDao
    abstract fun studentGradeDao(): StudentGradeDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun noticeDao(): NoticeDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "school_erp_database"
                )
                .addCallback(SchoolDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SchoolDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(db: SchoolDatabase) {
            // Seed Routes
            val route1Id = db.transportRouteDao().insertRoute(
                TransportRoute(
                    name = "Route A - Noida Sec 62 & Indirapuram",
                    driverName = "Gopal Singh",
                    driverLicense = "DL-12X2024098",
                    licenseExpiry = "2029-05-12",
                    vehicleNo = "UP-16-BT-9980",
                    feeSlab = 1800.0
                )
            ).toInt()

            val route2Id = db.transportRouteDao().insertRoute(
                TransportRoute(
                    name = "Route B - Dwarka Sector 10 & 21",
                    driverName = "Satish Kumar",
                    driverLicense = "DL-03W2021005",
                    licenseExpiry = "2027-11-20",
                    vehicleNo = "DL-1CA-2010",
                    feeSlab = 2500.0
                )
            ).toInt()

            // Seed Students
            val s1 = db.studentDao().insertStudent(
                Student(
                    name = "Aarav Sharma",
                    rollNo = "101",
                    gradeClass = "Class 10",
                    section = "A",
                    parentName = "Rajesh Sharma",
                    parentPhone = "+919876543210",
                    parentEmail = "sharma.family@outlook.com",
                    medicalHistory = "None - Shellfish Allergy",
                    rteQuota = false,
                    enrollmentStage = "Finalized",
                    transportRouteId = route1Id
                )
            ).toInt()

            val s2 = db.studentDao().insertStudent(
                Student(
                    name = "Arjun Verma",
                    rollNo = "102",
                    gradeClass = "Class 10",
                    section = "A",
                    parentName = "Suresh Verma",
                    parentPhone = "+918123456789",
                    parentEmail = "suresh.v@gmail.com",
                    medicalHistory = "None",
                    rteQuota = true, // Right to Education Quota (Mandatory 25% tracking in Indian Schools)
                    enrollmentStage = "Finalized",
                    transportRouteId = route2Id
                )
            ).toInt()

            val s3 = db.studentDao().insertStudent(
                Student(
                    name = "Ishita Iyer",
                    rollNo = "1201",
                    gradeClass = "Class 12",
                    section = "B",
                    parentName = "Kalyan Iyer",
                    parentPhone = "+919999888777",
                    parentEmail = "kalyaniyer@yahoo.co.in",
                    medicalHistory = "Asthmatic",
                    rteQuota = false,
                    enrollmentStage = "Finalized",
                    transportRouteId = route1Id
                )
            ).toInt()

            // Admissions pipeline enquiry states (RTE checks and leads)
            db.studentDao().insertStudent(
                Student(
                    name = "Siddharth Malhotra",
                    rollNo = "N/A",
                    gradeClass = "Class 10",
                    section = "Pending",
                    parentName = "Sameer Malhotra",
                    parentPhone = "+919811554422",
                    parentEmail = "sam.malhotra@gmail.com",
                    rteQuota = false,
                    enrollmentStage = "Interview", // Admissions stage
                    aadharSubmitted = true,
                    birthCertSubmitted = false,
                    previousMarksSubmitted = true,
                    transportRouteId = 0
                )
            )

            db.studentDao().insertStudent(
                Student(
                    name = "Pooja Banerjee",
                    rollNo = "N/A",
                    gradeClass = "Class 11",
                    section = "Pending",
                    parentName = "Subrato Banerjee",
                    parentPhone = "+919911991199",
                    parentEmail = "banerjee.s@mail.ru",
                    rteQuota = true,
                    enrollmentStage = "Assessment", // Admissions stage
                    aadharSubmitted = true,
                    birthCertSubmitted = true,
                    previousMarksSubmitted = false,
                    transportRouteId = 0
                )
            )

            // Seed Teachers
            db.teacherDao().insertTeacher(
                Teacher(
                    name = "Mrs. Sunita Sharma",
                    subject = "Mathematics",
                    phone = "+919810054321",
                    email = "sunita.sharma@school.edu.in",
                    basicSalary = 48000.0,
                    leavesTaken = 1,
                    totalAllowedLeaves = 12,
                    pfDeducted = 1800.0,
                    biometricRegistered = true
                )
            )

            db.teacherDao().insertTeacher(
                Teacher(
                    name = "Mr. K. Pillai",
                    subject = "Physics",
                    phone = "+919830099887",
                    email = "krishna.pillai@school.edu.in",
                    basicSalary = 52000.0,
                    leavesTaken = 0,
                    totalAllowedLeaves = 12,
                    pfDeducted = 1800.0,
                    biometricRegistered = true
                )
            )

            db.teacherDao().insertTeacher(
                Teacher(
                    name = "Miss Ananya Sen",
                    subject = "English Literature",
                    phone = "+919899011223",
                    email = "ananya.sen@school.edu.in",
                    basicSalary = 44000.0,
                    leavesTaken = 3,
                    totalAllowedLeaves = 12,
                    pfDeducted = 1800.0,
                    biometricRegistered = false
                )
            )

            // Seed Payments
            db.paymentDao().insertPayment(
                PaymentInstallment(
                    studentId = s1,
                    amountPaid = 18500.0, // Q1 (Tuition + annual charger)
                    paymentDate = "2026-04-10",
                    method = "UPI (Razorpay)",
                    installmentNumber = 1,
                    status = "Paid",
                    receiptNumber = "REC-2026-09881",
                    penaltyAmount = 0.0,
                    gstAmount = 880.95
                )
            )

            db.paymentDao().insertPayment(
                PaymentInstallment(
                    studentId = s1,
                    amountPaid = 15000.0, // Q2 Tuition
                    paymentDate = "2026-07-05",
                    method = "Net Banking",
                    installmentNumber = 2,
                    status = "Paid",
                    receiptNumber = "REC-2026-11234",
                    penaltyAmount = 0.0,
                    gstAmount = 714.28
                )
            )

            db.paymentDao().insertPayment(
                PaymentInstallment(
                    studentId = s2,
                    amountPaid = 4500.0, // Q1 (RTE rate is heavily discounted or sponsored)
                    paymentDate = "2026-04-15",
                    method = "Cash",
                    installmentNumber = 1,
                    status = "Paid",
                    receiptNumber = "REC-RTE-0012",
                    penaltyAmount = 0.0,
                    gstAmount = 0.0
                )
            )

            // Defaulters (Aarav is pending Q3, Arjun is pending Q2, Q3)
            db.paymentDao().insertPayment(
                PaymentInstallment(
                    studentId = s1,
                    amountPaid = 15000.0, 
                    paymentDate = "Pending",
                    method = "Razorpay",
                    installmentNumber = 3,
                    status = "Pending",
                    receiptNumber = "N/A"
                )
            )

            db.paymentDao().insertPayment(
                PaymentInstallment(
                    studentId = s3,
                    amountPaid = 22000.0,
                    paymentDate = "Pending",
                    method = "UPI/Net Banking",
                    installmentNumber = 2, // Quarter 2 Dues 
                    status = "Defaulter", // Flagged as absolute defaulter (quarter was due in July)
                    receiptNumber = "N/A",
                    penaltyAmount = 450.0 // late fee accumulated 
                )
            )

            // Seed Attendance 
            db.attendanceDao().insertAttendance(Attendance(studentId = s1, dateString = "2026-06-14", status = "Present"))
            db.attendanceDao().insertAttendance(Attendance(studentId = s2, dateString = "2026-06-14", status = "Present"))
            db.attendanceDao().insertAttendance(Attendance(studentId = s3, dateString = "2026-06-14", status = "Absent", smsSent = true))

            db.attendanceDao().insertAttendance(Attendance(studentId = s1, dateString = "2026-06-13", status = "Present"))
            db.attendanceDao().insertAttendance(Attendance(studentId = s2, dateString = "2026-06-13", status = "Late"))
            db.attendanceDao().insertAttendance(Attendance(studentId = s3, dateString = "2026-06-13", status = "Present"))

            // Seed Grades strictly following CBSE structures (Theory out of 80, Internals check out of 20)
            db.studentGradeDao().insertGrade(StudentGrade(studentId = s1, subject = "Mathematics", examTerm = "Term 1", theoryMarks = 74, internalMarks = 18, gradeChar = "A1"))
            db.studentGradeDao().insertGrade(StudentGrade(studentId = s1, subject = "Science", examTerm = "Term 1", theoryMarks = 71, internalMarks = 19, gradeChar = "A1"))
            db.studentGradeDao().insertGrade(StudentGrade(studentId = s1, subject = "Social Studies", examTerm = "Term 1", theoryMarks = 68, internalMarks = 18, gradeChar = "A2"))

            db.studentGradeDao().insertGrade(StudentGrade(studentId = s2, subject = "Mathematics", examTerm = "Term 1", theoryMarks = 52, internalMarks = 14, gradeChar = "B2"))
            db.studentGradeDao().insertGrade(StudentGrade(studentId = s2, subject = "Science", examTerm = "Term 1", theoryMarks = 58, internalMarks = 16, gradeChar = "B1"))

            // Seed Homework lists
            db.homeworkDao().insertHomework(
                Homework(
                    className = "Class 10",
                    subject = "Mathematics",
                    title = "Trigonometrical Identities & Formulae",
                    instructions = "Solve Exercise 8.4 questions 1-5 from NCERT. Write in detailed proof format in your homework registers.",
                    dueDate = "2026-06-18"
                )
            )

            db.homeworkDao().insertHomework(
                Homework(
                    className = "Class 10",
                    subject = "English",
                    title = "Letter Writing Practice",
                    instructions = "Write a letter to the editor of a local national daily in Noida expressing concern over the growing traffic layout.",
                    dueDate = "2026-06-16"
                )
            )

            // Seed Notices
            db.noticeDao().insertNotice(
                Notice(
                    title = "Summer Vacation Extends to 20th June",
                    description = "Owing to the extreme heat waves and DM advisory across Delhi NCR region, the school sessions will resume in physical state starting Monday, 22nd June 2026.",
                    category = "Urgent",
                    dateString = "2026-06-10"
                )
            )

            db.noticeDao().insertNotice(
                Notice(
                    title = "Uniform & Attendance Compliance",
                    description = "Parents are requested to ensure their wards attend in complete navy-blue uniform with proper shoes in matching configurations starting Next Term.",
                    category = "General",
                    dateString = "2026-06-08"
                )
            )
        }
    }
}
