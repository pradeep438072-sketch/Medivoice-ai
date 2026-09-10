package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun MedicineHistoryScreen(
    historyRecords: List<DoseRecord>
) {
    var filterStatus by remember { mutableStateOf("ALL") }

    val filteredRecords = remember(historyRecords, filterStatus) {
        when (filterStatus) {
            "TAKEN" -> historyRecords.filter { it.status == "TAKEN" }
            "SKIPPED" -> historyRecords.filter { it.status == "SKIPPED" }
            else -> historyRecords
        }
    }

    val totalTaken = historyRecords.count { it.status == "TAKEN" }
    val totalSkipped = historyRecords.count { it.status == "SKIPPED" }
    val totalLogged = historyRecords.size
    val adherencePercent = if (totalLogged > 0) ((totalTaken.toFloat() / totalLogged) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("medicine_history_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Card
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Medicine Records History",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Complete log of previously taken and skipped doses",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Surface(
                                color = MedicalGreenContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$adherencePercent% Overall",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HistoryStatTile(
                                modifier = Modifier.weight(1f),
                                title = "Total Logs",
                                value = totalLogged.toString(),
                                color = TealPrimary,
                                bgColor = TealContainer
                            )
                            HistoryStatTile(
                                modifier = Modifier.weight(1f),
                                title = "Doses Taken",
                                value = totalTaken.toString(),
                                color = MedicalGreen,
                                bgColor = MedicalGreenContainer
                            )
                            HistoryStatTile(
                                modifier = Modifier.weight(1f),
                                title = "Doses Skipped",
                                value = totalSkipped.toString(),
                                color = Color(0xFF78909C),
                                bgColor = Color(0xFFECEFF1)
                            )
                        }
                    }
                }
            }

            // Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = filterStatus == "ALL",
                        onClick = { filterStatus = "ALL" },
                        label = { Text("All Records ($totalLogged)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealContainer,
                            selectedLabelColor = TealPrimaryDark
                        ),
                        modifier = Modifier.testTag("filter_all_history")
                    )
                    FilterChip(
                        selected = filterStatus == "TAKEN",
                        onClick = { filterStatus = "TAKEN" },
                        label = { Text("Taken ($totalTaken)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalGreenContainer,
                            selectedLabelColor = MedicalGreen
                        ),
                        modifier = Modifier.testTag("filter_taken_history")
                    )
                    FilterChip(
                        selected = filterStatus == "SKIPPED",
                        onClick = { filterStatus = "SKIPPED" },
                        label = { Text("Skipped ($totalSkipped)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFECEFF1),
                            selectedLabelColor = Color(0xFF546E7A)
                        ),
                        modifier = Modifier.testTag("filter_skipped_history")
                    )
                }
            }

            if (filteredRecords.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.HistoryToggleOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No history records found for this filter",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredRecords, key = { it.id }) { record ->
                    HistoryItemCard(record = record)
                }
            }
        }
    }
}

@Composable
fun HistoryStatTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    bgColor: Color
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = title, fontSize = 11.sp, color = color)
        }
    }
}

@Composable
fun HistoryItemCard(record: DoseRecord) {
    val isTaken = record.status == "TAKEN"
    val isSkipped = record.status == "SKIPPED"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_record_card_${record.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = if (isTaken) MedicalGreenContainer else Color(0xFFECEFF1),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isTaken) Icons.Filled.Check else Icons.Filled.Close,
                            contentDescription = null,
                            tint = if (isTaken) MedicalGreen else Color(0xFF78909C),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = record.medicineName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${record.dosage} • Scheduled: ${record.scheduledTime}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    if (record.notes.isNotBlank()) {
                        Text(
                            text = record.notes,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isTaken) MedicalGreen else Color(0xFF78909C),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    color = if (isTaken) MedicalGreenContainer else if (isSkipped) Color(0xFFECEFF1) else WarningAmberContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTaken) MedicalGreen else if (isSkipped) Color(0xFF546E7A) else WarningAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = record.dateString,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
