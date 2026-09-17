package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryViewModel

@Composable
fun ProjectsEquipmentScreen(
    viewModel: MinistryViewModel,
    projects: List<ProjectEntity>,
    equipment: List<EquipmentEntity>,
    currentLang: AppLanguage
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Projects, 1: Equipment
    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showAddEquipmentDialog by remember { mutableStateOf(false) }
    var projectToUpdate by remember { mutableStateOf<ProjectEntity?>(null) }
    var equipmentToCheckout by remember { mutableStateOf<EquipmentEntity?>(null) }

    val activeProjectsCount = projects.count { it.status == "Active" }
    val availableEquipment = equipment.count { it.status == "Available" }
    val borrowedEquipment = equipment.count { it.status == "Borrowed" }

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
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Projects & Equipment Logistics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Manager: RENE CYUBAHIRO • Ministry Assets",
                            fontSize = 12.sp,
                            color = SubtleGold
                        )
                    }
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Active Projects",
                    value = "$activeProjectsCount",
                    subtitle = "${projects.size} total initiatives",
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Equipment Items",
                    value = "${equipment.sumOf { it.quantity }}",
                    subtitle = "$borrowedEquipment currently checked out",
                    icon = Icons.Default.Mic,
                    iconColor = SubtleGold,
                    iconBgColor = GoldContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab Selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CleanWhite,
                contentColor = DeepForestGreen,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Ministry Projects (${projects.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Equipment Inventory (${equipment.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Projects Tab
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Projects Portfolio", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Button(
                        onClick = { showAddProjectDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Project", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(projects, key = { it.id }) { proj ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = proj.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkCharcoal,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadge(status = proj.status)
                        }
                        Text(
                            text = proj.description,
                            fontSize = 12.sp,
                            color = CoolGrey,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Progress: ${proj.progressPercent}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepForestGreen)
                            Text("Budget: ${String.format("%,.0f", proj.budget)} RWF", fontSize = 12.sp, color = SubtleGold, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { proj.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = DeepForestGreen,
                            trackColor = SageContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lead: ${proj.leader} • Due: ${proj.deadline}",
                                fontSize = 11.sp,
                                color = CoolGrey
                            )
                            TextButton(onClick = { projectToUpdate = proj }) {
                                Text("Update Progress", fontSize = 11.sp, color = DeepForestGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Equipment Tab
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Inventory Items", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Button(
                        onClick = { showAddEquipmentDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Equipment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(equipment, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkCharcoal
                            )
                            StatusBadge(status = item.status)
                        }

                        Text(
                            text = "Category: ${item.category} • Qty: ${item.quantity} • Custodian: ${item.responsiblePerson}",
                            fontSize = 12.sp,
                            color = CoolGrey,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (item.status == "Borrowed" && item.borrowedBy.isNotBlank()) {
                            Surface(
                                color = Color(0xFFFEF3D6),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "Borrowed by: ${item.borrowedBy} (Return by: ${item.returnDate})",
                                    fontSize = 11.sp,
                                    color = Color(0xFF8A5B00),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (item.status == "Available") {
                                TextButton(onClick = { equipmentToCheckout = item }) {
                                    Text("Check Out to Member", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                                }
                            } else if (item.status == "Borrowed") {
                                TextButton(onClick = { viewModel.checkinEquipment(item) }) {
                                    Text("Check In (Returned)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SubtleGold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Add Project Dialog
    if (showAddProjectDialog) {
        var name by remember { mutableStateOf("") }
        var objective by remember { mutableStateOf("") }
        var leader by remember { mutableStateOf("Rene Cyubahiro") }
        var deadline by remember { mutableStateOf("2025-12-31") }
        var budgetStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProjectDialog = false },
            title = { Text("Create New Project") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Project Name *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = objective, onValueChange = { objective = it }, label = { Text("Objective / Scope *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = leader, onValueChange = { leader = it }, label = { Text("Project Leader") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = budgetStr, onValueChange = { budgetStr = it }, label = { Text("Budget (RWF)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Target Deadline") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && objective.isNotBlank()) {
                            viewModel.addProject(
                                ProjectEntity(
                                    name = name,
                                    description = objective,
                                    leader = leader,
                                    startDate = "2025-01-01",
                                    deadline = deadline,
                                    budget = budgetStr.toDoubleOrNull() ?: 0.0,
                                    progressPercent = 0,
                                    status = "Active"
                                )
                            )
                            showAddProjectDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Create Project")
                }
            },
            dismissButton = { TextButton(onClick = { showAddProjectDialog = false }) { Text("Cancel") } }
        )
    }

    // Update Project Progress Dialog
    projectToUpdate?.let { proj ->
        var progress by remember { mutableFloatStateOf(proj.progressPercent.toFloat()) }
        var status by remember { mutableStateOf(proj.status) }

        AlertDialog(
            onDismissRequest = { projectToUpdate = null },
            title = { Text("Update Progress: ${proj.name}") },
            text = {
                Column {
                    Text("Progress: ${progress.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = DeepForestGreen, activeTrackColor = DeepForestGreen)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status:", fontSize = 12.sp, color = CoolGrey)
                    val statuses = listOf("Active", "In Review", "Completed", "Delayed")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(statuses) { s ->
                            FilterChip(selected = status == s, onClick = { status = s }, label = { Text(s, fontSize = 11.sp) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalStatus = if (progress.toInt() == 100) "Completed" else status
                        viewModel.updateProjectProgress(proj, progress.toInt(), finalStatus)
                        projectToUpdate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Save")
                }
            },
            dismissButton = { TextButton(onClick = { projectToUpdate = null }) { Text("Cancel") } }
        )
    }

    // Add Equipment Dialog
    if (showAddEquipmentDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Audio") }
        var qtyStr by remember { mutableStateOf("1") }
        var custodian by remember { mutableStateOf("Rene Cyubahiro") }

        AlertDialog(
            onDismissRequest = { showAddEquipmentDialog = false },
            title = { Text("Add Equipment Item") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Equipment Name *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Category:", fontSize = 12.sp, color = CoolGrey)
                    val cats = listOf("Audio", "Video", "Instruments", "IT", "Furniture")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(cats) { c ->
                            FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c, fontSize = 11.sp) })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = qtyStr, onValueChange = { qtyStr = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = custodian, onValueChange = { custodian = it }, label = { Text("Custodian / Location") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addEquipment(
                                EquipmentEntity(
                                    name = name,
                                    category = category,
                                    quantity = qtyStr.toIntOrNull() ?: 1,
                                    condition = "Good",
                                    location = "Youth Hall",
                                    responsiblePerson = custodian,
                                    status = "Available"
                                )
                            )
                            showAddEquipmentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Register")
                }
            },
            dismissButton = { TextButton(onClick = { showAddEquipmentDialog = false }) { Text("Cancel") } }
        )
    }

    // Checkout Dialog
    equipmentToCheckout?.let { eq ->
        var borrower by remember { mutableStateOf("") }
        var returnDate by remember { mutableStateOf("2025-06-30") }

        AlertDialog(
            onDismissRequest = { equipmentToCheckout = null },
            title = { Text("Check Out: ${eq.name}") },
            text = {
                Column {
                    OutlinedTextField(value = borrower, onValueChange = { borrower = it }, label = { Text("Borrower Name *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = returnDate, onValueChange = { returnDate = it }, label = { Text("Expected Return Date") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (borrower.isNotBlank()) {
                            viewModel.checkoutEquipment(eq, borrower, returnDate)
                            equipmentToCheckout = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Confirm Checkout")
                }
            },
            dismissButton = { TextButton(onClick = { equipmentToCheckout = null }) { Text("Cancel") } }
        )
    }
}
