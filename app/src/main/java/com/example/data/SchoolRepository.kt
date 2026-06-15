package com.example.data

import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val db: SchoolDatabase) {

    // Students
    val allStudents: Flow<List<Student>> = db.studentDao().getAllStudents()
    suspend fun getStudentById(id: Int): Student? = db.studentDao().getStudentById(id)
    suspend fun insertStudent(student: Student): Long = db.studentDao().insertStudent(student)
    suspend fun updateStudent(student: Student) = db.studentDao().updateStudent(student)
    suspend fun deleteStudent(student: Student) = db.studentDao().deleteStudent(student)

    // Teachers
    val allTeachers: Flow<List<Teacher>> = db.teacherDao().getAllTeachers()
    suspend fun getTeacherById(id: Int): Teacher? = db.teacherDao().getTeacherById(id)
    suspend fun insertTeacher(teacher: Teacher): Long = db.teacherDao().insertTeacher(teacher)
    suspend fun updateTeacher(teacher: Teacher) = db.teacherDao().updateTeacher(teacher)
    suspend fun deleteTeacher(teacher: Teacher) = db.teacherDao().deleteTeacher(teacher)

    // Attendance
    fun getAttendanceForDate(date: String): Flow<List<Attendance>> = db.attendanceDao().getAttendanceForDate(date)
    fun getAttendanceForStudent(id: Int): Flow<List<Attendance>> = db.attendanceDao().getAttendanceForStudent(id)
    suspend fun insertAttendance(attendance: Attendance) = db.attendanceDao().insertAttendance(attendance)
    suspend fun deleteAttendanceForDay(studentId: Int, date: String) = db.attendanceDao().deleteAttendanceForDay(studentId, date)

    // Payments / Fee Installments
    val allPayments: Flow<List<PaymentInstallment>> = db.paymentDao().getAllPayments()
    fun getPaymentsForStudent(studentId: Int): Flow<List<PaymentInstallment>> = db.paymentDao().getPaymentsForStudent(studentId)
    suspend fun insertPayment(payment: PaymentInstallment): Long = db.paymentDao().insertPayment(payment)
    suspend fun updatePayment(payment: PaymentInstallment) = db.paymentDao().updatePayment(payment)

    // Transport Routes
    val allRoutes: Flow<List<TransportRoute>> = db.transportRouteDao().getAllRoutes()
    suspend fun getRouteById(id: Int): TransportRoute? = db.transportRouteDao().getRouteById(id)
    suspend fun getRoutesByIds(ids: List<Int>): List<TransportRoute> = db.transportRouteDao().getRoutesByIds(ids)
    suspend fun insertRoute(route: TransportRoute): Long = db.transportRouteDao().insertRoute(route)
    suspend fun deleteRoute(route: TransportRoute) = db.transportRouteDao().deleteRoute(route)

    // Grades / Academy report cards
    fun getGradesForStudent(studentId: Int): Flow<List<StudentGrade>> = db.studentGradeDao().getGradesForStudent(studentId)
    suspend fun insertGrade(grade: StudentGrade): Long = db.studentGradeDao().insertGrade(grade)
    suspend fun clearGradesForStudent(studentId: Int) = db.studentGradeDao().clearGradesForStudent(studentId)

    // Homework
    val allHomework: Flow<List<Homework>> = db.homeworkDao().getAllHomework()
    fun getHomeworkForClass(className: String): Flow<List<Homework>> = db.homeworkDao().getHomeworkForClass(className)
    suspend fun insertHomework(homework: Homework): Long = db.homeworkDao().insertHomework(homework)

    // Notices
    val allNotices: Flow<List<Notice>> = db.noticeDao().getAllNotices()
    suspend fun insertNotice(notice: Notice): Long = db.noticeDao().insertNotice(notice)
    suspend fun deleteNotice(notice: Notice) = db.noticeDao().deleteNotice(notice)
}
