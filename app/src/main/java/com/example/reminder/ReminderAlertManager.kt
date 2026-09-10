package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.example.data.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class ActiveReminder(
    val medicineName: String,
    val dosage: String,
    val category: String,
    val time: String,
    val instructions: String,
    val doseRecordId: Long? = null
)

class ReminderAlertManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _activeReminder = MutableStateFlow<ActiveReminder?>(null)
    val activeReminder: StateFlow<ActiveReminder?> = _activeReminder.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
        createNotificationChannel()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            tts?.setSpeechRate(0.95f)
            tts?.setPitch(1.05f)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Audio, sound, and voice notifications for scheduled medicine doses"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun triggerReminderAlert(
        medicineName: String,
        dosage: String,
        category: String,
        time: String,
        instructions: String,
        doseRecordId: Long? = null
    ) {
        val alert = ActiveReminder(
            medicineName = medicineName,
            dosage = dosage,
            category = category,
            time = time,
            instructions = instructions,
            doseRecordId = doseRecordId
        )
        _activeReminder.value = alert

        // 1. Play sound alert (Chime/Tone)
        playToneAlert()

        // 2. Vibrate
        vibratePhone()

        // 3. Post system notification
        postSystemNotification(alert)

        // 4. Voice alert via TextToSpeech
        val spokenText = "Attention. It is time to take your $medicineName $dosage. $instructions."
        speakVoiceAlert(spokenText)
    }

    fun playToneAlert() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 800)
        } catch (_: Exception) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
                toneGen.startTone(ToneGenerator.TONE_PROP_PROMPT, 600)
            } catch (_: Exception) {}
        }
    }

    private fun vibratePhone() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 300, 200, 400), -1)
            }
        } catch (_: Exception) {}
    }

    private fun postSystemNotification(alert: ActiveReminder) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Medicine Reminder: ${alert.medicineName} (${alert.dosage})")
                .setContentText("Scheduled at ${alert.time}. ${alert.instructions}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (_: Exception) {}
    }

    fun speakVoiceAlert(text: String, onFinished: (() -> Unit)? = null) {
        if (!isTtsReady) return
        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "medivoice_alert_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun dismissActiveReminder() {
        _activeReminder.value = null
        stopSpeaking()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    companion object {
        const val CHANNEL_ID = "medivoice_reminders_channel"
    }
}
