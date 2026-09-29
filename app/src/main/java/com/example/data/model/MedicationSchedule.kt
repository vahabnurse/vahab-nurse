package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_schedules",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["patientId"])]
)
data class MedicationSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val medicineName: String,
    val dosage: String,                 // e.g. "1g", "500mg", "10 Unit", "2 ampoule"
    val route: String,                  // e.g. "عضلانی (IM)", "وریدی (IV)", "زیرجلدی (SC)", "انفوزیون (IV Drip)", "داخل جلدی (ID)"
    val intervalHours: Int,             // e.g. 4, 6, 8, 12, 24, 48, 72, 168
    val intervalDescription: String = "", // e.g. "هر ۸ ساعت", "هر ۱۲ ساعت", "روزی یک بار"
    val startDateTime: Long = System.currentTimeMillis(),
    val nextDueDateTime: Long = System.currentTimeMillis(),
    val totalDoses: Int = 0,            // 0 means ongoing / until doctor discontinues
    val completedDosesCount: Int = 0,
    val instructions: String = "",      // Notes, special cautions e.g. "تست پنی‌سیلین انجام شود", "آهسته تزریق شود"
    val isActive: Boolean = true,
    val colorTagHex: Long = 0xFF0288D1,
    val createdAt: Long = System.currentTimeMillis()
)
