package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicationSchedule
import com.example.data.model.Patient
import com.example.ui.components.AddEditMedicationDialog
import com.example.ui.components.AddEditPatientDialog
import com.example.ui.components.AdministerInjectionDialog
import com.example.ui.components.PatientCard
import com.example.ui.components.UpcomingInjectionsView
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDueNow
import com.example.ui.theme.StatusOverdue
import com.example.ui.viewmodel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    viewModel: PatientViewModel,
    onNavigateToPatientDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredPatients by viewModel.filteredPatients.collectAsStateWithLifecycle()
    val upcomingInjections by viewModel.upcomingInjections.collectAsStateWithLifecycle()
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    var showAddPatientDialog by remember { mutableStateOf(false) }
    var medicationToAdminister by remember { mutableStateOf<Pair<MedicationSchedule, String>?>(null) }
    var patientForNewMedication by remember { mutableStateOf<Pair<Long, String>?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مدیریت تزریقات و رژیم دارویی",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ثبت مشخصات بیمار، داروها و فواصل تزریق",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPatientDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("تعریف بیمار جدید", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_patient_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Dashboard Summary Metrics
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "کل بیماران",
                            count = summary.totalPatients.toString(),
                            icon = Icons.Default.People,
                            iconColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "تزریق فوری / موعد",
                            count = summary.urgentDueCount.toString(),
                            icon = Icons.Default.Warning,
                            iconColor = if (summary.urgentDueCount > 0) StatusOverdue else StatusDueNow,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "داروهای فعال",
                            count = summary.activeMedicationsCount.toString(),
                            icon = Icons.Default.Medication,
                            iconColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "تزریق‌های امروز",
                            count = summary.completedTodayCount.toString(),
                            icon = Icons.Default.CheckCircle,
                            iconColor = StatusCompleted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("جستجوی نام بیمار، شماره پرونده، تخت یا دارو...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "جستجو")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "پاک کردن")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_patients_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = selectedFilter == PatientViewModel.FilterType.ALL,
                            onClick = { viewModel.setFilter(PatientViewModel.FilterType.ALL) },
                            label = { Text("همه بیماران (${summary.totalPatients})") },
                            modifier = Modifier.testTag("filter_all")
                        )
                        FilterChip(
                            selected = selectedFilter == PatientViewModel.FilterType.URGENT_DUE,
                            onClick = { viewModel.setFilter(PatientViewModel.FilterType.URGENT_DUE) },
                            label = { Text("تزریقات فوری (${summary.urgentDueCount})") },
                            leadingIcon = if (summary.urgentDueCount > 0) {
                                { Icon(Icons.Default.Warning, contentDescription = null, tint = StatusOverdue, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("filter_urgent")
                        )
                        FilterChip(
                            selected = selectedFilter == PatientViewModel.FilterType.ACTIVE_MEDS,
                            onClick = { viewModel.setFilter(PatientViewModel.FilterType.ACTIVE_MEDS) },
                            label = { Text("دارای رژیم فعال") },
                            modifier = Modifier.testTag("filter_active_meds")
                        )
                    }
                }
            }

            // Live Queue of Upcoming / Overdue Injections
            if (upcomingInjections.isNotEmpty() && selectedFilter != PatientViewModel.FilterType.URGENT_DUE) {
                item {
                    UpcomingInjectionsView(
                        items = upcomingInjections.take(8),
                        onItemClick = { item ->
                            onNavigateToPatientDetail(item.patient.id)
                        },
                        onAdministerClick = { item ->
                            medicationToAdminister = item.schedule to item.patient.fullName
                        },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }

            // Patients Section Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "لیست بیماران و رژیم‌های درمانی",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${filteredPatients.size} بیمار",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Patient Cards List
            if (filteredPatients.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "بیماری با این مشخصات یافت نشد." else "هنوز بیماری ثبت نشده است.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredPatients, key = { it.patient.id }) { patientWithMeds ->
                    PatientCard(
                        patientWithMedications = patientWithMeds,
                        onClick = { onNavigateToPatientDetail(patientWithMeds.patient.id) },
                        onAdministerClick = { med ->
                            medicationToAdminister = med to patientWithMeds.patient.fullName
                        },
                        onAddMedicationClick = {
                            patientForNewMedication = patientWithMeds.patient.id to patientWithMeds.patient.fullName
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddPatientDialog) {
        AddEditPatientDialog(
            patient = null,
            onDismiss = { showAddPatientDialog = false },
            onSave = { newPatient ->
                viewModel.savePatient(newPatient) { createdId ->
                    showAddPatientDialog = false
                    onNavigateToPatientDetail(createdId)
                }
            }
        )
    }

    medicationToAdminister?.let { (schedule, patientName) ->
        AdministerInjectionDialog(
            schedule = schedule,
            patientName = patientName,
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

    patientForNewMedication?.let { (patientId, patientName) ->
        AddEditMedicationDialog(
            patientId = patientId,
            patientName = patientName,
            schedule = null,
            onDismiss = { patientForNewMedication = null },
            onSave = { newMed ->
                viewModel.saveMedicationSchedule(newMed)
                patientForNewMedication = null
            }
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    count: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = count,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
