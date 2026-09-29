package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicationSchedule
import com.example.data.model.Patient
import com.example.data.model.PatientWithMedications
import com.example.ui.components.AddEditMedicationDialog
import com.example.ui.components.AddEditPatientDialog
import com.example.ui.components.AdministerInjectionDialog
import com.example.ui.components.InjectionHistorySheet
import com.example.ui.components.InjectionLogItem
import com.example.ui.components.MedicationScheduleCard
import com.example.ui.viewmodel.PatientViewModel
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    patientId: Long,
    viewModel: PatientViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val patientWithMeds by viewModel.selectedPatientWithMedications.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showEditPatientDialog by remember { mutableStateOf(false) }
    var showDeletePatientDialog by remember { mutableStateOf(false) }

    var medicationToEdit by remember { mutableStateOf<MedicationSchedule?>(null) }
    var showAddMedicationDialog by remember { mutableStateOf(false) }
    var medicationToDelete by remember { mutableStateOf<MedicationSchedule?>(null) }
    var medicationToAdminister by remember { mutableStateOf<MedicationSchedule?>(null) }
    var scheduleForLogHistory by remember { mutableStateOf<MedicationSchedule?>(null) }

    if (patientWithMeds == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "اطلاعات بیمار یافت نشد.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBackClick) {
                    Text("بازگشت به لیست بیماران")
                }
            }
        }
        return
    }

    val patient = patientWithMeds!!.patient
    val medications = patientWithMeds!!.medications
    val logs = patientWithMeds!!.logs

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (patient.roomOrBedNumber.isNotBlank()) {
                            Text(
                                text = patient.roomOrBedNumber,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("patient_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEditPatientDialog = true },
                        modifier = Modifier.testTag("edit_patient_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "ویرایش بیمار",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showDeletePatientDialog = true },
                        modifier = Modifier.testTag("delete_patient_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف بیمار",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { showAddMedicationDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_medication_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تعریف داروی جدید", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Patient Header Card
            PatientHeaderInfoCard(
                patient = patient,
                activeMedsCount = medications.count { it.isActive },
                totalLogsCount = logs.size,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // Primary Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("رژیم دارویی و تزریقات (${medications.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_medications")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("تاریخچه لاگ (${logs.size})", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_history")
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("خلاصه پرونده", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Summarize, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("tab_summary")
                )
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Medication Schedules Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (medications.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Medication,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "هنوز هیچ دارویی برای این بیمار تعریف نشده است.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(onClick = { showAddMedicationDialog = true }) {
                                            Icon(Icons.Default.Add, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("تعریف اولین دارو و فاصله تزریق")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(medications, key = { it.id }) { schedule ->
                                MedicationScheduleCard(
                                    schedule = schedule,
                                    onAdminister = { medicationToAdminister = schedule },
                                    onEdit = { medicationToEdit = schedule },
                                    onDelete = { medicationToDelete = schedule },
                                    onToggleActive = {
                                        viewModel.toggleMedicationActive(schedule.id, schedule.isActive)
                                    },
                                    onViewLogs = { scheduleForLogHistory = schedule }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                    }
                }

                1 -> {
                    // Injection Logs History Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (logs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "هنوز هیچ تزریقی برای این بیمار ثبت نشده است.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        } else {
                            items(logs, key = { it.id }) { log ->
                                InjectionLogItem(
                                    log = log,
                                    onDelete = { viewModel.deleteLog(log) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    }
                }

                2 -> {
                    // Clinical Summary Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            ClinicalSummaryView(
                                patient = patient,
                                medications = medications,
                                logs = logs
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Patient Dialog
    if (showEditPatientDialog) {
        AddEditPatientDialog(
            patient = patient,
            onDismiss = { showEditPatientDialog = false },
            onSave = { updatedPatient ->
                viewModel.savePatient(updatedPatient)
                showEditPatientDialog = false
            }
        )
    }

    // Delete Patient Confirmation Dialog
    if (showDeletePatientDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePatientDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("حذف بیمار") },
            text = { Text("آیا از حذف کامل پرونده بیمار «${patient.fullName}» و تمام رژیم‌های دارویی و تاریخچه تزریقات ایشان اطمینان دارید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePatient(patient)
                        showDeletePatientDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_patient_button")
                ) {
                    Text("حذف کامل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePatientDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Add / Edit Medication Dialog
    if (showAddMedicationDialog || medicationToEdit != null) {
        AddEditMedicationDialog(
            patientId = patient.id,
            patientName = patient.fullName,
            schedule = medicationToEdit,
            onDismiss = {
                showAddMedicationDialog = false
                medicationToEdit = null
            },
            onSave = { schedule ->
                viewModel.saveMedicationSchedule(schedule)
                showAddMedicationDialog = false
                medicationToEdit = null
            }
        )
    }

    // Delete Medication Confirmation
    medicationToDelete?.let { schedule ->
        AlertDialog(
            onDismissRequest = { medicationToDelete = null },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("حذف دارو از رژیم") },
            text = { Text("آیا از حذف داروی «${schedule.medicineName}» اطمینان دارید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMedicationSchedule(schedule)
                        medicationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { medicationToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }

    // Administer Injection Dialog
    medicationToAdminister?.let { schedule ->
        AdministerInjectionDialog(
            schedule = schedule,
            patientName = patient.fullName,
            onDismiss = { medicationToAdminister = null },
            onConfirm = { by, site, status, notes ->
                viewModel.administerInjection(
                    schedule = schedule,
                    administeredBy = by,
                    injectionSite = site,
                    status = status,
                    notes = notes
                )
                medicationToAdminister = null
            }
        )
    }

    // Filtered logs for single schedule
    scheduleForLogHistory?.let { schedule ->
        val scheduleLogs = logs.filter { it.scheduleId == schedule.id }
        InjectionHistorySheet(
            title = "تاریخچه تزریقات: ${schedule.medicineName}",
            logs = scheduleLogs,
            onDismiss = { scheduleForLogHistory = null },
            onDeleteLog = { log -> viewModel.deleteLog(log) }
        )
    }
}

@Composable
fun PatientHeaderInfoCard(
    patient: Patient,
    activeMedsCount: Int,
    totalLogsCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (patient.gender == "خانم") Color(0xFFF48FB1)
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (patient.gender == "خانم") Color(0xFF880E4F) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.fullName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${patient.gender} • ${if (patient.age.isNotBlank()) "${patient.age} ساله" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (patient.roomOrBedNumber.isNotBlank()) {
                            Text(
                                text = "• ${patient.roomOrBedNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Allergy Alert if present
            if (patient.allergies.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "هشدار حساسیت",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "هشدار حساسیت دارویی: ${patient.allergies}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFB71C1C)
                        )
                    }
                }
            }

            if (patient.diagnosis.isNotBlank() || patient.nationalId.isNotBlank() || patient.phoneNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (patient.nationalId.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "کد ملی: ${patient.nationalId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (patient.phoneNumber.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = patient.phoneNumber,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (patient.diagnosis.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تشخیص: ${patient.diagnosis}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (patient.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ملاحظات: ${patient.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ClinicalSummaryView(
    patient: Patient,
    medications: List<MedicationSchedule>,
    logs: List<com.example.data.model.InjectionLog>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "برگه خلاصه پرونده و رژیم دارویی",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Patient Identity Info
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "مشخصات هویتی و بالینی:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(text = "• نام و نام خانوادگی: ${patient.fullName}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• سن و جنسیت: ${patient.age} سال (${patient.gender})", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• محل بستری / تخت: ${patient.roomOrBedNumber.ifBlank { "ثبت نشده" }}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• کد ملی / پرونده: ${patient.nationalId.ifBlank { "ثبت نشده" }}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "• سابقه حساسیت: ${patient.allergies.ifBlank { "عدم گزارش حساسیت دارویی" }}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Text(
                text = "داروهای تجویزی و فواصل تزریق:",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            if (medications.isEmpty()) {
                Text(text = "هیچ دارویی تعریف نشده است.", style = MaterialTheme.typography.bodySmall)
            } else {
                medications.forEachIndexed { index, med ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${index + 1}. ${med.medicineName} (${med.dosage})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "روش تزریق: ${med.route} | فاصله زمانی: ${DateUtils.formatIntervalText(med.intervalHours)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (med.instructions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "دستور مصرف: ${med.instructions}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "دوزهای انجام‌شده: ${med.completedDosesCount} ${if (med.totalDoses > 0) "از ${med.totalDoses}" else ""}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "آمار کل تزریقات انجام‌شده: ${logs.size} نوبت تزریق ثبت شده است.",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
