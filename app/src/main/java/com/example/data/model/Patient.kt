package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val nationalId: String = "",
    val phoneNumber: String = "",
    val age: String = "",
    val gender: String = "آقا", // "آقا", "خانم", "کودک"
    val roomOrBedNumber: String = "",
    val allergies: String = "",
    val diagnosis: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}
