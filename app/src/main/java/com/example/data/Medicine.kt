package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // e.g., Antibiotics, Blood Pressure, Diabetes, Pain Relief, Vitamins, Allergy, Cardio, Other
    val dosage: String, // e.g., 500 mg, 1 Tablet, 10 ml
    val frequency: String, // e.g., Once Daily, Twice Daily, Every 8 Hours, As Needed
    val reminderTime: String, // 12-hour AM/PM format, e.g., "08:30 AM", "02:00 PM", "08:00 PM"
    val instructions: String = "Take after meal",
    val colorHex: Long = 0xFF00796B,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dose_records")
data class DoseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,
    val medicineName: String,
    val dosage: String,
    val scheduledTime: String, // 12-hour AM/PM format
    val dateString: String, // YYYY-MM-DD
    val status: String, // "TAKEN", "PENDING", "SKIPPED"
    val actionTimestamp: Long = 0L,
    val notes: String = ""
)
