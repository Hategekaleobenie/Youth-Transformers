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
fun EvangelismEventsScreen(
    viewModel: MinistryViewModel,
    evangelism: List<EvangelismEntity>,
    events: List<MinistryEventEntity>,
    currentLang: AppLanguage
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Evangelism, 1: Events
    var showAddEvangelismDialog by remember { mutableStateOf(false) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    val totalSoulsReached = evangelism.sumOf { it.peopleReachedCount }
    val totalContacts = evangelism.sumOf { it.newContactsCount }

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
                        Icon(Icons.Default.Public, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Evangelism & Ministry Events",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Reaching Young People with the Gospel of Christ",
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
                    title = "Souls Reached",
                    value = "$totalSoulsReached",
                    subtitle = "$totalContacts follow-up contacts",
                    icon = Icons.Default.Favorite,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Missions Held",
                    value = "${evangelism.size}",
                    subtitle = "${events.size} organized events",
                    icon = Icons.Default.Campaign,
                    iconColor = SubtleGold,
                    iconBgColor = GoldContainer,
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
                    text = { Text("Evangelism Missions (${evangelism.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Ministry Gatherings (${events.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Evangelism Tab
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Outreach Missions", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Button(
                        onClick = { showAddEvangelismDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Outreach", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(evangelism, key = { it.id }) { out ->
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
                                text = out.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkCharcoal
                            )
                            Text(
                                text = out.dateStr,
                                fontSize = 11.sp,
                                color = CoolGrey
                            )
                        }

                        Text(
                            text = "Location: ${out.location} • Team: ${out.participantsCount} workers",
                            fontSize = 12.sp,
                            color = DeepForestGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Souls Reached: ${out.peopleReachedCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkCharcoal)
                            Text("New Contacts: ${out.newContactsCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SubtleGold)
                        }

                        if (out.testimonies.isNotBlank()) {
                            Surface(
                                color = SoftBackground,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "Testimony / Notes: ${out.testimonies}",
                                    fontSize = 11.sp,
                                    color = CoolGrey,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Events Tab
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ministry Gatherings & Vigils", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Button(
                        onClick = { showAddEventDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Event", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(events, key = { it.id }) { ev ->
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
                                text = ev.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkCharcoal
                            )
                            StatusBadge(status = ev.status)
                        }

                        Text(
                            text = ev.description,
                            fontSize = 12.sp,
                            color = CoolGrey,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "Date: ${ev.dateStr} ${ev.timeStr} • Venue: ${ev.location}",
                            fontSize = 11.sp,
                            color = DeepForestGreen,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Expected Attendance: ${ev.expectedAttendance} youth • Budget: ${String.format("%,.0f", ev.budget)} RWF",
                            fontSize = 11.sp,
                            color = SubtleGold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Add Evangelism Dialog
    if (showAddEvangelismDialog) {
        var title by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("Kigali City Center") }
        var teamCount by remember { mutableStateOf("12") }
        var soulsReached by remember { mutableStateOf("15") }
        var newContacts by remember { mutableStateOf("8") }
        var testimony by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddEvangelismDialog = false },
            title = { Text("Log Evangelism Outreach Mission") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Mission Title *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = teamCount, onValueChange = { teamCount = it }, label = { Text("Participants") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = soulsReached, onValueChange = { soulsReached = it }, label = { Text("Souls Reached") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newContacts, onValueChange = { newContacts = it }, label = { Text("New Contacts") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = testimony, onValueChange = { testimony = it }, label = { Text("Testimonies / Notes") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && location.isNotBlank()) {
                            viewModel.addEvangelism(
                                EvangelismEntity(
                                    title = title,
                                    location = location,
                                    dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                    teamLead = "Bonheur Ndinzwe",
                                    participantsCount = teamCount.toIntOrNull() ?: 10,
                                    peopleReachedCount = soulsReached.toIntOrNull() ?: 0,
                                    followUpsCount = 0,
                                    newContactsCount = newContacts.toIntOrNull() ?: 0,
                                    testimonies = testimony
                                )
                            )
                            showAddEvangelismDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Log Mission")
                }
            },
            dismissButton = { TextButton(onClick = { showAddEvangelismDialog = false }) { Text("Cancel") } }
        )
    }

    // Add Event Dialog
    if (showAddEventDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var dateTime by remember { mutableStateOf("2025-08-15 18:00") }
        var venue by remember { mutableStateOf("Kigali Main Auditorium") }
        var expected by remember { mutableStateOf("150") }
        var budgetStr by remember { mutableStateOf("100000") }

        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = { Text("Create Ministry Gathering") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Event Name *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = dateTime, onValueChange = { dateTime = it }, label = { Text("Date & Time") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue / Location") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = expected, onValueChange = { expected = it }, label = { Text("Expected Attendance") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = budgetStr, onValueChange = { budgetStr = it }, label = { Text("Budget (RWF)") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addMinistryEvent(
                                MinistryEventEntity(
                                    name = name,
                                    description = description,
                                    dateStr = if (dateTime.length >= 10) dateTime.take(10) else dateTime,
                                    timeStr = if (dateTime.length > 11) dateTime.substring(11) else "18:00",
                                    location = venue,
                                    organizer = "Leo Benie Hategeka",
                                    expectedAttendance = expected.toIntOrNull() ?: 100,
                                    budget = budgetStr.toDoubleOrNull() ?: 0.0,
                                    status = "Upcoming"
                                )
                            )
                            showAddEventDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Create")
                }
            },
            dismissButton = { TextButton(onClick = { showAddEventDialog = false }) { Text("Cancel") } }
        )
    }
}
