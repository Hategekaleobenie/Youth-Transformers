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
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BibleStudyScreen(
    viewModel: MinistryViewModel,
    bibleStudies: List<BibleStudyEntity>,
    members: List<MemberEntity>,
    currentLang: AppLanguage
) {
    var showAddStudyDialog by remember { mutableStateOf(false) }
    var studyForAttendance by remember { mutableStateOf<BibleStudyEntity?>(null) }

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
                            .background(SageContainer, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bible Study Ministry",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Leader: LIONA AKALIZA • Scriptural Exposition & Doctrine",
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
                    title = "Scheduled Studies",
                    value = "${bibleStudies.size}",
                    subtitle = "This curriculum term",
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Hosts",
                    value = "Kigali & Cells",
                    subtitle = "Weekly fellowships",
                    icon = Icons.Default.Home,
                    iconColor = SubtleGold,
                    iconBgColor = GoldContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Study Sessions Calendar", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkCharcoal)
                Button(
                    onClick = { showAddStudyDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule Study", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Studies List
        items(bibleStudies, key = { it.id }) { study ->
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
                            text = study.curriculumTrack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SubtleGold
                        )
                        Text(
                            text = "${study.dateStr} ${study.timeStr}",
                            fontSize = 11.sp,
                            color = CoolGrey,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = study.topic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkCharcoal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = SageContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Scripture: ${study.scriptureRefs}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepForestGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Preacher: ${study.preacher}", fontSize = 12.sp, color = DarkCharcoal)
                            Text("Host / Location: ${study.host}", fontSize = 11.sp, color = CoolGrey)
                        }

                        Button(
                            onClick = { studyForAttendance = study },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Take Attendance", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Schedule Study Dialog
    if (showAddStudyDialog) {
        var topic by remember { mutableStateOf("") }
        var preacher by remember { mutableStateOf("Liona Akaliza") }
        var host by remember { mutableStateOf("Leo Benie Residence") }
        var passage by remember { mutableStateOf("") }
        var dateTime by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd 18:00", Locale.getDefault()).format(Date())) }
        var track by remember { mutableStateOf("Foundations of Faith") }

        AlertDialog(
            onDismissRequest = { showAddStudyDialog = false },
            title = { Text("Schedule Bible Study Session") },
            text = {
                Column {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Study Topic *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passage,
                        onValueChange = { passage = it },
                        label = { Text("Scripture Passage (e.g. John 15:1-8) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = preacher,
                        onValueChange = { preacher = it },
                        label = { Text("Preacher / Facilitator") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text("Host / Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dateTime,
                        onValueChange = { dateTime = it },
                        label = { Text("Date & Time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (topic.isNotBlank() && passage.isNotBlank()) {
                            viewModel.addBibleStudy(
                                BibleStudyEntity(
                                    topic = topic,
                                    preacher = preacher,
                                    host = host,
                                    dateStr = if (dateTime.length >= 10) dateTime.take(10) else dateTime,
                                    timeStr = if (dateTime.length > 11) dateTime.substring(11) else "18:00",
                                    scriptureRefs = passage,
                                    curriculumTrack = track
                                )
                            )
                            showAddStudyDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudyDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Take Attendance Dialog
    studyForAttendance?.let { study ->
        AlertDialog(
            onDismissRequest = { studyForAttendance = null },
            title = { Text("Record Attendance: ${study.topic}", fontSize = 15.sp) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp)) {
                    Text("Mark members present for this session:", fontSize = 12.sp, color = CoolGrey)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(members.filter { it.memberStatus != "Deactivated" }) { mem ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mem.fullName, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Row {
                                    FilledTonalButton(
                                        onClick = {
                                            viewModel.recordAttendance(
                                                AttendanceRecordEntity(
                                                    memberId = mem.id,
                                                    memberName = mem.fullName,
                                                    activityType = "Bible Study",
                                                    activityTitle = study.topic,
                                                    dateStr = study.dateStr,
                                                    status = "Present"
                                                )
                                            )
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Present", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.recordAttendance(
                                                AttendanceRecordEntity(
                                                    memberId = mem.id,
                                                    memberName = mem.fullName,
                                                    activityType = "Bible Study",
                                                    activityTitle = study.topic,
                                                    dateStr = study.dateStr,
                                                    status = "Absent"
                                                )
                                            )
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Absent", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { studyForAttendance = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Done")
                }
            }
        )
    }
}
