package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.InjectionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface InjectionLogDao {
    @Query("SELECT * FROM injection_logs WHERE patientId = :patientId ORDER BY administeredAt DESC")
    fun getLogsForPatient(patientId: Long): Flow<List<InjectionLog>>

    @Query("SELECT * FROM injection_logs WHERE scheduleId = :scheduleId ORDER BY administeredAt DESC")
    fun getLogsForSchedule(scheduleId: Long): Flow<List<InjectionLog>>

    @Query("SELECT * FROM injection_logs ORDER BY administeredAt DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<InjectionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InjectionLog): Long

    @Delete
    suspend fun deleteLog(log: InjectionLog)

    @Query("DELETE FROM injection_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)
}
