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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.util.MinistryStrings
import com.example.ui.viewmodel.MinistryViewModel

@Composable
fun SettingsScreen(
    viewModel: MinistryViewModel,
    currentUser: UserEntity?,
    currentLang: AppLanguage
) {
    var showChangePassDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Info & Identity
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(DeepForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Youth Transformers Logo",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "YOUTH TRANSFORMERS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = DeepForestGreen,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Ministry Management & Discipleship Platform",
                        fontSize = 12.sp,
                        color = SubtleGold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "\"Transforming young lives through Christ.\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = CoolGrey
                    )
                }
            }
        }

        // Language Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Language & Localization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkCharcoal
                    )
                    Text(
                        text = "The platform supports English and Ikinyarwanda for localized youth ministry.",
                        fontSize = 11.sp,
                        color = CoolGrey,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = currentLang == AppLanguage.ENGLISH,
                            onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                            label = { Text("English (EN)", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SageContainer,
                                selectedLabelColor = DeepForestGreen
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = currentLang == AppLanguage.KINYARWANDA,
                            onClick = { viewModel.setLanguage(AppLanguage.KINYARWANDA) },
                            label = { Text("Ikinyarwanda (RW)", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldContainer,
                                selectedLabelColor = OnGoldContainer
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // User Account & Security
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Security & Credentials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Signed in as: ${currentUser?.fullName ?: "Guest"} (${currentUser?.role?.replace("_", " ")})",
                        fontSize = 12.sp,
                        color = DeepForestGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showChangePassDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change My Password")
                    }
                }
            }
        }

        // Section 24: Ministry Principles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SageContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepForestGreen.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Church, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Youth Transformers Ministry Principles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepForestGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val principles = listOf(
                        "1. The software supports ministry; it does not replace spiritual leadership.",
                        "2. Spiritual maturity, readiness to serve, or personal faithfulness must NEVER be judged solely on numerical data or metrics.",
                        "3. Discipleship is relational, patient, and prayer-driven.",
                        "4. Care data, family sensitivities, and personal struggles are pastoral trusts to be safeguarded with absolute integrity."
                    )

                    principles.forEach { principle ->
                        Text(
                            text = principle,
                            fontSize = 12.sp,
                            color = OnSageContainer,
                            modifier = Modifier.padding(vertical = 3.dp),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showChangePassDialog) {
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePassDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it; passError = null },
                        label = { Text("New Password *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it; passError = null },
                        label = { Text("Confirm New Password *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 6) {
                            passError = "Password must be at least 6 characters."
                        } else if (newPass != confirmPass) {
                            passError = "Passwords do not match."
                        } else {
                            viewModel.changePassword(newPass) {
                                showChangePassDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Update Password")
                }
            },
            dismissButton = { TextButton(onClick = { showChangePassDialog = false }) { Text("Cancel") } }
        )
    }
}
