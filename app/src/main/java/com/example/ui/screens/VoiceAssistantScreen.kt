package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.reminder.ReminderAlertManager
import com.example.ui.theme.*
import com.example.voice.ChatMessage
import com.example.voice.VoiceAssistantManager
import kotlinx.coroutines.launch

@Composable
fun VoiceAssistantScreen(
    voiceManager: VoiceAssistantManager,
    reminderAlertManager: ReminderAlertManager,
    onSendQuery: (String) -> Unit
) {
    val context = LocalContext.current
    val isListening by voiceManager.isListening.collectAsState()
    val isSpeaking by reminderAlertManager.isSpeaking.collectAsState()
    val chatMessages by voiceManager.chatMessages.collectAsState()
    val transcript by voiceManager.transcript.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Keep scrolled to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    // Audio Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening()
        }
    }

    // When transcript updates from speech recognizer, send it
    LaunchedEffect(transcript) {
        if (transcript.isNotBlank()) {
            onSendQuery(transcript)
        }
    }

    // Mic Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val quickCommands = listOf(
        "Check reminders",
        "Daily dosage status",
        "Show medicine history",
        "Add medicine Aspirin 100mg at 10:00 AM",
        "Take Lisinopril",
        "Suggest medicine for headache"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("voice_assistant_screen")
    ) {
        // Python Engine Status Header
        Surface(
            color = TealContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isListening) ErrorRed else MedicalGreen)
                    )
                    Text(
                        text = if (isListening) "MediVoice: Listening to speech..." else "MediVoice Voice AI: Ready",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealPrimaryDark
                    )
                }

                if (isSpeaking) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable { reminderAlertManager.stopSpeaking() }
                    ) {
                        Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = "Speaking", tint = TealPrimary, modifier = Modifier.size(16.dp))
                        Text(text = "Speaking (Tap to Stop)", fontSize = 11.sp, color = TealPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Chat Transcript List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }
        }

        // Quick Suggestion Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 6.dp)
        ) {
            Text(
                text = "Tap to Ask Voice Assistant:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickCommands) { command ->
                    SuggestionChip(
                        onClick = {
                            onSendQuery(command)
                        },
                        label = { Text(command, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // Input & Mic Control Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Interactive Mic Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(if (isListening) ErrorRed else TealPrimary)
                        .clickable {
                            if (isListening) {
                                voiceManager.stopListening()
                            } else {
                                val hasAudio = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasAudio) {
                                    voiceManager.startListening()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        }
                        .testTag("voice_assistant_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Filled.MicOff else Icons.Filled.Mic,
                        contentDescription = "Microphone",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Text Input fallback / typing
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Speak or type command...") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        if (textInput.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val query = textInput
                                    textInput = ""
                                    onSendQuery(query)
                                },
                                modifier = Modifier.testTag("voice_assistant_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = "Send",
                                    tint = TealPrimary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voice_assistant_text_input")
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "USER"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) TealPrimary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
