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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommitteeScreen(
    viewModel: MinistryViewModel,
    tasks: List<CommitteeTaskEntity>,
    reports: List<CommitteeReportEntity>,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    currentLang: AppLanguage
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Tasks, 1: Reports, 2: Roster
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showSubmitReportDialog by remember { mutableStateOf(false) }
    var reportToView by remember { mutableStateOf<CommitteeReportEntity?>(null) }

    val pendingTasks = tasks.count { !it.isCompleted }
    val completedTasks = tasks.count { it.isCompleted }

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
                        Icon(Icons.Default.Groups, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Executive Committee & Tasks",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Coordinator: GREEN LONDON • Operations & Reports",
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
                    title = "Pending Tasks",
                    value = "$pendingTasks",
                    subtitle = "$completedTasks completed",
                    icon = Icons.Default.PendingActions,
                    iconColor = SubtleGold,
                    iconBgColor = GoldContainer,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Reports Filed",
                    value = "${reports.size}",
                    subtitle = "Operational reports",
                    icon = Icons.Default.Description,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tab Row
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
                    text = { Text("Tasks (${tasks.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Reports (${reports.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Committee Roster", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Tasks Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Assigned Committee Tasks", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Button(
                        onClick = { showAddTaskDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Assign Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(tasks, key = { it.id }) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTaskCompleted(task) },
                            colors = CheckboxDefaults.colors(checkedColor = DeepForestGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (task.isCompleted) CoolGrey else DarkCharcoal,
                                style = if (task.isCompleted) MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                ) else MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Assigned to: ${task.assignedToName} • Due: ${task.deadline}",
                                fontSize = 11.sp,
                                color = CoolGrey
                            )
                        }
                        StatusBadge(status = task.priority)
                    }
                }
            }
        } else if (selectedTab == 1) {
            // Reports Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Operational Progress Reports", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Button(
                        onClick = { showSubmitReportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Create, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Submit Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(reports, key = { it.id }) { rep ->
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
                                text = "${rep.reportType} by ${rep.authorName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DarkCharcoal
                            )
                            StatusBadge(status = rep.status)
                        }
                        Text(
                            text = "Date: ${rep.dateStr}",
                            fontSize = 11.sp,
                            color = CoolGrey,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Accomplished: ${rep.accomplished}",
                            fontSize = 12.sp,
                            color = DarkCharcoal,
                            maxLines = 2
                        )
                        Text(
                            text = "Current focus: ${rep.currentWork}",
                            fontSize = 12.sp,
                            color = DeepForestGreen,
                            maxLines = 1,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { reportToView = rep }) {
                                Text("Read Full 6-Point Report", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                            }
                        }
                    }
                }
            }
        } else {
            // Committee Roster
            item {
                Text("Committee Leaders Directory", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            items(users.filter { it.role != "MEMBER" }, key = { it.id }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.fullName.take(2).uppercase(), fontWeight = FontWeight.Bold, color = DeepForestGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(user.role.replace("_", " "), fontSize = 11.sp, color = SubtleGold)
                            Text("${user.email} • ${user.phone}", fontSize = 11.sp, color = CoolGrey)
                        }
                        StatusBadge(status = if (user.isActive) "Active" else "Inactive")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Submit Report Dialog (Mandatory 6 questions)
    if (showSubmitReportDialog) {
        var reportType by remember { mutableStateOf("Weekly Report") }
        var accomplishments by remember { mutableStateOf("") }
        var inProgress by remember { mutableStateOf("") }
        var challenges by remember { mutableStateOf("") }
        var supportNeeded by remember { mutableStateOf("") }
        var nextSteps by remember { mutableStateOf("") }
        var prayerNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSubmitReportDialog = false },
            title = { Text("Submit Committee Report") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text("Report Frequency:", fontSize = 12.sp, color = CoolGrey)
                        val freqs = listOf("Daily Report", "Weekly Report", "Monthly Report")
                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(freqs) { f ->
                                FilterChip(selected = reportType == f, onClick = { reportType = f }, label = { Text(f, fontSize = 11.sp) })
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = accomplishments,
                            onValueChange = { accomplishments = it },
                            label = { Text("1. What did you accomplish? *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = inProgress,
                            onValueChange = { inProgress = it },
                            label = { Text("2. What are you currently working on? *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = challenges,
                            onValueChange = { challenges = it },
                            label = { Text("3. What challenges did you face?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = supportNeeded,
                            onValueChange = { supportNeeded = it },
                            label = { Text("4. What support do you need from leadership?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = nextSteps,
                            onValueChange = { nextSteps = it },
                            label = { Text("5. What will you do next?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = prayerNotes,
                            onValueChange = { prayerNotes = it },
                            label = { Text("6. Prayer / Ministry Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (accomplishments.isNotBlank() && inProgress.isNotBlank()) {
                            viewModel.submitCommitteeReport(
                                CommitteeReportEntity(
                                    authorName = currentUser?.fullName ?: "Committee Member",
                                    authorRole = currentUser?.role ?: "COMMITTEE",
                                    reportType = reportType,
                                    dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                    accomplished = accomplishments,
                                    currentWork = inProgress,
                                    challenges = challenges,
                                    supportNeeded = supportNeeded,
                                    nextSteps = nextSteps,
                                    prayerNotes = prayerNotes,
                                    status = "Submitted"
                                )
                            )
                            showSubmitReportDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = { TextButton(onClick = { showSubmitReportDialog = false }) { Text("Cancel") } }
        )
    }

    // View Report Dialog
    reportToView?.let { rep ->
        AlertDialog(
            onDismissRequest = { reportToView = null },
            title = {
                Column {
                    Text("${rep.reportType} Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("By ${rep.authorName} • ${rep.dateStr}", fontSize = 11.sp, color = SubtleGold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { ReportBlock("1. Accomplishments", rep.accomplished) }
                    item { ReportBlock("2. Currently Working On", rep.currentWork) }
                    item { ReportBlock("3. Challenges Faced", rep.challenges.ifBlank { "None noted" }) }
                    item { ReportBlock("4. Support Needed", rep.supportNeeded.ifBlank { "None requested" }) }
                    item { ReportBlock("5. Next Steps", rep.nextSteps.ifBlank { "In planning" }) }
                    item { ReportBlock("6. Prayer / Ministry Notes", rep.prayerNotes.ifBlank { "None" }) }
                }
            },
            confirmButton = {
                Button(onClick = { reportToView = null }, colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)) {
                    Text("Close")
                }
            }
        )
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        var title by remember { mutableStateOf("") }
        var assignee by remember { mutableStateOf("Bonheur Ndinzwe") }
        var priority by remember { mutableStateOf("High") }
        var dueDate by remember { mutableStateOf("2025-06-30") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Assign Committee Task") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = assignee, onValueChange = { assignee = it }, label = { Text("Assigned Person *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Priority:", fontSize = 12.sp, color = CoolGrey)
                    val priorities = listOf("Low", "Medium", "High", "Urgent")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(priorities) { p ->
                            FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p, fontSize = 11.sp) })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addCommitteeTask(
                                CommitteeTaskEntity(
                                    title = title,
                                    assignedToName = assignee,
                                    assignedToRole = "COMMITTEE",
                                    deadline = dueDate,
                                    priority = priority,
                                    isCompleted = false
                                )
                            )
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Assign")
                }
            },
            dismissButton = { TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ReportBlock(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DeepForestGreen)
        Text(content, fontSize = 12.sp, color = DarkCharcoal, modifier = Modifier.padding(top = 2.dp))
    }
}
