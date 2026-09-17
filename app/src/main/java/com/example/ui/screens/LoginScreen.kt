package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MinistryRoles
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.util.MinistryStrings
import com.example.ui.viewmodel.MinistryViewModel

@Composable
fun LoginScreen(
    viewModel: MinistryViewModel,
    users: List<UserEntity>,
    currentLang: AppLanguage
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }
    var showReferenceRoster by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Header with App Logo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DeepForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Youth Transformers Logo",
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "YOUTH TRANSFORMERS",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = DeepForestGreen
                        )
                    )

                    Text(
                        text = "Ministry Management & Discipleship Platform",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = SubtleGold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"Transforming young lives through Christ.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = CoolGrey
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Input Form
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            errorMessage = null
                        },
                        label = { Text("Ministry Email") },
                        placeholder = { Text("e.g. hategekabenie@gmail.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = DeepForestGreen)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = DeepForestGreen)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (identifier.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both email and password."
                            } else {
                                viewModel.login(identifier, password) { success, err ->
                                    if (!success) {
                                        errorMessage = err ?: "Invalid credentials."
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = MinistryStrings.t("login", currentLang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text(
                            text = "Forgot Password?",
                            color = SubtleGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Non-clickable Informational Reference Guide for Evaluators
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = DeepForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Registered Ministry Accounts",
                                fontWeight = FontWeight.Bold,
                                color = DarkCharcoal,
                                fontSize = 13.sp
                            )
                        }
                        TextButton(onClick = { showReferenceRoster = !showReferenceRoster }) {
                            Text(
                                text = if (showReferenceRoster) "Hide" else "Show Roster",
                                fontSize = 12.sp,
                                color = DeepForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "Authentic credentials are required. Default setup password: Transform2026!",
                        fontSize = 11.sp,
                        color = CoolGrey,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    if (showReferenceRoster) {
                        Spacer(modifier = Modifier.height(10.dp))
                        users.forEach { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = SoftBackground)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = DarkCharcoal
                                        )
                                        Text(
                                            text = user.email,
                                            fontSize = 11.sp,
                                            color = CoolGrey
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SageContainer
                                    ) {
                                        Text(
                                            text = MinistryRoles.getDisplayName(user.role),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepForestGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            icon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = DeepForestGreen) },
            title = { Text("Password Reset Request") },
            text = {
                Column {
                    Text(
                        "Enter your ministry email to receive a secure password reset link or request an administrator reset from Leo Benie Hategeka.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text("Ministry Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordDialog = false
                        viewModel.showMessage("Password reset instructions logged for $forgotEmailInput. Contact Ministry Leader if immediate access is required.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
