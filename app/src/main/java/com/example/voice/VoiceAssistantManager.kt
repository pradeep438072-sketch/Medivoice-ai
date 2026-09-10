package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.data.DoseRecord
import com.example.data.Medicine
import com.example.reminder.ReminderAlertManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString() + "_" + (0..999).random(),
    val sender: String, // "USER" or "ASSISTANT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class AssistantCommandResult {
    data class AddedMedicine(val medicine: Medicine) : AssistantCommandResult()
    data class MarkedDoseTaken(val medicineName: String) : AssistantCommandResult()
    data class GeneralResponse(val message: String) : AssistantCommandResult()
}

class VoiceAssistantManager(
    private val context: Context,
    private val reminderAlertManager: ReminderAlertManager
) {
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ASSISTANT",
                text = "Hello! I am MediVoice AI Assistant. You can speak or type to add medicines, check reminders, track daily dosage, or view your medicine history."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        initSpeechRecognizer()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                        }

                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }

                        override fun onError(error: Int) {
                            _isListening.value = false
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                val spokenText = matches[0]
                                _transcript.value = spokenText
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                _transcript.value = matches[0]
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            } catch (_: Exception) {}
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak a medicine command...")
        }
        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (_: Exception) {
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isListening.value = false
    }

    suspend fun processUserQuery(
        query: String,
        currentMedicines: List<Medicine>,
        todayDoses: List<DoseRecord>,
        history: List<DoseRecord>,
        onAddMedicine: suspend (Medicine) -> Unit,
        onMarkTaken: suspend (Long) -> Unit
    ): String {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return ""

        // Add user message to transcript
        addMessage("USER", trimmed)

        val lower = trimmed.lowercase()
        val responseText: String

        when {
            // 1. Check reminders
            lower.contains("reminder") || lower.contains("what medicine") || lower.contains("today medicine") || lower.contains("schedule") -> {
                val pending = todayDoses.filter { it.status == "PENDING" }
                if (pending.isEmpty()) {
                    responseText = "You have no pending medicine reminders for today. All scheduled doses have been addressed!"
                } else {
                    val listStr = pending.joinToString("; ") { "${it.medicineName} (${it.dosage}) at ${it.scheduledTime}" }
                    responseText = "You have ${pending.size} upcoming reminders today: $listStr."
                }
            }

            // 2. Check Daily Dosage
            lower.contains("dosage") || lower.contains("dose") || lower.contains("how many") || lower.contains("remaining") -> {
                val taken = todayDoses.count { it.status == "TAKEN" }
                val pending = todayDoses.count { it.status == "PENDING" }
                val skipped = todayDoses.count { it.status == "SKIPPED" }
                val total = todayDoses.size
                responseText = "Daily dosage status: $taken doses taken, $pending doses pending, $skipped skipped out of $total total scheduled doses."
            }

            // 3. Check Medicine History
            lower.contains("history") || lower.contains("record") || lower.contains("previous") -> {
                val takenCount = history.count { it.status == "TAKEN" }
                val lastRecord = history.firstOrNull()
                if (lastRecord != null) {
                    responseText = "You have $takenCount taken dose records in your history. Most recent was ${lastRecord.medicineName} marked ${lastRecord.status.lowercase()} on ${lastRecord.dateString}."
                } else {
                    responseText = "No medicine history records found yet."
                }
            }

            // 4. Mark Medicine Taken
            lower.startsWith("take ") || lower.contains("took ") || lower.contains("mark taken") -> {
                val candidate = todayDoses.find { dose ->
                    lower.contains(dose.medicineName.lowercase()) && dose.status == "PENDING"
                } ?: todayDoses.firstOrNull { it.status == "PENDING" }

                if (candidate != null) {
                    onMarkTaken(candidate.id)
                    responseText = "I have marked ${candidate.medicineName} (${candidate.dosage}) as taken. Great job staying on track!"
                } else {
                    responseText = "Could not find a pending medicine matching your command to mark as taken."
                }
            }

            // 5. Add Medicine Command (e.g. "Add medicine Aspirin 100mg at 10:00 AM")
            lower.contains("add ") || lower.contains("new medicine") -> {
                val parsed = parseAddMedicineCommand(trimmed)
                if (parsed != null) {
                    onAddMedicine(parsed)
                    responseText = "Added ${parsed.name} (${parsed.dosage}) for ${parsed.category} at ${parsed.reminderTime}. Daily reminder is set!"
                } else {
                    responseText = "I recognized you want to add a medicine. Please specify the name, dosage, and time (e.g., 'Add medicine Aspirin 100mg at 10:00 AM')."
                }
            }

            // 6. Symptom suggestion query
            lower.contains("suggest") || lower.contains("headache") || lower.contains("fever") || lower.contains("cold") || lower.contains("cough") || lower.contains("pain") -> {
                responseText = "For common mild symptoms, please check our Safe Medicine Suggestion tab. Notice: Always consult a certified physician or pharmacist before taking medications."
            }

            // Fallback general guidance
            else -> {
                responseText = "I heard '$trimmed'. You can ask me: 'Check reminders', 'Daily dosage status', 'Show history', 'Take [Medicine Name]', or 'Add medicine [Name] [Dosage] at [Time]'."
            }
        }

        // Add assistant response to transcript
        addMessage("ASSISTANT", responseText)

        // Voice alert / speak out response
        reminderAlertManager.speakVoiceAlert(responseText)

        return responseText
    }

    private fun parseAddMedicineCommand(query: String): Medicine? {
        val clean = query.replace("add medicine", "", ignoreCase = true)
            .replace("add", "", ignoreCase = true)
            .trim()

        val atParts = clean.split(" at ", ignoreCase = true)
        val nameAndDose = atParts[0].trim()
        val timePart = if (atParts.size > 1) {
            val rawTime = atParts[1].trim().uppercase()
            if (rawTime.contains("AM") || rawTime.contains("PM")) rawTime else "$rawTime AM"
        } else {
            "09:00 AM"
        }

        val tokens = nameAndDose.split(" ")
        if (tokens.isEmpty() || tokens[0].isBlank()) return null

        val name = tokens[0].replaceFirstChar { it.uppercase() }
        val dosage = if (tokens.size > 1) tokens.drop(1).joinToString(" ") else "1 dose"

        val category = when {
            name.contains("cillin", true) || name.contains("biotic", true) -> "Antibiotics"
            name.contains("para", true) || name.contains("ibu", true) || name.contains("aspirin", true) -> "Pain Relief"
            name.contains("metformin", true) || name.contains("glip", true) -> "Diabetes"
            name.contains("pril", true) || name.contains("artan", true) -> "Blood Pressure"
            name.contains("vit", true) || name.contains("zinc", true) -> "Vitamins"
            else -> "General Health"
        }

        return Medicine(
            name = name,
            category = category,
            dosage = dosage,
            frequency = "Once Daily",
            reminderTime = timePart,
            instructions = "Take as prescribed with water"
        )
    }

    private fun addMessage(sender: String, text: String) {
        _chatMessages.value = _chatMessages.value + ChatMessage(sender = sender, text = text)
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
    }
}
