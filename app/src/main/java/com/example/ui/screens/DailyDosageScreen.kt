package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DoseRecord
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyDosageScreen(
    todayDoses: List<DoseRecord>,
    onMarkTaken: (Long) -> Unit,
    onMarkSkipped: (Long, String) -> Unit,
    onTriggerAlert: (DoseRecord) -> Unit
) {
    val takenCount = todayDoses.count { it.status == "TAKEN" }
    val pendingCount = todayDoses.count { it.status == "PENDING" }
    val skippedCount = todayDoses.count { it.status == "SKIPPED" }
    val remainingCount = pendingCount
    val totalCount = todayDoses.size
    val adherencePercent = if (totalCount > 0) ((takenCount.toFloat() / totalCount) * 100).toInt() else 0

    var doseToSkip by remember { mutableStateOf<DoseRecord?>(null) }
    var skipReason by remember { mutableStateOf("Feeling better / not needed") }

    val skipReasons = listOf(
        "Feeling better / not needed",
        "Doctor advised to hold",
        "Experienced mild side effect",
        "Fasting / empty stomach required",
        "Medication currently unavailable"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("daily_dosage_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card with 4 Metrics
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily Dosage Tracking",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Real-time compliance log for today",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Surface(
                                color = if (adherencePercent >= 75) MedicalGreenContainer else WarningAmberContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "$adherencePercent% Adherence",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (adherencePercent >= 75) MedicalGreen else WarningAmber,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // 4 Big Metric Grid Tiles: Taken, Pending, Skipped, Remaining
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DosageMetricBox(
                                modifier = Modifier.weight(1f),
                                title = "Taken",
                                count = takenCount,
                                color = MedicalGreen,
                                bgColor = MedicalGreenContainer,
                                icon = Icons.Filled.CheckCircle
                            )
                            DosageMetricBox(
                                modifier = Modifier.weight(1f),
                                title = "Pending",
                                count = pendingCount,
                                color = WarningAmber,
                                bgColor = WarningAmberContainer,
                                icon = Icons.Filled.HourglassEmpty
                            )
                            DosageMetricBox(
                                modifier = Modifier.weight(1f),
                                title = "Skipped",
                                count = skippedCount,
                                color = Color(0xFF78909C),
                                bgColor = Color(0xFFECEFF1),
                                icon = Icons.Filled.Block
                            )
                            DosageMetricBox(
                                modifier = Modifier.weight(1f),
                                title = "Remaining",
                                count = remainingCount,
                                color = CyanSecondary,
                                bgColor = CyanSecondaryContainer,
                                icon = Icons.Filled.Alarm
                            )
                        }

                        // Progress Indicator
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$takenCount of $totalCount completed doses",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (pendingCount == 0) "All done for today!" else "$remainingCount dose(s) left",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (pendingCount == 0) MedicalGreen else CyanSecondary
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
            }

            // Section: Dose Breakdown List
            item {
                Text(
                    text = "Dose Tracking Timeline",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (todayDoses.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No doses recorded for today. Add medications to generate daily doses.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            } else {
                items(todayDoses, key = { it.id }) { dose ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dosage_record_${dose.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = dose.medicineName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Dosage: ${dose.dosage}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }

                                Surface(
                                    color = when (dose.status) {
                                        "TAKEN" -> MedicalGreenContainer
                                        "SKIPPED" -> Color(0xFFECEFF1)
                                        else -> WarningAmberContainer
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = dose.status,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (dose.status) {
                                            "TAKEN" -> MedicalGreen
                                            "SKIPPED" -> Color(0xFF546E7A)
                                            else -> WarningAmber
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Scheduled: ${dose.scheduledTime}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TealPrimary
                                )

                                if (dose.actionTimestamp > 0L) {
                                    val timeStr = SimpleDateFormat("hh:mm a", Locale.US).format(Date(dose.actionTimestamp))
                                    Text(
                                        text = "• Action at $timeStr",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (dose.notes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${dose.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onTriggerAlert(dose) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play Voice Alert", fontSize = 11.sp)
                                }

                                if (dose.status == "PENDING") {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        TextButton(
                                            onClick = { doseToSkip = dose },
                                            modifier = Modifier.testTag("skip_dose_dialog_button_${dose.id}")
                                        ) {
                                            Text("Skip Dose", color = Color(0xFF78909C), fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = { onMarkTaken(dose.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MedicalGreen),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("mark_taken_button_${dose.id}")
                                        ) {
                                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Mark Taken", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (dose.status == "TAKEN") "Recorded Taken" else "Recorded Skipped",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (dose.status == "TAKEN") MedicalGreen else Color(0xFF78909C)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Skip Dose Reason Dialog
    if (doseToSkip != null) {
        AlertDialog(
            onDismissRequest = { doseToSkip = null },
            title = {
                Text("Reason for Skipping Dose", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Medication: ${doseToSkip?.medicineName} (${doseToSkip?.dosage})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Please record why this dose was not taken:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    skipReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = skipReason == reason,
                                onClick = { skipReason = reason }
                            )
                            Text(text = reason, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        doseToSkip?.let { dose ->
                            onMarkSkipped(dose.id, skipReason)
                        }
                        doseToSkip = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                ) {
                    Text("Confirm Skip")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { doseToSkip = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DosageMetricBox(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    color: Color,
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count.toString(), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}
