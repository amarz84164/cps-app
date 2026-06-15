package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.SchoolViewModel
import com.example.UserRole
import com.example.data.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Color tokens matching Sleek Interface theme
val AcademicNavy = Color(0xFF6750A4)
val AcademicGold = Color(0xFF21005D)
val SoftBackground = Color(0xFFFAF9FD)
val CardBackground = Color(0xFFFFFFFF)
val MutedText = Color(0xFF49454F)
val LightBlueAccent = Color(0xFFEADDFF)
val ActiveGreen = Color(0xFF2E7D32)
val AlertRed = Color(0xFFB3261E)
val BlueAccentLight = Color(0xFFD0E4FF)
val BlueAccentDark = Color(0xFF001D35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDashboard(viewModel: SchoolViewModel) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect feedback broadcasts
    LaunchedEffect(key1 = true) {
        viewModel.smsBroadcastFeedback.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = when (currentRole) {
                                UserRole.SUPER_ADMIN -> "SUPER ADMIN"
                                UserRole.ADMIN -> "SCHOOL ADMIN"
                                UserRole.TEACHER -> "ACADEMIC STAFF"
                                UserRole.PARENT -> "PARENT PORTAL"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AcademicNavy,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "St. Xavier's Academy",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0xFF1C1B1F)
                        )
                    }
                },
                actions = {
                    RoleSelectorMenu(
                        currentRole = currentRole,
                        onRoleSelected = { viewModel.setRole(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val roleInitials = when (currentRole) {
                        UserRole.SUPER_ADMIN -> "VP"
                        UserRole.ADMIN -> "AD"
                        UserRole.TEACHER -> "TC"
                        UserRole.PARENT -> "PA"
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .background(Color(0xFFEADDFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = roleInitials,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1C1B1F),
                    actionIconContentColor = AcademicNavy
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SoftBackground)
        ) {
            // Alert Ribbon showing active role
            ActiveRoleHeadline(currentRole = currentRole)

            Box(modifier = Modifier.weight(1f)) {
                when (currentRole) {
                    UserRole.SUPER_ADMIN -> SuperAdminLayout(viewModel)
                    UserRole.ADMIN -> AdminLayout(viewModel)
                    UserRole.TEACHER -> TeacherLayout(viewModel)
                    UserRole.PARENT -> ParentLayout(viewModel)
                }
            }
        }
    }
}

@Composable
fun ActiveRoleHeadline(currentRole: UserRole) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlueAccent),
        border = androidx.compose.foundation.BorderStroke(1.dp, AcademicNavy.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AcademicNavy, CircleShape)
                )
                Text(
                    text = "Dashboard Role: ${currentRole.displayName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF21005D)
                )
            }
            Text(
                text = "Academic Yr 2026-27",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AcademicNavy
            )
        }
    }
}

@Composable
fun RoleSelectorMenu(currentRole: UserRole, onRoleSelected: (UserRole) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = AcademicGold,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Switch Roles",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Switch Role", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            UserRole.values().forEach { role ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = role.displayName,
                            fontWeight = if (currentRole == role) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentRole == role) AcademicNavy else Color.Black
                        )
                    },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    },
                    leadingIcon = {
                        val icon = when (role) {
                            UserRole.SUPER_ADMIN -> Icons.Filled.SupportAgent
                            UserRole.ADMIN -> Icons.Filled.Desk
                            UserRole.TEACHER -> Icons.Filled.SupervisorAccount
                            UserRole.PARENT -> Icons.Filled.FamilyRestroom
                        }
                        Icon(imageVector = icon, contentDescription = null, tint = AcademicGold)
                    }
                )
            }
        }
    }
}

// ==========================================
// SUPER_ADMIN MODULES
// ==========================================
@Composable
fun SuperAdminLayout(viewModel: SchoolViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val teachers by viewModel.teachers.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Financials", "Admissions (SIS)", "Staff & Leave", "Notice Hub")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = AcademicNavy
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> FinancialAnalyzerView(students, teachers, payments, viewModel)
                1 -> SuperAdminAdmissionsSISView(students, viewModel)
                2 -> SuperAdminStaffPayrollView(teachers, viewModel)
                3 -> AdminNoticeBroadcasterView(notices, viewModel)
            }
        }
    }
}

