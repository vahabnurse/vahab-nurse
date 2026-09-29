package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MedicationSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationScheduleDao {
    @Query("SELECT * FROM medication_schedules WHERE patientId = :patientId ORDER BY isActive DESC, nextDueDateTime ASC")
    fun getSchedulesForPatient(patientId: Long): Flow<List<MedicationSchedule>>

    @Query("SELECT * FROM medication_schedules WHERE isActive = 1 ORDER BY nextDueDateTime ASC")
    fun getAllActiveSchedules(): Flow<List<MedicationSchedule>>

    @Query("SELECT * FROM medication_schedules WHERE id = :id")
    fun getScheduleById(id: Long): Flow<MedicationSchedule?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MedicationSchedule): Long

    @Update
    suspend fun updateSchedule(schedule: MedicationSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: MedicationSchedule)

    @Query("DELETE FROM medication_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)

    @Query("UPDATE medication_schedules SET completedDosesCount = completedDosesCount + 1, nextDueDateTime = :nextDue WHERE id = :id")
    suspend fun advanceScheduleAfterAdministration(id: Long, nextDue: Long)

    @Query("UPDATE medication_schedules SET isActive = :isActive WHERE id = :id")
    suspend fun setScheduleActiveStatus(id: Long, isActive: Boolean)
}
