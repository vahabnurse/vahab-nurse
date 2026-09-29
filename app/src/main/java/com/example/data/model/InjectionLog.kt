package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "injection_logs",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicationSchedule::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["patientId"]),
        Index(value = ["scheduleId"])
    ]
)
data class InjectionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long,
    val scheduleId: Long,
    val medicineName: String,
    val dosage: String,
    val route: String,
    val administeredAt: Long = System.currentTimeMillis(),
    val administeredBy: String = "",     // Name of nurse/clinician
    val injectionSite: String = "",      // e.g. "بازوی راست", "عضله گلوتئال راست", "ورید دست چپ"
    val status: String = "انجام شد",      // "انجام شد", "به تعویق افتاد", "لغو شد"
    val notes: String = ""               // Patient response, side-effects, vitals
)
