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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnnouncementsScreen(
    viewModel: MinistryViewModel,
    announcements: List<AnnouncementEntity>,
    currentUser: UserEntity?,
    currentLang: AppLanguage
) {
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }
    var selectedAudienceFilter by remember { mutableStateOf("All") }

    val filteredAnnouncements = announcements.filter {
        selectedAudienceFilter == "All" || it.targetAudience.contains(selectedAudienceFilter, ignoreCase = true)
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
                            .background(GoldContainer, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = OnGoldContainer, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ministry Announcements",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Official Communications & Broadcasts",
                            fontSize = 12.sp,
                            color = SubtleGold
                        )
                    }
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
                Text("Bulletin Board", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Button(
                    onClick = { showAddAnnouncementDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            val audiences = listOf("All", "All Members", "Committee", "Level 1", "Social Media")
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(audiences) { a ->
                    FilterChip(
                        selected = selectedAudienceFilter == a,
                        onClick = { selectedAudienceFilter = a },
                        label = { Text(a, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredAnnouncements, key = { it.id }) { ann ->
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
                            text = ann.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarkCharcoal,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(status = ann.targetAudience)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ann.content,
                        fontSize = 13.sp,
                        color = DarkCharcoal,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Published by ${ann.authorName} • ${ann.dateStr}",
                        fontSize = 11.sp,
                        color = SubtleGold,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    if (showAddAnnouncementDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var targetAudience by remember { mutableStateOf("All Members") }

        AlertDialog(
            onDismissRequest = { showAddAnnouncementDialog = false },
            title = { Text("Publish Ministry Announcement") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Target Audience:", fontSize = 12.sp, color = CoolGrey)
                    val auds = listOf("All Members", "Committee", "Level 1", "Media Team")
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(auds) { a ->
                            FilterChip(selected = targetAudience == a, onClick = { targetAudience = a }, label = { Text(a, fontSize = 11.sp) })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Announcement Message *") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            viewModel.addAnnouncement(
                                AnnouncementEntity(
                                    title = title,
                                    content = content,
                                    targetAudience = targetAudience,
                                    authorName = currentUser?.fullName ?: "Leadership",
                                    dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                )
                            )
                            showAddAnnouncementDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Publish")
                }
            },
            dismissButton = { TextButton(onClick = { showAddAnnouncementDialog = false }) { Text("Cancel") } }
        )
    }
}