@Composable
fun FinancialAnalyzerView(
    students: List<Student>,
    teachers: List<Teacher>,
    payments: List<PaymentInstallment>,
    viewModel: SchoolViewModel
) {
    val totalFeesPaid = payments.filter { it.status == "Paid" }.sumOf { it.amountPaid }
    val totalPending = payments.filter { it.status == "Pending" || it.status == "Defaulter" }.sumOf { it.amountPaid }
    
    // Calculated outlays: Teacher salaries
    var totalPayrollExpense = 0.0
    teachers.forEach { teacher ->
        val calculations = viewModel.simulateSalaryCalculation(teacher.basicSalary, teacher.leavesTaken)
        totalPayrollExpense += (calculations["net"] ?: 0.0)
    }

    val netBalance = totalFeesPaid - totalPayrollExpense

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Real-Time School Outlays & Collections (INR)",
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Custom Canvas-based pie graph representation
                        Box(
                            modifier = Modifier.size(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val feeRatio = if (totalFeesPaid + totalPayrollExpense == 0.0) 0.5f 
                                           else (totalFeesPaid / (totalFeesPaid + totalPayrollExpense)).toFloat()

                            Canvas(modifier = Modifier.size(90.dp)) {
                                drawArc(
                                    color = Color(0xFFE2E8F0),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = AcademicNavy,
                                    startAngle = -90f,
                                    sweepAngle = feeRatio * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format("%.0f%%", feeRatio * 100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = AcademicNavy
                                )
                                Text(text = "Collection", fontSize = 9.sp, color = MutedText)
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LegendRow(color = AcademicNavy, label = "Fee Collections: ₹${String.format("%,.0f", totalFeesPaid)}")
                            LegendRow(color = AlertRed, label = "Payroll Expenses: ₹${String.format("%,.0f", totalPayrollExpense)}")
                            Divider()
                            LegendRow(color = ActiveGreen, label = "Net Surplus: ₹${String.format("%,.0f", netBalance)}")
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Admitted",
                    value = students.filter { it.enrollmentStage == "Finalized" }.size.toString(),
                    icon = Icons.Filled.People,
                    color = AcademicNavy,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "RTE Recruits",
                    value = students.filter { it.rteQuota }.size.toString(),
                    icon = Icons.Filled.CheckCircle,
                    color = AcademicGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Collection Breakdown",
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FinanceEntryRow(label = "Total Direct Student Fees Paid", amount = totalFeesPaid, isPositive = true)
                    FinanceEntryRow(label = "Accrued Late Fee Demands", amount = payments.filter { it.status == "Defaulter" }.sumOf { it.penaltyAmount }, isPositive = true)
                    FinanceEntryRow(label = "Unpaid Dues Outstanding", amount = totalPending, isPositive = false)
                }
            }
        }
    }
}

@Composable
fun LegendRow(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}

