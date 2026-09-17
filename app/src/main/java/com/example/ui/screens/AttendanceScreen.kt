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
fun AttendanceScreen(
    viewModel: MinistryViewModel,
    attendanceRecords: List<AttendanceRecordEntity>,
    members: List<MemberEntity>,
    currentLang: AppLanguage
) {
    var showQuickRecordDialog by remember { mutableStateOf(false) }
    var selectedActivityTypeFilter by remember { mutableStateOf("All") }

    val totalRecords = attendanceRecords.size
    val presentCount = attendanceRecords.count { it.status == "Present" }
    val overallRate = if (totalRecords > 0) ((presentCount.toFloat() / totalRecords) * 100).toInt() else 85

    val filteredRecords = attendanceRecords.filter {
        selectedActivityTypeFilter == "All" || it.activityType.equals(selectedActivityTypeFilter, ignoreCase = true)
    }

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
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ministry Attendance Registry",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Secretary & Leadership Tracking",
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
                    title = "Total Marks",
                    value = "$totalRecords",
                    subtitle = "$presentCount present marks",
                    icon = Icons.Default.EventAvailable,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Average Attendance",
                    value = "$overallRate%",
                    subtitle = "Across all gatherings",
                    icon = Icons.Default.TrendingUp,
                    iconColor = SubtleGold,
                    iconBgColor = GoldContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Filter and Quick Record Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Attendance History", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                Button(
                    onClick = { showQuickRecordDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Record Entry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(filteredRecords, key = { it.id }) { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = record.memberName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "${record.activityType}: ${record.activityTitle}",
                            fontSize = 12.sp,
                            color = DeepForestGreen
                        )
                        Text(
                            text = "Date: ${record.dateStr}",
                            fontSize = 11.sp,
                            color = CoolGrey
                        )
                    }
                    StatusBadge(status = record.status)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showQuickRecordDialog) {
        var memberName by remember { mutableStateOf(members.firstOrNull()?.fullName ?: "") }
        var activityType by remember { mutableStateOf("Weekly Bible Study") }
        var activityTitle by remember { mutableStateOf("Fellowship Gathering") }
        var status by remember { mutableStateOf("Present") }
        var dateStr by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }

        AlertDialog(
            onDismissRequest = { showQuickRecordDialog = false },
            title = { Text("Record Attendance Entry") },
            text = {
                Column {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Member Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = activityTitle,
                        onValueChange = { activityTitle = it },
                        label = { Text("Meeting / Event Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Activity Type:", fontSize = 12.sp, color = CoolGrey)
                    val types = listOf("Bible Study", "Saturday Meeting", "Evangelism", "Prayer Night", "Committee", "Special Event")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(types) { t ->
                            FilterChip(
                                selected = activityType == t,
                                onClick = { activityType = t },
                                label = { Text(t, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status:", fontSize = 12.sp, color = CoolGrey)
                    val statuses = listOf("Present", "Absent", "Excused")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(statuses) { s ->
                            FilterChip(
                                selected = status == s,
                                onClick = { status = s },
                                label = { Text(s, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memberName.isNotBlank() && activityTitle.isNotBlank()) {
                            val memberObj = members.firstOrNull { it.fullName.equals(memberName, ignoreCase = true) }
                            viewModel.recordAttendance(
                                AttendanceRecordEntity(
                                    memberId = memberObj?.id ?: 0L,
                                    memberName = memberName,
                                    activityType = activityType,
                                    activityTitle = activityTitle,
                                    dateStr = dateStr,
                                    status = status
                                )
                            )
                            showQuickRecordDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Save Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickRecordDialog = false }) { Text("Cancel") }
            }
        )
    }
}
