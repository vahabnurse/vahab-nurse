package com.example.data.repository

import com.example.data.local.InjectionLogDao
import com.example.data.local.MedicationScheduleDao
import com.example.data.local.PatientDao
import com.example.data.model.InjectionLog
import com.example.data.model.MedicationSchedule
import com.example.data.model.Patient
import com.example.data.model.PatientWithMedications
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow

class PatientRepository(
    private val patientDao: PatientDao,
    private val scheduleDao: MedicationScheduleDao,
    private val logDao: InjectionLogDao
) {
    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()
    val allPatientsWithMedications: Flow<List<PatientWithMedications>> = patientDao.getAllPatientsWithMedications()
    val allActiveSchedules: Flow<List<MedicationSchedule>> = scheduleDao.getAllActiveSchedules()
    val recentLogs: Flow<List<InjectionLog>> = logDao.getRecentLogs()

    fun getPatientWithMedicationsById(patientId: Long): Flow<PatientWithMedications?> {
        return patientDao.getPatientWithMedicationsById(patientId)
    }

    fun getSchedulesForPatient(patientId: Long): Flow<List<MedicationSchedule>> {
        return scheduleDao.getSchedulesForPatient(patientId)
    }

    fun getLogsForPatient(patientId: Long): Flow<List<InjectionLog>> {
        return logDao.getLogsForPatient(patientId)
    }

    suspend fun insertPatient(patient: Patient): Long {
        return patientDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        patientDao.deletePatient(patient)
    }

    suspend fun deletePatientById(id: Long) {
        patientDao.deletePatientById(id)
    }

    suspend fun insertSchedule(schedule: MedicationSchedule): Long {
        return scheduleDao.insertSchedule(schedule)
    }

    suspend fun updateSchedule(schedule: MedicationSchedule) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun deleteSchedule(schedule: MedicationSchedule) {
        scheduleDao.deleteSchedule(schedule)
    }

    suspend fun setScheduleActiveStatus(scheduleId: Long, isActive: Boolean) {
        scheduleDao.setScheduleActiveStatus(scheduleId, isActive)
    }

    suspend fun recordInjection(
        schedule: MedicationSchedule,
        administeredBy: String,
        injectionSite: String,
        status: String,
        notes: String
    ) {
        val now = System.currentTimeMillis()
        val log = InjectionLog(
            patientId = schedule.patientId,
            scheduleId = schedule.id,
            medicineName = schedule.medicineName,
            dosage = schedule.dosage,
            route = schedule.route,
            administeredAt = now,
            administeredBy = administeredBy,
            injectionSite = injectionSite,
            status = status,
            notes = notes
        )
        logDao.insertLog(log)

        if (status == "انجام شد") {
            val nextDue = now + (schedule.intervalHours.toLong() * 3600_000L)
            scheduleDao.advanceScheduleAfterAdministration(schedule.id, nextDue)
        }
    }

    suspend fun deleteLog(log: InjectionLog) {
        logDao.deleteLog(log)
    }

    suspend fun insertSampleDataIfEmpty() {
        val samplePatient1 = Patient(
            firstName = "علی",
            lastName = "رضایی",
            nationalId = "0012345678",
            phoneNumber = "09123456789",
            age = "48",
            gender = "آقا",
            roomOrBedNumber = "تخت ۲۰۴ - بخش جراحی",
            allergies = "بدون حساسیت دارویی شناخته‌شده",
            diagnosis = "عفونت ریوی پس از عمل",
            notes = "فشار خون تحت کنترل باشد"
        )
        val samplePatient2 = Patient(
            firstName = "مریم",
            lastName = "کریمی",
            nationalId = "0087654321",
            phoneNumber = "09351234567",
            age = "62",
            gender = "خانم",
            roomOrBedNumber = "تخت ۱۰۲ - بخش داخلی",
            allergies = "حساسیت به پنی‌سیلین",
            diagnosis = "دیابت نوع ۲ و کم‌خونی",
            notes = "قند خون قبل از هر تزریق انسولین چک شود"
        )
        val samplePatient3 = Patient(
            firstName = "حسین",
            lastName = "محمدی",
            nationalId = "0456789123",
            phoneNumber = "09187654321",
            age = "35",
            gender = "آقا",
            roomOrBedNumber = "تخت ۳۱۵ - بخش ارتوپدی",
            allergies = "عدم تحمل NSAIDs",
            diagnosis = "شکستگی استخوان فمور و آنتی‌بیوتیک تراپی",
            notes = "تزریق عضلانی با آرامش انجام شود"
        )

        val id1 = patientDao.insertPatient(samplePatient1)
        val id2 = patientDao.insertPatient(samplePatient2)
        val id3 = patientDao.insertPatient(samplePatient3)

        val now = System.currentTimeMillis()

        // Meds for Patient 1
        scheduleDao.insertSchedule(
            MedicationSchedule(
                patientId = id1,
                medicineName = "سفتریاکسون (Ceftriaxone)",
                dosage = "1g",
                route = "وریدی (IV)",
                intervalHours = 12,
                intervalDescription = "هر ۱۲ ساعت",
                startDateTime = now - 6 * 3600_000L,
                nextDueDateTime = now + 6 * 3600_000L,
                totalDoses = 14,
                completedDosesCount = 2,
                instructions = "آهسته در ۱۰ میلی‌لیتر آب مقطر رقیق شود",
                colorTagHex = 0xFF0288D1
            )
        )
        scheduleDao.insertSchedule(
            MedicationSchedule(
                patientId = id1,
                medicineName = "هپارین (Heparin)",
                dosage = "5000 Unit",
                route = "زیرجلدی (SC)",
                intervalHours = 8,
                intervalDescription = "هر ۸ ساعت",
                startDateTime = now - 8 * 3600_000L,
                nextDueDateTime = now - 15 * 60_000L, // Overdue by 15 mins for realism
                totalDoses = 21,
                completedDosesCount = 4,
                instructions = "دور ناف، زاویه ۴۵ یا ۹۰ درجه، بدون ماساژ",
                colorTagHex = 0xFFD32F2F
            )
        )

        // Meds for Patient 2
        scheduleDao.insertSchedule(
            MedicationSchedule(
                patientId = id2,
                medicineName = "انسولین لانتوس (Lantus)",
                dosage = "18 Unit",
                route = "زیرجلدی (SC)",
                intervalHours = 24,
                intervalDescription = "روزی یک بار (شب‌ها)",
                startDateTime = now,
                nextDueDateTime = now + 4 * 3600_000L,
                totalDoses = 0,
                completedDosesCount = 10,
                instructions = "ساعت ۲۱ هر شب، ثبت قند خون",
                colorTagHex = 0xFF7B1FA2
            )
        )
        scheduleDao.insertSchedule(
            MedicationSchedule(
                patientId = id2,
                medicineName = "ویتامین B12 (سیانوکوبالامین)",
                dosage = "1000 mcg",
                route = "عضلانی (IM)",
                intervalHours = 168, // Weekly
                intervalDescription = "هفتگی (هر ۷ روز)",
                startDateTime = now - 2 * 86400_000L,
                nextDueDateTime = now + 5 * 86400_000L,
                totalDoses = 4,
                completedDosesCount = 1,
                instructions = "عضله گلوتئال عمیق",
                colorTagHex = 0xFF00796B
            )
        )

        // Meds for Patient 3
        scheduleDao.insertSchedule(
            MedicationSchedule(
                patientId = id3,
                medicineName = "سفازولین (Cefazolin)",
                dosage = "1g",
                route = "وریدی (IV)",
                intervalHours = 6,
                intervalDescription = "هر ۶ ساعت",
                startDateTime = now - 6 * 3600_000L,
                nextDueDateTime = now + 30 * 60_000L, // In 30 mins
                totalDoses = 16,
                completedDosesCount = 3,
                instructions = "انفوزیون آرام طی ۱۵ دقیقه",
                colorTagHex = 0xFFF57C00
            )
        )
    }
}
