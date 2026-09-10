package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminder.ActiveReminder
import com.example.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun MediVoiceTopBar(
    currentTime: String,
    currentDate: String,
    userEmail: String,
    onOpenVoiceAssistant: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(TealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HealthAndSafety,
                            contentDescription = "MediVoice Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "MediVoice AI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TealPrimaryDark
                            )
                        )
                        Text(
                            text = userEmail.ifEmpty { "Patient Care" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onOpenVoiceAssistant,
                        modifier = Modifier
                            .testTag("top_voice_button")
                            .clip(CircleShape)
                            .background(TealContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Open Voice Assistant",
                            tint = TealPrimary
                        )
                    }

                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .testTag("top_logout_button")
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Account Logout",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Digital Clock Banner in Normal Clock AM/PM Format
            Surface(
                color = TealPrimaryDark,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "Clock",
                            tint = Color(0xFF80CBC4),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "LIVE CLOCK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80CBC4),
                            letterSpacing = 1.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = currentTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediVoiceBottomNav(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val navItems = listOf(
            Triple(AppScreen.DASHBOARD, Icons.Filled.Dashboard, "Dashboard"),
            Triple(AppScreen.MEDICINES, Icons.Filled.Medication, "Medicines"),
            Triple(AppScreen.DOSAGE, Icons.Filled.CheckCircleOutline, "Dosage"),
            Triple(AppScreen.VOICE, Icons.Filled.RecordVoiceOver, "Voice AI"),
            Triple(AppScreen.HISTORY, Icons.Filled.History, "History"),
            Triple(AppScreen.SUGGESTIONS, Icons.Filled.MedicalServices, "Suggest")
        )

        navItems.forEach { (screen, icon, label) ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectScreen(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
            )
        }
    }
}

@Composable
fun ActiveReminderDialog(
    activeReminder: ActiveReminder,
    onTakeDose: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsActive,
                    contentDescription = "Alert Alarm",
                    tint = ErrorRed,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Medicine Reminder Alert!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = ErrorRedContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = activeReminder.medicineName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF990000)
                            )
                        )
                        Text(
                            text = "Dosage: ${activeReminder.dosage} • ${activeReminder.category}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scheduled Time: ${activeReminder.time}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        if (activeReminder.instructions.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Instructions: ${activeReminder.instructions}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF990000),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = "Voice playing",
                        tint = TealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Voice alert spoken aloud via MediVoice TTS",
                        style = MaterialTheme.typography.bodySmall.copy(color = TealPrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onTakeDose,
                colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                modifier = Modifier.testTag("alert_take_dose_button")
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mark as Taken")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("alert_dismiss_button")
            ) {
                Text("Snooze / Dismiss")
            }
        }
    )
}
