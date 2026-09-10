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
import com.example.data.Medicine
import com.example.suggestion.OtcRecommendation
import com.example.suggestion.SymptomKnowledgeBase
import com.example.ui.theme.*

@Composable
fun MedicineSuggestionScreen(
    onAddSuggestedMedicine: (Medicine) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val symptoms = SymptomKnowledgeBase.symptoms
    val currentSymptom = symptoms.getOrElse(selectedIndex) { symptoms[0] }
    var addedNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("medicine_suggestion_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Mandatory Doctor / Pharmacist Consultation Warning Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorRedContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medical_disclaimer_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Medical Alert",
                            tint = ErrorRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DOCTOR & PHARMACIST CONSULTATION REQUIRED",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF990000),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "These suggestions are informational OTC reference points only. Always consult a licensed medical doctor or certified pharmacist before starting, stopping, or combining any medications.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF770000),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // 2. Symptom Selector Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select Presenting Symptom",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(symptoms.indices.toList()) { index ->
                            val symptom = symptoms[index]
                            val isSelected = selectedIndex == index
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedIndex = index
                                    addedNotice = null
                                },
                                label = { Text(symptom.symptom, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealContainer,
                                    selectedLabelColor = TealPrimaryDark
                                ),
                                modifier = Modifier.testTag("symptom_chip_$index")
                            )
                        }
                    }
                }
            }

            if (addedNotice != null) {
                item {
                    Surface(
                        color = MedicalGreenContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = MedicalGreen)
                            Text(text = addedNotice!!, fontSize = 13.sp, color = MedicalGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 3. Recommended OTC Medicines
            item {
                Text(
                    text = "Common OTC Medications for ${currentSymptom.symptom}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(currentSymptom.recommendedOtcs) { otc ->
                OtcCard(
                    otc = otc,
                    onAddToReminders = {
                        onAddSuggestedMedicine(
                            Medicine(
                                name = otc.medicineName,
                                category = otc.category,
                                dosage = otc.standardDosage,
                                frequency = otc.defaultFrequency,
                                reminderTime = otc.suggestedTime,
                                instructions = otc.instructions
                            )
                        )
                        addedNotice = "Added ${otc.medicineName} (${otc.standardDosage}) at ${otc.suggestedTime} to your reminders!"
                    }
                )
            }

            // 4. Red Flag Warning (When to see a doctor)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WarningAmberContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.LocalHospital, contentDescription = null, tint = WarningAmber)
                            Text(
                                text = "When to See a Doctor Immediately",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = WarningAmber
                            )
                        }
                        Text(
                            text = currentSymptom.whenToSeeDoctor,
                            fontSize = 12.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            // 5. Non-Pharmacological Home Care Tips
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Supportive Home Care & Hydration",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        currentSymptom.homeCareTips.forEach { tip ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "•", color = TealPrimary, fontWeight = FontWeight.Bold)
                                Text(
                                    text = tip,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OtcCard(
    otc: OtcRecommendation,
    onAddToReminders: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("otc_card_${otc.medicineName.lowercase().replace(" ", "_")}")
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
                Column {
                    Text(
                        text = otc.medicineName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${otc.standardDosage} • ${otc.category}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Surface(
                    color = TealContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = otc.suggestedTime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimaryDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Guidance: ${otc.instructions}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                color = Color(0xFFFFF8E1),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                    Text(text = otc.warning, fontSize = 11.sp, color = Color(0xFFE65100))
                }
            }

            Button(
                onClick = onAddToReminders,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_suggested_med_${otc.medicineName.lowercase().replace(" ", "_")}")
            ) {
                Icon(imageVector = Icons.Filled.AlarmAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add to My Medicine Reminders")
            }
        }
    }
}
