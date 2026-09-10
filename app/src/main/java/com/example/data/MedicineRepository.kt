package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedicineRepository(private val medicineDao: MedicineDao) {

    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()
    val allHistory: Flow<List<DoseRecord>> = medicineDao.getAllDoseHistory()

    fun getTodayDoses(dateString: String): Flow<List<DoseRecord>> {
        return medicineDao.getDoseRecordsForDate(dateString)
    }

    suspend fun insertMedicine(medicine: Medicine): Long {
        val id = medicineDao.insertMedicine(medicine)
        // Also schedule today's dose record
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())
        medicineDao.insertDoseRecord(
            DoseRecord(
                medicineId = id,
                medicineName = medicine.name,
                dosage = medicine.dosage,
                scheduledTime = medicine.reminderTime,
                dateString = todayStr,
                status = "PENDING",
                notes = "Scheduled reminder"
            )
        )
        return id
    }

    suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.updateMedicine(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine) {
        medicineDao.deleteMedicine(medicine)
    }

    suspend fun deleteMedicineById(id: Long) {
        medicineDao.deleteMedicineById(id)
    }

    suspend fun markDoseTaken(recordId: Long, notes: String = "Taken as scheduled") {
        medicineDao.updateDoseStatus(
            id = recordId,
            status = "TAKEN",
            timestamp = System.currentTimeMillis(),
            notes = notes
        )
    }

    suspend fun markDoseSkipped(recordId: Long, reason: String = "Skipped by user") {
        medicineDao.updateDoseStatus(
            id = recordId,
            status = "SKIPPED",
            timestamp = System.currentTimeMillis(),
            notes = reason
        )
    }

    suspend fun addDoseRecord(record: DoseRecord): Long {
        return medicineDao.insertDoseRecord(record)
    }
}
