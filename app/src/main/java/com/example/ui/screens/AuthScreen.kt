package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthState
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    authState: AuthState,
    onRequestOtp: (String, String) -> Unit,
    onVerifyOtp: (String) -> Unit
) {
    var email by remember { mutableStateOf(authState.userEmail.ifEmpty { "patient@medivoice.ai" }) }
    var password by remember { mutableStateOf("patient123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Logo and Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(TealContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.HealthAndSafety,
                        contentDescription = "MediVoice Shield",
                        tint = TealPrimary,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = "MediVoice AI",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TealPrimaryDark
                    )
                )

                Text(
                    text = if (authState.isAwaitingOtp) "Two-Factor OTP Verification" else "Patient & Caregiver Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                )

                if (authState.error != null) {
                    Surface(
                        color = ErrorRedContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authState.error,
                            style = MaterialTheme.typography.bodySmall.copy(color = ErrorRed),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (authState.successMessage != null && authState.isAwaitingOtp) {
                    Surface(
                        color = MedicalGreenContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authState.successMessage,
                            style = MaterialTheme.typography.bodySmall.copy(color = MedicalGreen),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (!authState.isAwaitingOtp) {
                    // Step 1: Email + Password
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email ID") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Lock, contentDescription = "Password")
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    Button(
                        onClick = { onRequestOtp(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_request_otp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(imageVector = Icons.Filled.LockOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue & Get OTP", fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "Demo Credentials prefilled for convenience. OTP will be simulated securely.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    )
                } else {
                    // Step 2: OTP Verification
                    Surface(
                        color = TealContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "OTP Sent to: ${authState.userEmail}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TealOnContainer)
                            )
                            if (authState.generatedOtp.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Generated OTP: ${authState.generatedOtp}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TealPrimaryDark
                                        )
                                    )
                                    TextButton(
                                        onClick = { enteredOtp = authState.generatedOtp },
                                        modifier = Modifier.testTag("auth_autofill_otp_button")
                                    ) {
                                        Text("Auto-fill", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { if (it.length <= 6) enteredOtp = it },
                        label = { Text("6-Digit OTP Code") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Pin, contentDescription = "OTP")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_otp_input")
                    )

                    Button(
                        onClick = { onVerifyOtp(enteredOtp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_verify_otp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify OTP & Login", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { onRequestOtp(email, password) },
                        modifier = Modifier.testTag("auth_resend_otp_button")
                    ) {
                        Text("Resend Verification Code", color = TealPrimary)
                    }
                }
            }
        }
    }
}
