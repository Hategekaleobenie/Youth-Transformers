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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryViewModel

@Composable
fun UserManagementScreen(
    viewModel: MinistryViewModel,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    currentLang: AppLanguage
) {
    var showCreateUserDialog by remember { mutableStateOf(false) }
    var userToChangeRole by remember { mutableStateOf<UserEntity?>(null) }
    var userToResetPassword by remember { mutableStateOf<UserEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(SageContainer, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "User Accounts & Role Permissions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Leader Portal • Role-Based Access Control (RBAC)",
                            fontSize = 12.sp,
                            color = SubtleGold
                        )
                    }
                }
            }
        }

        // Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Registered Users (${users.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Button(
                    onClick = { showCreateUserDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Users List
        items(users, key = { it.id }) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.fullName.take(2).uppercase(), fontWeight = FontWeight.Bold, color = DeepForestGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                            Text("${user.email} • @${user.username}", fontSize = 11.sp, color = CoolGrey)
                        }
                        StatusBadge(status = if (user.isActive) "Active" else "Deactivated")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GoldContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Role: ${user.role.replace("_", " ")}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnGoldContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row {
                            TextButton(onClick = { userToChangeRole = user }) {
                                Text("Change Role", fontSize = 11.sp, color = DeepForestGreen)
                            }
                            TextButton(onClick = { userToResetPassword = user }) {
                                Text("Reset Pass", fontSize = 11.sp, color = SubtleGold)
                            }
                            if (user.id != currentUser?.id) {
                                TextButton(
                                    onClick = { viewModel.setUserActive(user.id, !user.isActive) }
                                ) {
                                    Text(
                                        if (user.isActive) "Deactivate" else "Activate",
                                        fontSize = 11.sp,
                                        color = if (user.isActive) MaterialTheme.colorScheme.error else DeepForestGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Create User Dialog
    if (showCreateUserDialog) {
        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var role by remember { mutableStateOf(MinistryRole.MEMBER.name) }
        var initialPass by remember { mutableStateOf("Youth2025!") }

        AlertDialog(
            onDismissRequest = { showCreateUserDialog = false },
            title = { Text("Create User Account") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username *") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth()) }
                    item {
                        Text("Assign Ministry Role:", fontSize = 12.sp, color = CoolGrey)
                        val roles = MinistryRoles.ALL_ROLES
                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(roles) { r ->
                                FilterChip(
                                    selected = role.equals(r, ignoreCase = true),
                                    onClick = { role = r },
                                    label = { Text(MinistryRoles.getDisplayName(r), fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    item { OutlinedTextField(value = initialPass, onValueChange = { initialPass = it }, label = { Text("Initial Temporary Password *") }, modifier = Modifier.fillMaxWidth()) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fullName.isNotBlank() && email.isNotBlank() && initialPass.isNotBlank()) {
                            val generatedUid = "uid_" + java.util.UUID.randomUUID().toString().replace("-", "").take(12)
                            viewModel.createUser(
                                UserEntity(
                                    uid = generatedUid,
                                    displayName = fullName,
                                    email = email,
                                    passwordHash = "",
                                    role = role.lowercase(),
                                    phone = phone,
                                    status = "active",
                                    mustChangePassword = true
                                ),
                                initialPass
                            )
                            showCreateUserDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Create")
                }
            },
            dismissButton = { TextButton(onClick = { showCreateUserDialog = false }) { Text("Cancel") } }
        )
    }

    // Change Role Dialog
    userToChangeRole?.let { user ->
        var selectedRole by remember { mutableStateOf(user.role) }

        AlertDialog(
            onDismissRequest = { userToChangeRole = null },
            title = { Text("Change Role for ${user.fullName}") },
            text = {
                Column {
                    Text("Select new role:", fontSize = 12.sp, color = CoolGrey)
                    Spacer(modifier = Modifier.height(8.dp))
                    val roles = MinistryRoles.ALL_ROLES
                    roles.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = selectedRole.equals(r, ignoreCase = true), onClick = { selectedRole = r })
                            Text(MinistryRoles.getDisplayName(r), fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserRole(user.id, selectedRole)
                        userToChangeRole = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Save Role")
                }
            },
            dismissButton = { TextButton(onClick = { userToChangeRole = null }) { Text("Cancel") } }
        )
    }

    // Reset Password Dialog
    userToResetPassword?.let { user ->
        var newPass by remember { mutableStateOf("NewPassword2025!") }

        AlertDialog(
            onDismissRequest = { userToResetPassword = null },
            title = { Text("Reset Password for ${user.fullName}") },
            text = {
                Column {
                    Text("Enter new secure password to assign to this account:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("New Password *") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.isNotBlank()) {
                            viewModel.resetUserPassword(user.id, newPass)
                            userToResetPassword = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Reset Password")
                }
            },
            dismissButton = { TextButton(onClick = { userToResetPassword = null }) { Text("Cancel") } }
        )
    }
}
