package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PatientWithMedications(
    @Embedded val patient: Patient,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    )
    val medications: List<MedicationSchedule> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    )
    val logs: List<InjectionLog> = emptyList()
)
