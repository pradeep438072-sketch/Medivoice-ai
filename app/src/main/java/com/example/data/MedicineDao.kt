package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY reminderTime ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Long): Medicine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)

    @Query("SELECT COUNT(*) FROM medicines")
    suspend fun getMedicineCount(): Int

    // Dose Records for Tracking & History
    @Query("SELECT * FROM dose_records WHERE dateString = :date ORDER BY scheduledTime ASC")
    fun getDoseRecordsForDate(date: String): Flow<List<DoseRecord>>

    @Query("SELECT * FROM dose_records ORDER BY actionTimestamp DESC, id DESC")
    fun getAllDoseHistory(): Flow<List<DoseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoseRecord(record: DoseRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoseRecords(records: List<DoseRecord>)

    @Update
    suspend fun updateDoseRecord(record: DoseRecord)

    @Query("UPDATE dose_records SET status = :status, actionTimestamp = :timestamp, notes = :notes WHERE id = :id")
    suspend fun updateDoseStatus(id: Long, status: String, timestamp: Long, notes: String)

    @Query("DELETE FROM dose_records WHERE id = :id")
    suspend fun deleteDoseRecord(id: Long)
}
