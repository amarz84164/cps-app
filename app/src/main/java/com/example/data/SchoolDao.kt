package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY name ASC")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: Int): Teacher?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE dateString = :date ORDER BY id DESC")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance WHERE studentId = :studentId AND dateString = :date")
    suspend fun deleteAttendanceForDay(studentId: Int, date: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY id DESC")
    fun getAllPayments(): Flow<List<PaymentInstallment>>

    @Query("SELECT * FROM payments WHERE studentId = :studentId")
    fun getPaymentsForStudent(studentId: Int): Flow<List<PaymentInstallment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentInstallment): Long

    @Update
    suspend fun updatePayment(payment: PaymentInstallment)
}

@Dao
interface TransportRouteDao {
    @Query("SELECT * FROM transport_routes ORDER BY name ASC")
    fun getAllRoutes(): Flow<List<TransportRoute>>

    @Query("SELECT * FROM transport_routes WHERE id = :id")
    suspend fun getRouteById(id: Int): TransportRoute?

    @Query("SELECT * FROM transport_routes WHERE id IN (:ids)")
    suspend fun getRoutesByIds(ids: List<Int>): List<TransportRoute>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: TransportRoute): Long

    @Delete
    suspend fun deleteRoute(route: TransportRoute)
}

@Dao
interface StudentGradeDao {
    @Query("SELECT * FROM grades WHERE studentId = :studentId")
    fun getGradesForStudent(studentId: Int): Flow<List<StudentGrade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: StudentGrade): Long

    @Query("DELETE FROM grades WHERE studentId = :studentId")
    suspend fun clearGradesForStudent(studentId: Int)
}

@Dao
interface HomeworkDao {
    @Query("SELECT * FROM homework ORDER BY id DESC")
    fun getAllHomework(): Flow<List<Homework>>

    @Query("SELECT * FROM homework WHERE className = :className ORDER BY id DESC")
    fun getHomeworkForClass(className: String): Flow<List<Homework>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: Homework): Long
}

@Dao
interface NoticeDao {
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice): Long

    @Delete
    suspend fun deleteNotice(notice: Notice)
}
