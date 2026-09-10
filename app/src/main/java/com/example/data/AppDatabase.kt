package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(entities = [Medicine::class, DoseRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medivoice_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.medicineDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: MedicineDao) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = dateFormat.format(Date())
                val yesterdayStr = dateFormat.format(Date(System.currentTimeMillis() - 86400000L))

                val med1Id = dao.insertMedicine(
                    Medicine(
                        name = "Amoxicillin",
                        category = "Antibiotics",
                        dosage = "500 mg",
                        frequency = "Twice Daily",
                        reminderTime = "09:00 AM",
                        instructions = "Take after breakfast with full glass of water",
                        colorHex = 0xFF00897B
                    )
                )

                val med2Id = dao.insertMedicine(
                    Medicine(
                        name = "Lisinopril",
                        category = "Blood Pressure",
                        dosage = "10 mg",
                        frequency = "Once Daily",
                        reminderTime = "08:00 AM",
                        instructions = "Take in the morning with or without food",
                        colorHex = 0xFF0288D1
                    )
                )

                val med3Id = dao.insertMedicine(
                    Medicine(
                        name = "Metformin",
                        category = "Diabetes",
                        dosage = "500 mg",
                        frequency = "Twice Daily",
                        reminderTime = "01:30 PM",
                        instructions = "Take with lunch to reduce stomach upset",
                        colorHex = 0xFF7B1FA2
                    )
                )

                val med4Id = dao.insertMedicine(
                    Medicine(
                        name = "Paracetamol",
                        category = "Pain Relief",
                        dosage = "650 mg",
                        frequency = "As Needed",
                        reminderTime = "04:00 PM",
                        instructions = "Take for fever or body ache. Max 4 doses/day",
                        colorHex = 0xFFE65100
                    )
                )

                val med5Id = dao.insertMedicine(
                    Medicine(
                        name = "Vitamin D3",
                        category = "Vitamins",
                        dosage = "2000 IU",
                        frequency = "Once Daily",
                        reminderTime = "08:00 PM",
                        instructions = "Take after dinner for optimum absorption",
                        colorHex = 0xFF2E7D32
                    )
                )

                // Today's dose records
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med2Id,
                        medicineName = "Lisinopril",
                        dosage = "10 mg",
                        scheduledTime = "08:00 AM",
                        dateString = todayStr,
                        status = "TAKEN",
                        actionTimestamp = System.currentTimeMillis() - 3600000L * 3,
                        notes = "Taken on time after morning walk"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med1Id,
                        medicineName = "Amoxicillin",
                        dosage = "500 mg",
                        scheduledTime = "09:00 AM",
                        dateString = todayStr,
                        status = "TAKEN",
                        actionTimestamp = System.currentTimeMillis() - 3600000L * 2,
                        notes = "Taken after light breakfast"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med3Id,
                        medicineName = "Metformin",
                        dosage = "500 mg",
                        scheduledTime = "01:30 PM",
                        dateString = todayStr,
                        status = "PENDING",
                        notes = "Scheduled for afternoon lunch"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med4Id,
                        medicineName = "Paracetamol",
                        dosage = "650 mg",
                        scheduledTime = "04:00 PM",
                        dateString = todayStr,
                        status = "PENDING",
                        notes = "Post-lunch checkup dose"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med5Id,
                        medicineName = "Vitamin D3",
                        dosage = "2000 IU",
                        scheduledTime = "08:00 PM",
                        dateString = todayStr,
                        status = "PENDING",
                        notes = "Night dose"
                    )
                )

                // Yesterday's dose history records
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med2Id,
                        medicineName = "Lisinopril",
                        dosage = "10 mg",
                        scheduledTime = "08:00 AM",
                        dateString = yesterdayStr,
                        status = "TAKEN",
                        actionTimestamp = System.currentTimeMillis() - 86400000L + 3600000L * 8,
                        notes = "Morning dose completed"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med1Id,
                        medicineName = "Amoxicillin",
                        dosage = "500 mg",
                        scheduledTime = "09:00 AM",
                        dateString = yesterdayStr,
                        status = "TAKEN",
                        actionTimestamp = System.currentTimeMillis() - 86400000L + 3600000L * 9,
                        notes = "Taken with water"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med4Id,
                        medicineName = "Paracetamol",
                        dosage = "650 mg",
                        scheduledTime = "04:00 PM",
                        dateString = yesterdayStr,
                        status = "SKIPPED",
                        actionTimestamp = System.currentTimeMillis() - 86400000L + 3600000L * 16,
                        notes = "Skipped - no headache or fever"
                    )
                )
                dao.insertDoseRecord(
                    DoseRecord(
                        medicineId = med5Id,
                        medicineName = "Vitamin D3",
                        dosage = "2000 IU",
                        scheduledTime = "08:00 PM",
                        dateString = yesterdayStr,
                        status = "TAKEN",
                        actionTimestamp = System.currentTimeMillis() - 86400000L + 3600000L * 20,
                        notes = "Night dose taken with milk"
                    )
                )
            }
        }
    }
}
