package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class UserRole(val displayName: String) {
    SUPER_ADMIN("Super Admin / Principal"),
    ADMIN("Admin / Front Desk"),
    TEACHER("Mrs. Teacher"),
    PARENT("Parent / Student")
}

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SchoolDatabase.getDatabase(application, viewModelScope)
    private val repository = SchoolRepository(db)

    // Current State role
    private val _currentRole = MutableStateFlow(UserRole.SUPER_ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Active Selection details for Parent view (Switch between student Aarav vs Arjun vs Ishita)
    private val _selectedStudentIdForParent = MutableStateFlow(1)
    val selectedStudentIdForParent: StateFlow<Int> = _selectedStudentIdForParent.asStateFlow()

    // Date for Attendance module
    private val _attendanceDate = MutableStateFlow(getCurrentDateString())
    val attendanceDate: StateFlow<String> = _attendanceDate.asStateFlow()

    // Observable states from DB
    val students: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teachers: StateFlow<List<Teacher>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routes: StateFlow<List<TransportRoute>> = repository.allRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentInstallment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val homework: StateFlow<List<Homework>> = repository.allHomework
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback notifications
    private val _smsBroadcastFeedback = MutableSharedFlow<String>()
    val smsBroadcastFeedback: SharedFlow<String> = _smsBroadcastFeedback.asSharedFlow()

    private val _paymentReceiptFeedback = MutableSharedFlow<String>()
    val paymentReceiptFeedback: SharedFlow<String> = _paymentReceiptFeedback.asSharedFlow()

    fun setRole(role: UserRole) {
        _currentRole.value = role
        // Default selected student when parent role activates
        if (role == UserRole.PARENT && students.value.isNotEmpty()) {
            _selectedStudentIdForParent.value = students.value.first().id
        }
    }

    fun selectParentStudent(studentId: Int) {
        _selectedStudentIdForParent.value = studentId
    }

    fun setAttendanceDate(date: String) {
        _attendanceDate.value = date
    }

    // --- DB Flow Queries ---
    fun getPaymentsForStudent(studentId: Int): Flow<List<PaymentInstallment>> = repository.getPaymentsForStudent(studentId)
    fun getGradesForStudent(studentId: Int): Flow<List<StudentGrade>> = repository.getGradesForStudent(studentId)
    fun getHomeworkForClass(className: String): Flow<List<Homework>> = repository.getHomeworkForClass(className)
    fun getAttendanceForDate(date: String): Flow<List<Attendance>> = repository.getAttendanceForDate(date)
    fun getAttendanceForStudent(studentId: Int): Flow<List<Attendance>> = repository.getAttendanceForStudent(studentId)

    // --- Admission Workflow (SIS) ---
    fun admitNewStudent(
        name: String,
        gradeClass: String,
        section: String,
        parentName: String,
        parentPhone: String,
        parentEmail: String,
        medicalHistory: String,
        rteQuota: Boolean,
        hasAadhar: Boolean,
        hasBirthCert: Boolean,
        hasPrevMarks: Boolean,
        routeId: Int,
        enrollmentStage: String = "Finalized"
    ) {
        viewModelScope.launch {
            val randomRoll = (103..999).random().toString()
            val newStudent = Student(
                name = name,
                rollNo = if (enrollmentStage == "Finalized") randomRoll else "Pending",
                gradeClass = gradeClass,
                section = if (enrollmentStage == "Finalized") section else "Pending",
                parentName = parentName,
                parentPhone = parentPhone,
                parentEmail = parentEmail,
                medicalHistory = medicalHistory,
                rteQuota = rteQuota,
                enrollmentStage = enrollmentStage,
                aadharSubmitted = hasAadhar,
                birthCertSubmitted = hasBirthCert,
                previousMarksSubmitted = hasPrevMarks,
                transportRouteId = routeId
            )
            repository.insertStudent(newStudent)
            _smsBroadcastFeedback.emit("Admission enquiry saved for $name! Stage: $enrollmentStage")
        }
    }

    fun updateEnrollmentStage(student: Student, nextStage: String) {
        viewModelScope.launch {
            val newRoll = if (nextStage == "Finalized" && student.rollNo == "Pending") {
                (104..999).random().toString()
            } else student.rollNo

            val updated = student.copy(
                enrollmentStage = nextStage,
                rollNo = newRoll,
                section = if (nextStage == "Finalized") "A" else student.section
            )
            repository.updateStudent(updated)
            _smsBroadcastFeedback.emit("Admission pipeline updated for ${student.name} to $nextStage")
        }
    }

    // --- Fee & Payment Gateway Simulators (Razorpay UPI Integration ready) ---
    fun makeRazorpayPayment(payment: PaymentInstallment, method: String) {
        viewModelScope.launch {
            val receiptNo = "GST-REC-${(1000..9999).random()}"
            val finalGst = payment.amountPaid * 0.18 // 18% GST calculation under standard CGST/SGST rules
            val settled = payment.copy(
                status = "Paid",
                paymentDate = getCurrentDateString(),
                method = method,
                receiptNumber = receiptNo,
                gstAmount = finalGst
            )
            repository.insertPayment(settled)
            _paymentReceiptFeedback.emit(receiptNo)
            _smsBroadcastFeedback.emit("Fee Payment Successful! Gst-Invoice $receiptNo sent to WhatsApp.")
        }
    }

    fun simulateLateFeeCalculation(baseTuition: Double): Double {
        // Indian context standard 2% penalty for monthly delay
        return baseTuition * 0.02
    }

    // --- Daily Attendance & Biometric triggers (with SMS instant warning) ---
    fun submitDailyAttendance(studentId: Int, date: String, status: String) {
        viewModelScope.launch {
            repository.deleteAttendanceForDay(studentId, date)
            val smsWarningTriggered = (status == "Absent")
            val record = Attendance(
                studentId = studentId,
                dateString = date,
                status = status,
                smsSent = smsWarningTriggered
            )
            repository.insertAttendance(record)

            if (smsWarningTriggered) {
                // Find student to construct targeted SMS message
                val s = students.value.find { it.id == studentId }
                s?.let {
                    _smsBroadcastFeedback.emit(
                        "SMS Gateway alert dispatched to ${it.parentPhone}: 'Dear Parent, your ward ${it.name} is ABSENT today (${date}) from school context. Kindly revert.'"
                    )
                }
            }
        }
    }

    // --- Academic Grading (CBSE strictly 80 Term theory & 20 Internals model) ---
    fun saveStudentMarks(studentId: Int, subject: String, term: String, theory: Int, internal: Int) {
        viewModelScope.launch {
            val total = theory + internal
            val gradeStr = when {
                total >= 91 -> "A1"
                total >= 81 -> "A2"
                total >= 71 -> "B1"
                total >= 61 -> "B2"
                total >= 51 -> "C1"
                total >= 41 -> "C2"
                total >= 33 -> "D"
                else -> "E (Needs Improvement)"
            }
            val record = StudentGrade(
                studentId = studentId,
                subject = subject,
                examTerm = term,
                theoryMarks = theory,
                internalMarks = internal,
                gradeChar = gradeStr
            )
            repository.insertGrade(record)
        }
    }

    // --- Transport Routing Allocation ---
    fun updateStudentTransportRoute(student: Student, routeId: Int) {
        viewModelScope.launch {
            val updated = student.copy(transportRouteId = routeId)
            repository.updateStudent(updated)
            _smsBroadcastFeedback.emit("Bus allocated for ${student.name}.")
        }
    }

    fun addNewRoute(
        name: String,
        driverName: String,
        license: String,
        expiry: String,
        vehicleNo: String,
        slab: Double
    ) {
        viewModelScope.launch {
            val route = TransportRoute(
                name = name,
                driverName = driverName,
                driverLicense = license,
                licenseExpiry = expiry,
                vehicleNo = vehicleNo,
                feeSlab = slab
            )
            repository.insertRoute(route)
        }
    }

    // --- HR & Payroll Deductions simulator ---
    fun simulateSalaryCalculation(basic: Double, leavesTaken: Int): Map<String, Double> {
        val da = basic * 0.12 // Dearness allowance (12%)
        val hra = basic * 0.20 // House rent allowance (20%)
        val pf = 1800.0 // Standard EPF deduction
        val pt = 200.0 // Professional Tax (State dependent in India)
        
        // Deduction for leaves exceeding buffer (e.g. 1 free casual leave per month average)
        val leaveDeduction = if (leavesTaken > 3) {
            (basic / 30) * (leavesTaken - 3)
        } else {
            0.0
        }
        
        val netSalary = (basic + da + hra) - (pf + pt + leaveDeduction)
        return mapOf(
            "da" to da,
            "hra" to hra,
            "pf" to pf,
            "pt" to pt,
            "leaves" to leaveDeduction,
            "net" to netSalary
        )
    }

    fun submitTeacherLeave(teacher: Teacher) {
        viewModelScope.launch {
            val updated = teacher.copy(leavesTaken = teacher.leavesTaken + 1)
            repository.updateTeacher(updated)
            _smsBroadcastFeedback.emit("Leave request processed for ${teacher.name}. Total: ${updated.leavesTaken}")
        }
    }

    // --- Notice Publish & Messaging alert ---
    fun publishDigitalNotice(title: String, body: String, category: String) {
        viewModelScope.launch {
            val n = Notice(
                title = title,
                description = body,
                category = category,
                dateString = getCurrentDateString()
            )
            repository.insertNotice(n)
            _smsBroadcastFeedback.emit("Notice published and pushed to all Student and Teacher dashboard indices!")
        }
    }

    fun uploadClassHomework(className: String, subject: String, title: String, instructions: String, due: String) {
        viewModelScope.launch {
            val hw = Homework(
                className = className,
                subject = subject,
                title = title,
                instructions = instructions,
                dueDate = due
            )
            repository.insertHomework(hw)
            _smsBroadcastFeedback.emit("Homework published for $className! Parents notified.")
        }
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
