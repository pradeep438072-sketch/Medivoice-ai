package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ActiveReminderDialog
import com.example.ui.components.MediVoiceBottomNav
import com.example.ui.components.MediVoiceTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MediVoiceApp()
            }
        }
    }
}

@Composable
fun MediVoiceApp(mainViewModel: MainViewModel = viewModel()) {
    val authState by mainViewModel.authState.collectAsStateWithLifecycle()
    val currentScreen by mainViewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTime by mainViewModel.currentTime.collectAsStateWithLifecycle()
    val currentDate by mainViewModel.currentDate.collectAsStateWithLifecycle()

    val medicines by mainViewModel.medicines.collectAsStateWithLifecycle()
    val todayDoses by mainViewModel.todayDoses.collectAsStateWithLifecycle()
    val allHistory by mainViewModel.allHistory.collectAsStateWithLifecycle()

    val activeReminder by mainViewModel.alertManager.activeReminder.collectAsStateWithLifecycle()

    // Request Notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (!authState.isLoggedIn) {
        AuthScreen(
            authState = authState,
            onRequestOtp = { email, password -> mainViewModel.requestOtp(email, password) },
            onVerifyOtp = { enteredOtp -> mainViewModel.verifyOtp(enteredOtp) }
        )
    } else {
        Scaffold(
            topBar = {
                MediVoiceTopBar(
                    currentTime = currentTime,
                    currentDate = currentDate,
                    userEmail = authState.userEmail,
                    onOpenVoiceAssistant = { mainViewModel.navigateTo(AppScreen.VOICE) },
                    onLogoutClick = { mainViewModel.logout() }
                )
            },
            bottomBar = {
                MediVoiceBottomNav(
                    currentScreen = currentScreen,
                    onSelectScreen = { screen -> mainViewModel.navigateTo(screen) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        AppScreen.DASHBOARD -> DashboardScreen(
                            currentTime = currentTime,
                            currentDate = currentDate,
                            todayDoses = todayDoses,
                            allMedicines = medicines,
                            onMarkTaken = { id -> mainViewModel.markDoseTaken(id) },
                            onMarkSkipped = { id -> mainViewModel.markDoseSkipped(id) },
                            onTriggerAlert = { dose -> mainViewModel.triggerDoseAlert(dose) },
                            onNavigate = { target -> mainViewModel.navigateTo(target) }
                        )

                        AppScreen.MEDICINES -> MedicineListScreen(
                            medicines = medicines,
                            onAddMedicine = { med -> mainViewModel.addMedicine(med) },
                            onUpdateMedicine = { med -> mainViewModel.updateMedicine(med) },
                            onDeleteMedicine = { id -> mainViewModel.deleteMedicine(id) },
                            onTriggerTestAlert = { med -> mainViewModel.triggerTestAlert(med) }
                        )

                        AppScreen.DOSAGE -> DailyDosageScreen(
                            todayDoses = todayDoses,
                            onMarkTaken = { id -> mainViewModel.markDoseTaken(id) },
                            onMarkSkipped = { id, reason -> mainViewModel.markDoseSkipped(id, reason) },
                            onTriggerAlert = { dose -> mainViewModel.triggerDoseAlert(dose) }
                        )

                        AppScreen.HISTORY -> MedicineHistoryScreen(
                            historyRecords = allHistory
                        )

                        AppScreen.VOICE -> VoiceAssistantScreen(
                            voiceManager = mainViewModel.voiceAssistant,
                            reminderAlertManager = mainViewModel.alertManager,
                            onSendQuery = { query -> mainViewModel.processVoiceQuery(query) }
                        )

                        AppScreen.SUGGESTIONS -> MedicineSuggestionScreen(
                            onAddSuggestedMedicine = { med ->
                                mainViewModel.addMedicine(med)
                                mainViewModel.navigateTo(AppScreen.MEDICINES)
                            }
                        )
                    }
                }
            }
        }

        // High-Priority Active Reminder Dialog Popup with voice & tone
        activeReminder?.let { reminder ->
            ActiveReminderDialog(
                activeReminder = reminder,
                onTakeDose = {
                    reminder.doseRecordId?.let { doseId ->
                        mainViewModel.markDoseTaken(doseId)
                    }
                    mainViewModel.dismissActiveReminder()
                },
                onDismiss = {
                    mainViewModel.dismissActiveReminder()
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

