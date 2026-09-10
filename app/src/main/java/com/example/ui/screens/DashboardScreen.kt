package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.DoseRecord
import com.example.data.Medicine
import com.example.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    currentTime: String,
    currentDate: String,
    todayDoses: List<DoseRecord>,
    allMedicines: List<Medicine>,
    onMarkTaken: (Long) -> Unit,
    onMarkSkipped: (Long) -> Unit,
    onTriggerAlert: (DoseRecord) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val takenCount = todayDoses.count { it.status == "TAKEN" }
    val pendingCount = todayDoses.count { it.status == "PENDING" }
    val skippedCount = todayDoses.count { it.status == "SKIPPED" }
    val totalCount = todayDoses.size
    val adherencePercent = if (totalCount > 0) ((takenCount.toFloat() / totalCount) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Healthcare Hero Banner with Live Digital Clock (AM/PM)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_live_clock_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF69F0AE))
                        )
                        Text(
                            text = "LIVE HEALTHCARE CLOCK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0F2F1),
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFB2DFDB),
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = TealPrimaryDark,
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Standard 12-Hour AM/PM Reminder Timing",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 2. Today's Dosage Tracking Summary Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_dosage_summary_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Insights,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Today's Dosage Status",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Surface(
                            color = if (adherencePercent >= 70) MedicalGreenContainer else WarningAmberContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$adherencePercent% Completed",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (adherencePercent >= 70) MedicalGreen else WarningAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 4 Stat Badges: Taken, Pending, Skipped, Remaining
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatTile(
                            modifier = Modifier.weight(1f),
                            title = "Taken",
                            count = takenCount,
                            color = MedicalGreen,
                            bgColor = MedicalGreenContainer,
                            icon = Icons.Filled.CheckCircle
                        )
                        StatTile(
                            modifier = Modifier.weight(1f),
                            title = "Pending",
                            count = pendingCount,
                            color = WarningAmber,
                            bgColor = WarningAmberContainer,
                            icon = Icons.Filled.Pending
                        )
                        StatTile(
                            modifier = Modifier.weight(1f),
                            title = "Skipped",
                            count = skippedCount,
                            color = Color(0xFF78909C),
                            bgColor = Color(0xFFECEFF1),
                            icon = Icons.Filled.Cancel
                        )
                        StatTile(
                            modifier = Modifier.weight(1f),
                            title = "Total",
                            count = totalCount,
                            color = CyanSecondary,
                            bgColor = CyanSecondaryContainer,
                            icon = Icons.Filled.CalendarMonth
                        )
                    }

                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) takenCount.toFloat() / totalCount else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MedicalGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // 3. Voice Assistant Quick Callout
        item {
            Surface(
                color = CyanSecondaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AppScreen.VOICE) }
                    .testTag("dashboard_voice_assistant_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CyanSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Voice Assistant",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "MediVoice AI Assistant",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanOnSecondary
                                )
                            )
                            Text(
                                text = "Speak: 'Check reminders' or 'Add medicine'",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyanOnSecondary.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Open Voice Assistant",
                        tint = CyanSecondary
                    )
                }
            }
        }

        // 4. Section Title: Today's Scheduled Medicines
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Medicine Schedule",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                TextButton(
                    onClick = { onNavigate(AppScreen.MEDICINES) },
                    modifier = Modifier.testTag("dashboard_view_all_medicines_button")
                ) {
                    Text("Manage List", fontWeight = FontWeight.Bold, color = TealPrimary)
                }
            }
        }

        if (todayDoses.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MedicalGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No medicines scheduled for today",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Add regular medications in the Medicine List to start tracking reminders.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Button(
                            onClick = { onNavigate(AppScreen.MEDICINES) },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text("Add Medicine")
                        }
                    }
                }
            }
        } else {
            items(todayDoses, key = { it.id }) { dose ->
                TodayDoseCard(
                    dose = dose,
                    allMedicines = allMedicines,
                    onMarkTaken = { onMarkTaken(dose.id) },
                    onMarkSkipped = { onMarkSkipped(dose.id) },
                    onTriggerAlert = { onTriggerAlert(dose) }
                )
            }
        }
    }
}

@Composable
fun StatTile(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    color: Color,
    bgColor: Color,
    icon: ImageVector
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun TodayDoseCard(
    dose: DoseRecord,
    allMedicines: List<Medicine>,
    onMarkTaken: () -> Unit,
    onMarkSkipped: () -> Unit,
    onTriggerAlert: () -> Unit
) {
    val matchingMed = allMedicines.find { it.id == dose.medicineId }
    val category = matchingMed?.category ?: "Prescription"
    val instructions = matchingMed?.instructions ?: "Take with water"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_dose_card_${dose.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (dose.status) {
                                    "TAKEN" -> MedicalGreenContainer
                                    "SKIPPED" -> Color(0xFFECEFF1)
                                    else -> WarningAmberContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (dose.status) {
                                "TAKEN" -> Icons.Filled.Check
                                "SKIPPED" -> Icons.Filled.Close
                                else -> Icons.Filled.Alarm
                            },
                            contentDescription = null,
                            tint = when (dose.status) {
                                "TAKEN" -> MedicalGreen
                                "SKIPPED" -> Color(0xFF78909C)
                                else -> WarningAmber
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = dose.medicineName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${dose.dosage} • $category",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Surface(
                    color = when (dose.status) {
                        "TAKEN" -> MedicalGreenContainer
                        "SKIPPED" -> Color(0xFFECEFF1)
                        else -> TealContainer
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = when (dose.status) {
                                "TAKEN" -> MedicalGreen
                                "SKIPPED" -> Color(0xFF78909C)
                                else -> TealPrimary
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = dose.scheduledTime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (dose.status) {
                                "TAKEN" -> MedicalGreen
                                "SKIPPED" -> Color(0xFF78909C)
                                else -> TealPrimary
                            }
                        )
                    }
                }
            }

            if (instructions.isNotBlank()) {
                Text(
                    text = instructions,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Test Sound & Voice Alert button
                OutlinedButton(
                    onClick = onTriggerAlert,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("test_alert_button_${dose.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = "Test Alert",
                        modifier = Modifier.size(16.dp),
                        tint = TealPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Voice Alert", fontSize = 12.sp, color = TealPrimary)
                }

                Spacer(modifier = Modifier.weight(1f))

                if (dose.status == "PENDING") {
                    TextButton(
                        onClick = onMarkSkipped,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF78909C)),
                        modifier = Modifier.testTag("skip_dose_button_${dose.id}")
                    ) {
                        Text("Skip", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onMarkTaken,
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("take_dose_button_${dose.id}")
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Take Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        color = if (dose.status == "TAKEN") MedicalGreenContainer else Color(0xFFECEFF1),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (dose.status == "TAKEN") "Status: Taken" else "Status: Skipped",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (dose.status == "TAKEN") MedicalGreen else Color(0xFF546E7A),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
