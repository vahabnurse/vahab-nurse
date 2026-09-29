package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.InjectionLog
import com.example.data.model.MedicationSchedule
import com.example.data.model.Patient
import com.example.data.model.PatientWithMedications
import com.example.data.repository.PatientRepository
import com.example.util.DateUtils
import com.example.util.DueStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardSummary(
    val totalPatients: Int = 0,
    val activeMedicationsCount: Int = 0,
    val urgentDueCount: Int = 0,
    val completedTodayCount: Int = 0
)

data class UpcomingInjectionItem(
    val patient: Patient,
    val schedule: MedicationSchedule,
    val dueStatus: DueStatus,
    val relativeTimeText: String
)

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PatientRepository

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(FilterType.ALL)
    val selectedPatientId = MutableStateFlow<Long?>(null)

    enum class FilterType {
        ALL,
        URGENT_DUE,
        ACTIVE_MEDS
    }

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PatientRepository(
            db.patientDao(),
            db.medicationScheduleDao(),
            db.injectionLogDao()
        )

        // Seed initial friendly clinical data if empty
        viewModelScope.launch {
            val existing = repository.allPatients.first()
            if (existing.isEmpty()) {
                repository.insertSampleDataIfEmpty()
            }
        }
    }

    val patientsWithMedications: StateFlow<List<PatientWithMedications>> = repository.allPatientsWithMedications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredPatients: StateFlow<List<PatientWithMedications>> = combine(
        repository.allPatientsWithMedications,
        searchQuery,
        selectedFilter
    ) { list, query, filter ->
        var result = list

        if (query.isNotBlank()) {
            val cleanQuery = query.trim().lowercase()
            result = result.filter { item ->
                item.patient.fullName.lowercase().contains(cleanQuery) ||
                item.patient.nationalId.contains(cleanQuery) ||
                item.patient.roomOrBedNumber.lowercase().contains(cleanQuery) ||
                item.medications.any { it.medicineName.lowercase().contains(cleanQuery) }
            }
        }

        when (filter) {
            FilterType.ALL -> result
            FilterType.URGENT_DUE -> {
                result.filter { item ->
                    item.medications.any { med ->
                        med.isActive && (
                            DateUtils.getDueStatus(med.nextDueDateTime) == DueStatus.OVERDUE ||
                            DateUtils.getDueStatus(med.nextDueDateTime) == DueStatus.DUE_NOW ||
                            DateUtils.getDueStatus(med.nextDueDateTime) == DueStatus.UPCOMING_SOON
                        )
                    }
                }
            }
            FilterType.ACTIVE_MEDS -> {
                result.filter { item -> item.medications.any { it.isActive } }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val upcomingInjections: StateFlow<List<UpcomingInjectionItem>> = repository.allPatientsWithMedications
        .combine(searchQuery) { list, _ ->
            val items = mutableListOf<UpcomingInjectionItem>()
            for (item in list) {
                for (schedule in item.medications) {
                    if (schedule.isActive) {
                        val status = DateUtils.getDueStatus(schedule.nextDueDateTime)
                        val relative = DateUtils.formatRelativeTime(schedule.nextDueDateTime)
                        items.add(
                            UpcomingInjectionItem(
                                patient = item.patient,
                                schedule = schedule,
                                dueStatus = status,
                                relativeTimeText = relative
                            )
                        )
                    }
                }
            }
            items.sortedBy { it.schedule.nextDueDateTime }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dashboardSummary: StateFlow<DashboardSummary> = repository.allPatientsWithMedications
        .combine(repository.recentLogs) { patients, logs ->
            val totalPatients = patients.size
            var activeMedCount = 0
            var urgentDue = 0
            for (p in patients) {
                for (m in p.medications) {
                    if (m.isActive) {
                        activeMedCount++
                        val status = DateUtils.getDueStatus(m.nextDueDateTime)
                        if (status == DueStatus.OVERDUE || status == DueStatus.DUE_NOW || status == DueStatus.UPCOMING_SOON) {
                            urgentDue++
                        }
                    }
                }
            }
            // Count logs today
            val startOfToday = System.currentTimeMillis() - 24 * 3600_000L
            val logsToday = logs.count { it.administeredAt >= startOfToday && it.status == "انجام شد" }

            DashboardSummary(
                totalPatients = totalPatients,
                activeMedicationsCount = activeMedCount,
                urgentDueCount = urgentDue,
                completedTodayCount = logsToday
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardSummary()
        )

    val selectedPatientWithMedications: StateFlow<PatientWithMedications?> = combine(
        repository.allPatientsWithMedications,
        selectedPatientId
    ) { list, id ->
        if (id == null) null else list.find { it.patient.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun selectPatient(patientId: Long?) {
        selectedPatientId.value = patientId
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setFilter(filter: FilterType) {
        selectedFilter.value = filter
    }

    fun savePatient(patient: Patient, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            if (patient.id == 0L) {
                val id = repository.insertPatient(patient)
                onComplete?.invoke(id)
            } else {
                repository.updatePatient(patient)
                onComplete?.invoke(patient.id)
            }
        }
    }

    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient)
            if (selectedPatientId.value == patient.id) {
                selectedPatientId.value = null
            }
        }
    }

    fun saveMedicationSchedule(schedule: MedicationSchedule) {
        viewModelScope.launch {
            if (schedule.id == 0L) {
                repository.insertSchedule(schedule)
            } else {
                repository.updateSchedule(schedule)
            }
        }
    }

    fun deleteMedicationSchedule(schedule: MedicationSchedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    fun toggleMedicationActive(scheduleId: Long, currentActive: Boolean) {
        viewModelScope.launch {
            repository.setScheduleActiveStatus(scheduleId, !currentActive)
        }
    }

    fun administerInjection(
        schedule: MedicationSchedule,
        administeredBy: String,
        injectionSite: String,
        status: String = "انجام شد",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.recordInjection(
                schedule = schedule,
                administeredBy = administeredBy,
                injectionSite = injectionSite,
                status = status,
                notes = notes
            )
        }
    }

    fun deleteLog(log: InjectionLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}
