package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val rollNo: String,
    val gradeClass: String, // e.g. "Class 10", "Class 12"
    val section: String,    // e.g. "A", "B"
    val parentName: String,
    val parentPhone: String,
    val parentEmail: String,
    val medicalHistory: String = "None",
    val rteQuota: Boolean = false, // Right to Education indicator
    val enrollmentStage: String = "Finalized", // Lead track: "Enquiry" -> "Assessment" -> "Interview" -> "Finalized"
    val aadharSubmitted: Boolean = true,
    val birthCertSubmitted: Boolean = true,
    val previousMarksSubmitted: Boolean = true,
    val transportRouteId: Int = 0 // 0 means status default / walking
) : Serializable

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val subject: String,
    val phone: String,
    val email: String,
    val basicSalary: Double = 35000.0,
    val leavesTaken: Int = 0,
    val totalAllowedLeaves: Int = 12,
    val pfDeducted: Double = 1800.0,
    val biometricRegistered: Boolean = true
) : Serializable

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val dateString: String, // YYYY-MM-DD
    val status: String,      // "Present", "Absent", "Late"
    val smsSent: Boolean = false
)

@Entity(tableName = "payments")
data class PaymentInstallment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val amountPaid: Double,
    val paymentDate: String, // YYYY-MM-DD
    val method: String,        // "UPI (Razorpay)", "Net Banking", "Card", "Cash"
    val installmentNumber: Int, // 1 (Q1), 2 (Q2), 3 (Q3), 4 (Q4)
    val status: String,        // "Paid", "Pending", "Defaulter"
    val receiptNumber: String,
    val penaltyAmount: Double = 0.0,
    val gstAmount: Double = 0.0
)

@Entity(tableName = "transport_routes")
data class TransportRoute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Route name e.g., "Route A - Dwarka Sector 10"
    val driverName: String,
    val driverLicense: String,
    val licenseExpiry: String, // YYYY-MM-DD
    val vehicleNo: String,     // e.g., "DL-2CA-9981"
    val feeSlab: Double        // distance based cost in INR
) : Serializable

@Entity(tableName = "grades")
data class StudentGrade(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val subject: String,
    val examTerm: String, // "Term 1", "Term 2", "Internal Assessment"
    val theoryMarks: Int, // out of 80
    val internalMarks: Int, // out of 20
    val gradeChar: String // "A1", "A2", "B1", "B2", "C", "D" etc.
)

@Entity(tableName = "homework")
data class Homework(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val className: String,
    val subject: String,
    val title: String,
    val instructions: String,
    val dueDate: String
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // "Urgent", "General", "Holidays", "Fees"
    val dateString: String // YYYY-MM-DD
)
