package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Patient
import com.example.data.model.PatientWithMedications
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY createdAt DESC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientById(id: Long): Flow<Patient?>

    @Transaction
    @Query("SELECT * FROM patients ORDER BY createdAt DESC")
    fun getAllPatientsWithMedications(): Flow<List<PatientWithMedications>>

    @Transaction
    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientWithMedicationsById(id: Long): Flow<PatientWithMedications?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatientById(id: Long)
}