@Composable
fun FinanceEntryRow(label: String, amount: Double, isPositive: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MutedText)
        Text(
            text = "${if (isPositive) "+" else "-"} ₹${String.format("%,.0f", amount)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) ActiveGreen else AlertRed
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SuperAdminAdmissionsSISView(students: List<Student>, viewModel: SchoolViewModel) {
    var filterStage by remember { mutableStateOf("All") }
    val stages = listOf("All", "Enquiry", "Assessment", "Interview", "Finalized")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            stages.forEach { stage ->
                val isSelected = filterStage == stage
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) AcademicNavy else Color.White,
                            RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, AcademicNavy.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .clickable { filterStage = stage }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stage,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else AcademicNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val filtered = students.filter { filterStage == "All" || it.enrollmentStage == filterStage }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "No recruits recorded under status '$filterStage'.",
                    color = MutedText,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { student ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AcademicNavy
                                    )
                                    Text(
                                        text = "Parent: ${student.parentName} (${student.parentPhone})",
                                        fontSize = 12.sp,
                                        color = MutedText
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (student.enrollmentStage) {
                                                "Finalized" -> ActiveGreen.copy(alpha = 0.15f)
                                                "Interview" -> AcademicGold.copy(alpha = 0.15f)
                                                else -> AcademicNavy.copy(alpha = 0.1f)
                                            },
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = student.enrollmentStage,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (student.enrollmentStage) {
                                            "Finalized" -> ActiveGreen
                                            "Interview" -> AcademicGold
                                            else -> AcademicNavy
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    DocumentationBadge(submitted = student.aadharSubmitted, label = "Aadhar")
                                    DocumentationBadge(submitted = student.birthCertSubmitted, label = "BirthCert")
                                    DocumentationBadge(submitted = student.previousMarksSubmitted, label = "PriorMarks")
                                }
                                if (student.rteQuota) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RTE Quota",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                }
                            }

                            if (student.enrollmentStage != "Finalized") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val nextStage = when (student.enrollmentStage) {
                                        "Enquiry" -> "Assessment"
                                        "Assessment" -> "Interview"
                                        "Interview" -> "Finalized"
                                        else -> ""
                                    }
                                    if (nextStage.isNotEmpty()) {
                                        Button(
                                            onClick = { viewModel.updateEnrollmentStage(student, nextStage) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AcademicGold),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "Promote to $nextStage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentationBadge(submitted: Boolean, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = if (submitted) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
            contentDescription = null,
            tint = if (submitted) ActiveGreen else AlertRed,
            modifier = Modifier.size(12.dp)
        )
        Text(text = label, fontSize = 10.sp, color = MutedText)
    }
}

@Composable
fun SuperAdminStaffPayrollView(teachers: List<Teacher>, viewModel: SchoolViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(teachers) { teacher ->
            val salDetails = viewModel.simulateSalaryCalculation(teacher.basicSalary, teacher.leavesTaken)
            val da = salDetails["da"] ?: 0.0
            val hra = salDetails["hra"] ?: 0.0
            val pf = salDetails["pf"] ?: 0.0
            val pt = salDetails["pt"] ?: 0.0
            val leaveDed = salDetails["leaves"] ?: 0.0
            val netSal = salDetails["net"] ?: 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = teacher.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AcademicNavy
                            )
                            Text(
                                text = "Lecturer: ${teacher.subject} • ${teacher.phone}",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (teacher.biometricRegistered) ActiveGreen.copy(alpha = 0.12f)
                                    else AlertRed.copy(alpha = 0.12f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (teacher.biometricRegistered) "Biometric Sync" else "No Biometric",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (teacher.biometricRegistered) ActiveGreen else AlertRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Salary Mechanics", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                            Text(text = "• Basic Salary: ₹${String.format("%.0f", teacher.basicSalary)}", fontSize = 11.sp, color = MutedText)
                            Text(text = "• DA (12%) / HRA (20%): +₹${String.format("%.0f", da)} / +₹${String.format("%.0f", hra)}", fontSize = 11.sp, color = MutedText)
                            Text(text = "• Provident Fund (EPF): -₹${String.format("%.0f", pf)}", fontSize = 11.sp, color = MutedText)
                            Text(text = "• Professional Tax / Leaves: -₹${String.format("%.0f", pt)} / -₹${String.format("%.0f", leaveDed)}", fontSize = 11.sp, color = MutedText)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Leaves Taken", fontSize = 10.sp, color = MutedText)
                            Text(text = "${teacher.leavesTaken} / ${teacher.totalAllowedLeaves} Yr", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AcademicGold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Net Released", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = AcademicNavy)
                            Text(text = "₹${String.format("%,.0f", netSal)}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ActiveGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.submitTeacherLeave(teacher) },
                            colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Add Casual Leave (+1)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Text(
                            text = "GST Safe Payroll slip Generated",
                            fontSize = 9.sp,
                            color = MutedText,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// ADMIN / FRONT DESK LAYOUT
// ==========================================
@Composable
fun AdminLayout(viewModel: SchoolViewModel) {
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Front Desk Intake", "Fee Collector", "Busing & Transport")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = AcademicNavy
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> AdminStudentIntakeFormView(routes, viewModel)
                1 -> AdminFeeCollectorView(students, payments, viewModel)
                2 -> AdminBusingTransportView(routes, students, viewModel)
            }
        }
    }
}

@Composable
fun AdminStudentIntakeFormView(routes: List<TransportRoute>, viewModel: SchoolViewModel) {
    var name by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("Class 10") }
    var selectedSection by remember { mutableStateOf("A") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var parentEmail by remember { mutableStateOf("") }
    var medical by remember { mutableStateOf("") }
    var rteQuota by remember { mutableStateOf(false) }
    var scaleStage by remember { mutableStateOf("Finalized") } // Enquiry stage choice

    var hasAadhar by remember { mutableStateOf(true) }
    var hasBirthCert by remember { mutableStateOf(true) }
    var hasPrevMarks by remember { mutableStateOf(true) }
    
    var selectedRouteId by remember { mutableStateOf(0) }

    val classes = listOf("Class 9", "Class 10", "Class 11", "Class 12")
    val stages = listOf("Finalized", "Interview", "Assessment", "Enquiry")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Admission & Registry Registration SIS",
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Student Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_name_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Class Target", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                classes.forEach { cls ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (selectedClass == cls) AcademicNavy else Color(0xFFF1F5F9),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedClass = cls }
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = cls.split(" ")[1], fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selectedClass == cls) Color.White else Color.Black)
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(0.8f)) {
                            Text(text = "RTE Recipient", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                    .clickable { rteQuota = !rteQuota },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (rteQuota) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    tint = if (rteQuota) ActiveGreen else MutedText,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "RTE 25%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Parent / Guardian Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("Contact Phone (+91)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = parentEmail,
                        onValueChange = { parentEmail = it },
                        label = { Text("Guardian Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = medical,
                        onValueChange = { medical = it },
                        label = { Text("Medical Concerns, e.g., Asthma/Allergies") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Mandatory Indian Document Submissions", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CheckboxRow(checked = hasAadhar, onCheckedChange = { hasAadhar = it }, label = "Aadhar Card")
                        CheckboxRow(checked = hasBirthCert, onCheckedChange = { hasBirthCert = it }, label = "Birth Cert")
                        CheckboxRow(checked = hasPrevMarks, onCheckedChange = { hasPrevMarks = it }, label = "Prior Grades")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Pipeline Lead Workflow Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        stages.forEach { st ->
                            val sSelected = scaleStage == st
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (sSelected) AcademicGold else Color(0xFFE2E8F0),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { scaleStage = st }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = st, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (sSelected) Color.White else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && parentName.isNotEmpty() && parentPhone.isNotEmpty()) {
                                viewModel.admitNewStudent(
                                    name = name,
                                    gradeClass = selectedClass,
                                    section = selectedSection,
                                    parentName = parentName,
                                    parentPhone = parentPhone,
                                    parentEmail = parentEmail,
                                    medicalHistory = if (medical.isEmpty()) "None" else medical,
                                    rteQuota = rteQuota,
                                    hasAadhar = hasAadhar,
                                    hasBirthCert = hasBirthCert,
                                    hasPrevMarks = hasPrevMarks,
                                    routeId = selectedRouteId,
                                    enrollmentStage = scaleStage
                                )
                                // reset
                                name = ""
                                parentName = ""
                                parentPhone = ""
                                parentEmail = ""
                                medical = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_admission"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Submit Admission Entry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckboxRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AcademicNavy)
        )
        Text(text = label, fontSize = 11.sp, color = Color.Black)
    }
}

@Composable
fun AdminFeeCollectorView(
    students: List<Student>,
    payments: List<PaymentInstallment>,
    viewModel: SchoolViewModel
) {
    val unpaidInstallments = payments.filter { it.status == "Pending" || it.status == "Defaulter" }

    if (unpaidInstallments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "All fees cleared of outstanding dues recursively!", color = MutedText, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Outstanding Fee Accounts & Late-Fee Calculator",
                    fontWeight = FontWeight.Bold,
                    color = AcademicNavy,
                    fontSize = 14.sp
                )
            }

            items(unpaidInstallments) { p ->
                val associatedStudent = students.find { it.id == p.studentId }
                associatedStudent?.let { s ->
                    val calculatedLateFee = if (p.status == "Defaulter") viewModel.simulateLateFeeCalculation(p.amountPaid) else 0.0
                    val totalCharge = p.amountPaid + calculatedLateFee

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = s.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = AcademicNavy
                                    )
                                    Text(
                                        text = "${s.gradeClass}-${s.section} • Parent Tel: ${s.parentPhone}",
                                        fontSize = 11.sp,
                                        color = MutedText
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(AlertRed.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (p.status == "Defaulter") "Defaulter (Late)" else "Pending",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Installment Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                                    Text(text = "• Quarter: #${p.installmentNumber}", fontSize = 11.sp, color = MutedText)
                                    Text(text = "• Base tuition fees: ₹${String.format("%.0f", p.amountPaid)}", fontSize = 11.sp, color = MutedText)
                                    if (calculatedLateFee > 0.0) {
                                        Text(
                                            text = "• Local late fee fee (2%): +₹${String.format("%.0f", calculatedLateFee)}",
                                            fontSize = 11.sp,
                                            color = AlertRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Total Payable", fontSize = 10.sp, color = MutedText)
                                    Text(
                                        text = "₹${String.format("%,.0f", totalCharge)}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AcademicNavy
                                    )
                                    Text(text = "Includes 18% GST estimate", fontSize = 8.sp, color = MutedText)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.makeRazorpayPayment(p, "UPI (Razorpay Gateway)") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AcademicGold),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Collect Razorpay UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.makeRazorpayPayment(p, "Cash (School Vault)") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "Favour Cash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBusingTransportView(
    routes: List<TransportRoute>,
    students: List<Student>,
    viewModel: SchoolViewModel
) {
    var rName by remember { mutableStateOf("") }
    var dName by remember { mutableStateOf("") }
    var license by remember { mutableStateOf("") }
    var vehicleNo by remember { mutableStateOf("") }
    var slabText by remember { mutableStateOf("1500") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Register New Vehicle Fleet Route",
                        fontWeight = FontWeight.Bold,
                        color = AcademicNavy,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = rName,
                        onValueChange = { rName = it },
                        label = { Text("Route (e.g. Route C - Noida Sec 12)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dName,
                            onValueChange = { dName = it },
                            label = { Text("Driver Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = vehicleNo,
                            onValueChange = { vehicleNo = it },
                            label = { Text("Vehicle Plate No") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = license,
                            onValueChange = { license = it },
                            label = { Text("Driver License DL...") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = slabText,
                            onValueChange = { slabText = it },
                            label = { Text("Fee Slab (INR)") },
                            modifier = Modifier.weight(0.8f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (rName.isNotEmpty() && dName.isNotEmpty() && vehicleNo.isNotEmpty()) {
                                viewModel.addNewRoute(
                                    name = rName,
                                    driverName = dName,
                                    license = license,
                                    expiry = "2029-06-15",
                                    vehicleNo = vehicleNo,
                                    slab = slabText.toDoubleOrNull() ?: 1500.0
                                )
                                rName = ""
                                dName = ""
                                license = ""
                                vehicleNo = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Add Transport Route / Bus", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(text = "Current Fleet Allocation", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 14.sp)
        }

        items(routes) { route ->
            val riders = students.filter { it.transportRouteId == route.id }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = route.name, fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 14.sp)
                            Text(text = "Driver: ${route.driverName} • License: ${route.driverLicense}", fontSize = 11.sp, color = MutedText)
                        }
                        Box(
                            modifier = Modifier
                                .background(LightBlueAccent, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = route.vehicleNo, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Allocated riders: ${riders.size} students", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MutedText)
                        Text(text = "Quarter Slab Charges: ₹${route.feeSlab}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AcademicGold)
                    }

                    if (riders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Rider List: " + riders.joinToString(", ") { it.name },
                            fontSize = 11.sp,
                            color = MutedText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TEACHER MODULES
// ==========================================
@Composable
fun TeacherLayout(viewModel: SchoolViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Classroom Roll Call", "Homework Board", "CBSE Marks Entry")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = AcademicNavy
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> TeacherAttendanceLayout(students, viewModel)
                1 -> TeacherHomeworkLayout(viewModel)
                2 -> TeacherGradesEntryLayout(students, viewModel)
            }
        }
    }
}

@Composable
fun TeacherAttendanceLayout(students: List<Student>, viewModel: SchoolViewModel) {
    val date by viewModel.attendanceDate.collectAsStateWithLifecycle()
    val attendanceList by viewModel.getAttendanceForDate(date).collectAsStateWithLifecycle(initialValue = emptyList())

    val finalizedStudents = students.filter { it.enrollmentStage == "Finalized" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Class 10th-A Roll Book", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 15.sp)
                    Text(text = "Date: $date", fontSize = 12.sp, color = MutedText)
                }

                Box(
                    modifier = Modifier
                        .background(AcademicGold.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "SMS Warn Ready • Absentees", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AcademicGold)
                }
            }
        }

        items(finalizedStudents) { student ->
            val record = attendanceList.find { it.studentId == student.id }
            val currentStatus = record?.status ?: "Pending"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "${student.rollNo}. ${student.name}", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 14.sp)
                        Text(text = "Guardian: ${student.parentName} (${student.parentPhone})", fontSize = 11.sp, color = MutedText)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AttendanceStatusButton(
                            label = "Present",
                            active = currentStatus == "Present",
                            color = ActiveGreen,
                            onClick = { viewModel.submitDailyAttendance(student.id, date, "Present") }
                        )
                        AttendanceStatusButton(
                            label = "Absent",
                            active = currentStatus == "Absent",
                            color = AlertRed,
                            onClick = { viewModel.submitDailyAttendance(student.id, date, "Absent") }
                        )
                        AttendanceStatusButton(
                            label = "Late",
                            active = currentStatus == "Late",
                            color = AcademicGold,
                            onClick = { viewModel.submitDailyAttendance(student.id, date, "Late") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceStatusButton(label: String, active: Boolean, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (active) color else color.copy(alpha = 0.08f),
                RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Color.White else color
        )
    }
}

@Composable
fun TeacherHomeworkLayout(viewModel: SchoolViewModel) {
    var subject by remember { mutableStateOf("Mathematics") }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("Class 10") }

    val homeworks by viewModel.homework.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Publish Direct NCERT Assignments", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject (e.g. Science)") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = selectedClass,
                            onValueChange = { selectedClass = it },
                            label = { Text("Classroom (e.g. Class 10)") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Exercise Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Homework Instructions / Tasks") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (subject.isNotEmpty() && title.isNotEmpty() && desc.isNotEmpty()) {
                                viewModel.uploadClassHomework(
                                    className = selectedClass,
                                    subject = subject,
                                    title = title,
                                    instructions = desc,
                                    due = "2026-06-20"
                                )
                                title = ""
                                desc = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Commit & Alert Parents (WhatsApp)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(text = "Recently Broadcasted Work", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 14.sp)
        }

        items(homeworks) { h ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${h.className} • ${h.subject}", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 13.sp)
                        Text(text = "Due: ${h.dueDate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AlertRed)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = h.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = h.instructions, fontSize = 12.sp, color = MutedText)
                }
            }
        }
    }
}

@Composable
fun TeacherGradesEntryLayout(students: List<Student>, viewModel: SchoolViewModel) {
    var selectedStudentId by remember { mutableStateOf(if (students.isNotEmpty()) students.first().id else 0) }
    var selectedSubject by remember { mutableStateOf("Mathematics") }
    var selectedTerm by remember { mutableStateOf("Term 1") }
    var theoryText by remember { mutableStateOf("65") }
    var internalText by remember { mutableStateOf("18") }

    val terms = listOf("Term 1", "Term 2")
    val subjects = listOf("Mathematics", "Science", "Social Studies", "English", "Hindi")

    val enrolledStudents = students.filter { it.enrollmentStage == "Finalized" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "CBSE Grading Portal (Theory 80 / Internal 20)", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (enrolledStudents.isNotEmpty()) {
                        Text(text = "Select Student Rider", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        val currentStud = enrolledStudents.find { it.id == selectedStudentId } ?: enrolledStudents.first()
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { dropdownExpanded = true }
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                .border(1.dp, AcademicNavy.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${currentStud.rollNo}. ${currentStud.name} (${currentStud.gradeClass})", fontSize = 13.sp, color = Color.Black)
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                enrolledStudents.forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(text = "${st.rollNo}. ${st.name}") },
                                        onClick = {
                                            selectedStudentId = st.id
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(text = "Subject Card", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                            var subExpanded by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { subExpanded = true }
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Text(text = selectedSubject, fontSize = 13.sp)
                                DropdownMenu(expanded = subExpanded, onDismissRequest = { subExpanded = false }) {
                                    subjects.forEach { sub ->
                                        DropdownMenuItem(text = { Text(text = sub) }, onClick = { selectedSubject = sub; subExpanded = false })
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(0.8f)) {
                            Text(text = "CBSE Term", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                            var termExpanded by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { termExpanded = true }
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Text(text = selectedTerm, fontSize = 13.sp)
                                DropdownMenu(expanded = termExpanded, onDismissRequest = { termExpanded = false }) {
                                    terms.forEach { tr ->
                                        DropdownMenuItem(text = { Text(text = tr) }, onClick = { selectedTerm = tr; termExpanded = false })
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = theoryText,
                            onValueChange = { theoryText = it },
                            label = { Text("Theory Score (Max 80)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = internalText,
                            onValueChange = { internalText = it },
                            label = { Text("Internal (Max 20)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            val theory = theoryText.toIntOrNull() ?: 0
                            val internal = internalText.toIntOrNull() ?: 0
                            if (theory in 0..80 && internal in 0..20) {
                                viewModel.saveStudentMarks(
                                    studentId = selectedStudentId,
                                    subject = selectedSubject,
                                    term = selectedTerm,
                                    theory = theory,
                                    internal = internal
                                )
                                theoryText = ""
                                internalText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Commit Marks & Auto Calculate Grade", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// PARENT / STUDENT PORTAL LAYOUT
// ==========================================
@Composable
fun ParentLayout(viewModel: SchoolViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val activeStudentId by viewModel.selectedStudentIdForParent.collectAsStateWithLifecycle()

    val enrolled = students.filter { it.enrollmentStage == "Finalized" }

    if (enrolled.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No enrolled ward profiles registered currently.", color = MutedText)
        }
        return
    }

    val currentWard = enrolled.find { it.id == activeStudentId } ?: enrolled.first()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Fee Ledger", "Report Card", "Study Desk", "Daily Roll", "Bus Router")

    Column(modifier = Modifier.fillMaxSize()) {
        // Ward Profiler Select Pill Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Select Ward Profile to Inspect:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    enrolled.forEach { st ->
                        val isSel = st.id == activeStudentId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSel) AcademicNavy else Color(0xFFF1F5F9),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.selectParentStudent(st.id) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = st.name.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else AcademicNavy
                            )
                        }
                    }
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = AcademicNavy
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> ParentFeeLedgerView(currentWard, viewModel)
                1 -> ParentReportCardView(currentWard, viewModel)
                2 -> ParentHomeworkView(currentWard, viewModel)
                3 -> ParentAttendanceView(currentWard, viewModel)
                4 -> ParentBusingView(currentWard, viewModel)
            }
        }
    }
}

@Composable
fun ParentFeeLedgerView(ward: Student, viewModel: SchoolViewModel) {
    val payments by viewModel.getPaymentsForStudent(ward.id).collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LightBlueAccent)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Total outstanding dues", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                        val outstanding = payments.filter { it.status != "Paid" }.sumOf { it.amountPaid }
                        Text(text = "₹${String.format("%,.0f", outstanding)}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AcademicNavy)
                    }
                    Box(
                        modifier = Modifier
                            .background(AcademicNavy, CircleShape)
                            .size(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Payment, contentDescription = null, tint = AcademicGold, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        items(payments) { p ->
            val lateFeeVal = if (p.status == "Defaulter") viewModel.simulateLateFeeCalculation(p.amountPaid) else 0.0
            val aggregated = p.amountPaid + lateFeeVal

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Quarter Installment #${p.installmentNumber}", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 13.sp)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (p.status == "Paid") ActiveGreen.copy(alpha = 0.12f) else AlertRed.copy(alpha = 0.12f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = p.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (p.status == "Paid") ActiveGreen else AlertRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Base Cost: ₹${String.format("%.0f", p.amountPaid)}", fontSize = 12.sp, color = MutedText)
                            if (lateFeeVal > 0.0) {
                                Text(text = "Late Penalty (2%): +₹${String.format("%.0f", lateFeeVal)}", fontSize = 12.sp, color = AlertRed, fontWeight = FontWeight.Bold)
                            }
                            if (p.status == "Paid") {
                                Text(text = "Receipt No: ${p.receiptNumber}", fontSize = 10.sp, color = MutedText)
                                Text(text = "Paid Via: ${p.method}", fontSize = 10.sp, color = MutedText)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Total Charged", fontSize = 10.sp, color = MutedText)
                            Text(text = "₹${String.format("%,.0f", aggregated)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                        }
                    }

                    if (p.status != "Paid") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.makeRazorpayPayment(p, "UPI (Razorpay PG)") },
                            colors = ButtonDefaults.buttonColors(containerColor = AcademicGold),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Instant Pay using Razorpay UPI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentReportCardView(ward: Student, viewModel: SchoolViewModel) {
    val gradesList by viewModel.getGradesForStudent(ward.id).collectAsStateWithLifecycle(initialValue = emptyList())

    if (gradesList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No academic evaluation records compiled for this term card yet.", color = MutedText, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AcademicNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Grade, contentDescription = null, tint = AcademicGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "CBSE Official Terminal Report Card", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ward: ${ward.name} • Roll: ${ward.rollNo} • Class: ${ward.gradeClass} A",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            items(gradesList) { gr ->
                val scoreTotal = gr.theoryMarks + gr.internalMarks
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = gr.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AcademicNavy)
                            Text(text = "Theory (80): ${gr.theoryMarks} | Internal (20): ${gr.internalMarks}", fontSize = 12.sp, color = MutedText)
                            Text(text = gr.examTerm, fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Total: $scoreTotal / 100", fontSize = 11.sp, color = MutedText)
                            Text(text = "Grade: ${gr.gradeChar}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AcademicGold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentHomeworkView(ward: Student, viewModel: SchoolViewModel) {
    val hList by viewModel.getHomeworkForClass(ward.gradeClass).collectAsStateWithLifecycle(initialValue = emptyList())

    if (hList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No pending homework assignments registered for ${ward.gradeClass}.", color = MutedText)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(hList) { h ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = h.subject, fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 13.sp)
                            Text(text = "Due: ${h.dueDate}", fontSize = 11.sp, color = AlertRed, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = h.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = h.instructions, fontSize = 12.sp, color = MutedText)
                    }
                }
            }
        }
    }
}

@Composable
fun ParentAttendanceView(ward: Student, viewModel: SchoolViewModel) {
    val attendances by viewModel.getAttendanceForStudent(ward.id).collectAsStateWithLifecycle(initialValue = emptyList())

    if (attendances.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No historical daily attendance feeds compiled.", color = MutedText)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(attendances) { att ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Date: ${att.dateString}", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 13.sp)
                            if (att.smsSent) {
                                Text(text = "• Absent Alert sent to WhatsApp", fontSize = 11.sp, color = AlertRed, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    when (att.status) {
                                        "Present" -> ActiveGreen.copy(alpha = 0.15f)
                                        "Absent" -> AlertRed.copy(alpha = 0.15f)
                                        else -> AcademicGold.copy(alpha = 0.15f)
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = att.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (att.status) {
                                    "Present" -> ActiveGreen
                                    "Absent" -> AlertRed
                                    else -> AcademicGold
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentBusingView(ward: Student, viewModel: SchoolViewModel) {
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val matchingRoute = routes.find { it.id == ward.transportRouteId }

    if (matchingRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Student walks / parent self-commutes detail.", color = MutedText, fontSize = 13.sp)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Assigned Bus Route Route Details", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 15.sp)
                        Icon(imageVector = Icons.Filled.DirectionsBus, contentDescription = null, tint = AcademicGold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "• Route Name: ${matchingRoute.name}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "• Driver Assigned: ${matchingRoute.driverName}", fontSize = 12.sp, color = MutedText)
                    Text(text = "• Bus Plate Number: ${matchingRoute.vehicleNo}", fontSize = 12.sp, color = MutedText)
                    Text(text = "• Driver License status: ${matchingRoute.driverLicense} (Active)", fontSize = 12.sp, color = MutedText)
                    Text(text = "• Seat Fee slab details: ₹${matchingRoute.feeSlab} quarterly", fontSize = 12.sp, color = MutedText)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightBlueAccent, RoundedCornerShape(4.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Vehicle is currently ON ROUTE. Morning Pick: 7:15 AM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    }
                }
            }
        }
    }
}

// ==========================================
// SHARED NOTICES
// ==========================================
@Composable
fun AdminNoticeBroadcasterView(notices: List<Notice>, viewModel: SchoolViewModel) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }

    val categories = listOf("General", "Urgent", "Holidays", "Fees")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Broadcasting on Digital Board", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Broadcaster Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Notices Description Context") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Category Priority Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademicNavy)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { cat ->
                            val checkSel = category == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (checkSel) AcademicGold else Color(0xFFE2E8F0),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { category = cat }
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = cat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (checkSel) Color.White else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && desc.isNotEmpty()) {
                                viewModel.publishDigitalNotice(title, desc, category)
                                title = ""
                                desc = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicNavy),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Broadcast Live Notice", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(text = "Digital Noticeboard Broadcast Logs", fontWeight = FontWeight.Bold, color = AcademicNavy, fontSize = 14.sp)
        }

        items(notices) { notice ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    when (notice.category) {
                                        "Urgent" -> AlertRed.copy(alpha = 0.12f)
                                        "Holidays" -> AcademicGold.copy(alpha = 0.12f)
                                        else -> AcademicNavy.copy(alpha = 0.1f)
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = notice.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (notice.category) {
                                    "Urgent" -> AlertRed
                                    "Holidays" -> AcademicGold
                                    else -> AcademicNavy
                                }
                            )
                        }
                        Text(text = notice.dateString, fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = notice.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = notice.description, fontSize = 12.sp, color = MutedText)
                }
            }
        }
    }
}
