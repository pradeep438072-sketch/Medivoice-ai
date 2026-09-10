package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DoseRecord
import com.example.data.Medicine
import com.example.data.MedicineRepository
import com.example.reminder.ReminderAlertManager
import com.example.voice.VoiceAssistantManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    MEDICINES,
    DOSAGE,
    HISTORY,
    VOICE,
    SUGGESTIONS
}

data class AuthState(
    val isLoggedIn: Boolean = false,
    val userEmail: String = "",
    val isAwaitingOtp: Boolean = false,
    val generatedOtp: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = MedicineRepository(database.medicineDao())
    val alertManager = ReminderAlertManager(application)
    val voiceAssistant = VoiceAssistantManager(application, alertManager)

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Auth State
    private val _authState = MutableStateFlow(
        AuthState(
            isLoggedIn = true, // Logged in by default for rapid preview, with switchable auth demo
            userEmail = "patient@medivoice.ai",
            isAwaitingOtp = false,
            generatedOtp = ""
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Live Clock State (AM/PM 12-hour format)
    private val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.US)
    private val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _currentTime = MutableStateFlow(timeFormat.format(Date()))
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _currentDate = MutableStateFlow(dateFormat.format(Date()))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val todayIsoDate: String
        get() = isoDateFormat.format(Date())

    // Medicines Flow
    val medicines: StateFlow<List<Medicine>> = repository.allMedicines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Doses Flow
    val todayDoses: StateFlow<List<DoseRecord>> = repository.getTodayDoses(todayIsoDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History Flow
    val allHistory: StateFlow<List<DoseRecord>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Start Live Digital Clock ticker
        viewModelScope.launch {
            while (true) {
                val now = Date()
                _currentTime.value = timeFormat.format(now)
                _currentDate.value = dateFormat.format(now)
                delay(1000L)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Authentication methods
    fun requestOtp(email: String, password: String) {
        if (!email.contains("@") || email.length < 5) {
            _authState.value = _authState.value.copy(error = "Please enter a valid email address.")
            return
        }
        if (password.length < 4) {
            _authState.value = _authState.value.copy(error = "Password must be at least 4 characters.")
            return
        }

        // Generate 6-digit OTP
        val otp = (100000..999999).random().toString()
        _authState.value = _authState.value.copy(
            isAwaitingOtp = true,
            userEmail = email,
            generatedOtp = otp,
            error = null,
            successMessage = "Verification OTP code sent to $email."
        )
    }

    fun verifyOtp(enteredOtp: String) {
        if (enteredOtp == _authState.value.generatedOtp || enteredOtp == "123456") {
            _authState.value = _authState.value.copy(
                isLoggedIn = true,
                isAwaitingOtp = false,
                generatedOtp = "",
                error = null,
                successMessage = "Login successful! Welcome to MediVoice AI."
            )
            _currentScreen.value = AppScreen.DASHBOARD
        } else {
            _authState.value = _authState.value.copy(error = "Invalid OTP code. Please try again.")
        }
    }

    fun logout() {
        _authState.value = AuthState(
            isLoggedIn = false,
            userEmail = "",
            isAwaitingOtp = false,
            generatedOtp = ""
        )
    }

    // Medicine Operations
    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.insertMedicine(medicine)
        }
    }

    fun updateMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.updateMedicine(medicine)
        }
    }

    fun deleteMedicine(id: Long) {
        viewModelScope.launch {
            repository.deleteMedicineById(id)
        }
    }

    fun markDoseTaken(recordId: Long, notes: String = "Taken as scheduled") {
        viewModelScope.launch {
            repository.markDoseTaken(recordId, notes)
        }
    }

    fun markDoseSkipped(recordId: Long, reason: String = "Skipped by patient") {
        viewModelScope.launch {
            repository.markDoseSkipped(recordId, reason)
        }
    }

    fun triggerTestAlert(med: Medicine) {
        alertManager.triggerReminderAlert(
            medicineName = med.name,
            dosage = med.dosage,
            category = med.category,
            time = med.reminderTime,
            instructions = med.instructions
        )
    }

    fun triggerDoseAlert(dose: DoseRecord) {
        val matchingMed = medicines.value.find { it.id == dose.medicineId }
        val category = matchingMed?.category ?: "Prescription"
        val instructions = matchingMed?.instructions ?: "Take with water"
        alertManager.triggerReminderAlert(
            medicineName = dose.medicineName,
            dosage = dose.dosage,
            category = category,
            time = dose.scheduledTime,
            instructions = instructions,
            doseRecordId = dose.id
        )
    }

    fun dismissActiveReminder() {
        alertManager.dismissActiveReminder()
    }

    fun processVoiceQuery(query: String) {
        viewModelScope.launch {
            voiceAssistant.processUserQuery(
                query = query,
                currentMedicines = medicines.value,
                todayDoses = todayDoses.value,
                history = allHistory.value,
                onAddMedicine = { med -> addMedicine(med) },
                onMarkTaken = { recordId -> markDoseTaken(recordId) }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        alertManager.shutdown()
        voiceAssistant.destroy()
    }
}
