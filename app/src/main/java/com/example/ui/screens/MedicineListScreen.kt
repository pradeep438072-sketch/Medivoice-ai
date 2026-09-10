package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.Medicine
import com.example.ui.theme.*

@Composable
fun MedicineListScreen(
    medicines: List<Medicine>,
    onAddMedicine: (Medicine) -> Unit,
    onUpdateMedicine: (Medicine) -> Unit,
    onDeleteMedicine: (Long) -> Unit,
    onTriggerTestAlert: (Medicine) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var medicineToEdit by remember { mutableStateOf<Medicine?>(null) }

    val categories = listOf("All", "Antibiotics", "Blood Pressure", "Diabetes", "Pain Relief", "Vitamins", "Allergy", "Cardio", "Other")

    val filteredMedicines = remember(medicines, searchQuery, selectedCategory) {
        medicines.filter { med ->
            (selectedCategory == "All" || med.category.equals(selectedCategory, ignoreCase = true)) &&
            (searchQuery.isBlank() || med.name.contains(searchQuery, ignoreCase = true) || med.category.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_medicine_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add New Medicine")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar & Filter Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search medicine name or category...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("medicine_search_input")
                )

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealContainer,
                                selectedLabelColor = TealPrimaryDark
                            ),
                            modifier = Modifier.testTag("category_filter_${cat.lowercase()}")
                        )
                    }
                }
            }

            // Medicine List
            if (filteredMedicines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Medication,
                            contentDescription = null,
                            tint = TealPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotBlank()) "No medicines match '$searchQuery'" else "No medicines found in $selectedCategory",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tap the + button below to add your prescribed medicines.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("medicine_list_scroll"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMedicines, key = { it.id }) { med ->
                        MedicineItemCard(
                            medicine = med,
                            onEdit = { medicineToEdit = med },
                            onDelete = { onDeleteMedicine(med.id) },
                            onTestAlert = { onTriggerTestAlert(med) }
                        )
                    }
                }
            }
        }
    }

    // Add or Edit Dialog
    if (showAddDialog || medicineToEdit != null) {
        MedicineFormDialog(
            existingMedicine = medicineToEdit,
            onDismiss = {
                showAddDialog = false
                medicineToEdit = null
            },
            onSave = { medicine ->
                if (medicineToEdit != null) {
                    onUpdateMedicine(medicine)
                } else {
                    onAddMedicine(medicine)
                }
                showAddDialog = false
                medicineToEdit = null
            }
        )
    }
}

@Composable
fun MedicineItemCard(
    medicine: Medicine,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestAlert: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("medicine_card_${medicine.id}")
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Medication,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = medicine.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // 12-Hour AM/PM Time Badge
                Surface(
                    color = TealPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Alarm,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = medicine.reminderTime,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Specs Row: Dosage & Frequency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(text = "Dosage", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = medicine.dosage, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(text = "Frequency", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = medicine.frequency, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (medicine.instructions.isNotBlank()) {
                Text(
                    text = "Note: ${medicine.instructions}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Test Sound, Notification & Voice Alert
                Button(
                    onClick = onTestAlert,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("test_sound_voice_${medicine.id}")
                ) {
                    Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Voice & Alarm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("edit_med_${medicine.id}")
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Medicine", tint = TealPrimary)
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_med_${medicine.id}")
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Medicine", tint = ErrorRed)
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineFormDialog(
    existingMedicine: Medicine?,
    onDismiss: () -> Unit,
    onSave: (Medicine) -> Unit
) {
    var name by remember { mutableStateOf(existingMedicine?.name ?: "") }
    var category by remember { mutableStateOf(existingMedicine?.category ?: "Antibiotics") }
    var dosage by remember { mutableStateOf(existingMedicine?.dosage ?: "500 mg") }
    var frequency by remember { mutableStateOf(existingMedicine?.frequency ?: "Once Daily") }
    var hour by remember {
        mutableStateOf(
            existingMedicine?.reminderTime?.split(":")?.getOrNull(0) ?: "09"
        )
    }
    var minute by remember {
        mutableStateOf(
            existingMedicine?.reminderTime?.split(":")?.getOrNull(1)?.split(" ")?.getOrNull(0) ?: "00"
        )
    }
    var amPm by remember {
        mutableStateOf(
            if (existingMedicine?.reminderTime?.contains("PM") == true) "PM" else "AM"
        )
    }
    var instructions by remember { mutableStateOf(existingMedicine?.instructions ?: "Take after meal with water") }

    val categories = listOf("Antibiotics", "Blood Pressure", "Diabetes", "Pain Relief", "Vitamins", "Allergy", "Cardio", "Other")
    val frequencies = listOf("Once Daily", "Twice Daily", "Three Times Daily", "Every 8 Hours", "As Needed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingMedicine != null) "Edit Medicine Details" else "Add New Medicine",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name *") },
                    placeholder = { Text("e.g. Amoxicillin, Metformin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("form_med_name")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage *") },
                        placeholder = { Text("e.g. 500 mg, 1 Tab") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("form_med_dosage")
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("form_med_category")
                    )
                }

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency") },
                    placeholder = { Text("e.g. Twice Daily") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("form_med_frequency")
                )

                // 12-Hour AM/PM Time Picker (Normal Clock Timing)
                Text(
                    text = "Reminder Time (12-Hour AM/PM Format):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hour,
                        onValueChange = { if (it.length <= 2) hour = it },
                        label = { Text("Hour") },
                        placeholder = { Text("01-12") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("form_med_hour")
                    )
                    Text(":", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    OutlinedTextField(
                        value = minute,
                        onValueChange = { if (it.length <= 2) minute = it },
                        label = { Text("Min") },
                        placeholder = { Text("00-59") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("form_med_minute")
                    )
                    Row(
                        modifier = Modifier.weight(1.2f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = amPm == "AM",
                            onClick = { amPm = "AM" },
                            label = { Text("AM", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = amPm == "PM",
                            onClick = { amPm = "PM" },
                            label = { Text("PM", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    placeholder = { Text("e.g. Take after meal with warm water") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("form_med_instructions")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val formattedHour = hour.padStart(2, '0')
                        val formattedMinute = minute.padStart(2, '0')
                        val fullTime = "$formattedHour:$formattedMinute $amPm"

                        onSave(
                            Medicine(
                                id = existingMedicine?.id ?: 0,
                                name = name.trim(),
                                category = category.trim(),
                                dosage = dosage.trim(),
                                frequency = frequency.trim(),
                                reminderTime = fullTime,
                                instructions = instructions.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("form_save_button")
            ) {
                Text("Save Medicine")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
