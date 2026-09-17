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
fun MemberCareScreen(
    viewModel: MinistryViewModel,
    members: List<MemberEntity>,
    followUps: List<FollowUpCaseEntity>,
    currentLang: AppLanguage
) {
    var showAddCaseDialog by remember { mutableStateOf(false) }
    var caseToResolve by remember { mutableStateOf<FollowUpCaseEntity?>(null) }
    var selectedPriorityFilter by remember { mutableStateOf("All") }

    val urgentCount = followUps.count { it.priority == "Urgent" && it.status != "Resolved" }
    val inProgressCount = followUps.count { it.status == "In Progress" }
    val resolvedCount = followUps.count { it.status == "Resolved" }

    val filteredCases = followUps.filter {
        selectedPriorityFilter == "All" || it.priority.equals(selectedPriorityFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Leader Header Card
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
                            .background(Color(0xFFFDE8D7), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFC05621), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Member Care & Shepherding",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Leader: CEDRICK GISUBIZO • Pastoral Confidentiality Active",
                            fontSize = 12.sp,
                            color = SubtleGold
                        )
                    }
                }
            }
        }

        // Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Cases",
                        value = "${followUps.size}",
                        subtitle = "Support files",
                        icon = Icons.Default.FolderShared,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Urgent Attention",
                        value = "$urgentCount",
                        subtitle = "Immediate contact",
                        icon = Icons.Default.Warning,
                        iconColor = Color(0xFFC05621),
                        iconBgColor = Color(0xFFFDE8D7),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "In Progress",
                        value = "$inProgressCount",
                        subtitle = "Counseling ongoing",
                        icon = Icons.Default.HourglassTop,
                        iconColor = SubtleGold,
                        iconBgColor = GoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Resolved Cases",
                        value = "$resolvedCount",
                        subtitle = "Restored / Supported",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Action & Filter Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pastoral Care Files", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                Button(
                    onClick = { showAddCaseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open Support Case", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Case Cards List
        items(filteredCases, key = { it.id }) { c ->
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
                        Column {
                            Text(
                                text = c.memberName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Category: ${c.reason}",
                                fontSize = 12.sp,
                                color = SubtleGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = c.priority)
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(status = c.status)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Notes: ${c.notes}",
                        fontSize = 12.sp,
                        color = DarkCharcoal
                    )

                    if (c.resolution.isNotBlank()) {
                        Surface(
                            color = SageContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "Resolution / Support outcome: ${c.resolution}",
                                fontSize = 11.sp,
                                color = OnSageContainer,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next follow-up: ${c.nextFollowUpDate} • Counselor: ${c.assignedPerson}",
                            fontSize = 10.sp,
                            color = CoolGrey
                        )

                        if (c.status != "Resolved") {
                            TextButton(onClick = { caseToResolve = c }) {
                                Text("Update / Resolve", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Open Case Dialog
    if (showAddCaseDialog) {
        var memberName by remember { mutableStateOf(members.firstOrNull()?.fullName ?: "") }
        var reason by remember { mutableStateOf("Attendance") }
        var priority by remember { mutableStateOf("Medium") }
        var notes by remember { mutableStateOf("") }
        var nextDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
        var assigned by remember { mutableStateOf("Cedrick Gisubizo") }

        AlertDialog(
            onDismissRequest = { showAddCaseDialog = false },
            title = { Text("Open Member Care Case") },
            text = {
                Column {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Member Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Reason Category:", fontSize = 12.sp, color = CoolGrey)
                    val reasons = listOf("Attendance", "Spiritual", "Academic", "Employment", "Financial", "Family", "General support", "Prefer not to say")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(reasons) { r ->
                            FilterChip(
                                selected = reason == r,
                                onClick = { reason = r },
                                label = { Text(r, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Priority:", fontSize = 12.sp, color = CoolGrey)
                    val priorities = listOf("Low", "Medium", "High", "Urgent")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(priorities) { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Confidential Pastoral Notes *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memberName.isNotBlank() && notes.isNotBlank()) {
                            val memberObj = members.firstOrNull { it.fullName.equals(memberName, ignoreCase = true) }
                            viewModel.addFollowUpCase(
                                FollowUpCaseEntity(
                                    memberId = memberObj?.id ?: 0L,
                                    memberName = memberName,
                                    reason = reason,
                                    priority = priority,
                                    assignedPerson = assigned,
                                    dateOpened = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                    notes = notes,
                                    nextFollowUpDate = nextDate,
                                    status = "Open"
                                )
                            )
                            showAddCaseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Open Case")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCaseDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Resolve / Update Case Dialog
    caseToResolve?.let { c ->
        var resolutionNotes by remember { mutableStateOf(c.resolution) }
        var markResolved by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { caseToResolve = null },
            title = { Text("Update Case: ${c.memberName}") },
            text = {
                Column {
                    Text("Reason: ${c.reason}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resolutionNotes,
                        onValueChange = { resolutionNotes = it },
                        label = { Text("Care Outcome / Pastoral Resolution *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = markResolved, onCheckedChange = { markResolved = it })
                        Text("Mark case as Resolved", fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateFollowUpCase(
                            c.copy(
                                resolution = resolutionNotes,
                                status = if (markResolved) "Resolved" else "In Progress"
                            )
                        )
                        caseToResolve = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Save Outcome")
                }
            },
            dismissButton = {
                TextButton(onClick = { caseToResolve = null }) { Text("Cancel") }
            }
        )
    }
}
