package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SocialMediaScreen(
    viewModel: MinistryViewModel,
    posts: List<SocialPostEntity>,
    interactions: List<SocialInteractionEntity>,
    currentLang: AppLanguage
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Content Calendar, 1: Social Interaction Inbox
    var selectedPlatformFilter by remember { mutableStateOf("All") }
    var showAddPostDialog by remember { mutableStateOf(false) }
    var showAddInteractionDialog by remember { mutableStateOf(false) }

    val publishedThisWeek = posts.count { it.status == "Published" }
    val weeklyTarget = 3
    val remainingToTarget = maxOf(0, weeklyTarget - publishedThisWeek)
    val progressFraction = (publishedThisWeek.toFloat() / weeklyTarget.toFloat()).coerceAtMost(1f)

    val filteredPosts = posts.filter {
        selectedPlatformFilter == "All" || it.platform.equals(selectedPlatformFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weekly Target Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weekly Gospel Content Target",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DarkCharcoal
                            )
                            Text(
                                text = "Target: Minimum 3 Gospel contents per week",
                                fontSize = 12.sp,
                                color = SubtleGold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(SageContainer, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = DeepForestGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Target: $weeklyTarget/wk", fontSize = 12.sp, color = CoolGrey)
                            Text("Published: $publishedThisWeek", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Remaining", fontSize = 12.sp, color = CoolGrey)
                            Text(
                                text = if (remainingToTarget == 0) "Target Met! ✨" else "$remainingToTarget more needed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remainingToTarget == 0) DeepForestGreen else SubtleGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = DeepForestGreen,
                        trackColor = SageContainer
                    )
                }
            }
        }

        // Section Tabs
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
                    text = { Text("Content Calendar", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Social Interaction (${interactions.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Platform Filters & Add Post Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Platforms:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CoolGrey)
                    Button(
                        onClick = { showAddPostDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Schedule Content", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                val platforms = listOf("All", "Instagram", "TikTok", "YouTube", "WhatsApp", "Facebook")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(platforms) { platform ->
                        FilterChip(
                            selected = selectedPlatformFilter == platform,
                            onClick = { selectedPlatformFilter = platform },
                            label = { Text(platform, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Posts List
            items(filteredPosts, key = { it.id }) { post ->
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
                                text = post.platform,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SubtleGold
                            )
                            StatusBadge(status = post.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = post.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DarkCharcoal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Date: ${post.scheduledDate} • Responsible: ${post.responsiblePerson}",
                            fontSize = 11.sp,
                            color = CoolGrey
                        )

                        if (post.link.isNotBlank()) {
                            Text(
                                text = "Link: ${post.link}",
                                fontSize = 11.sp,
                                color = DeepForestGreen,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (post.status != "Published") {
                                TextButton(onClick = { viewModel.updateSocialPostStatus(post, "Published") }) {
                                    Text("Mark Published", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepForestGreen)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Social Interaction Inbox (Prayer Requests, Comments, Inquiries)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Interactions & Follow-ups:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CoolGrey)
                    Button(
                        onClick = { showAddInteractionDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Message", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(interactions, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.type} • ${item.platform}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SubtleGold
                            )
                            StatusBadge(status = item.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "From: ${item.senderName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "\"${item.message}\"",
                            fontSize = 12.sp,
                            color = CoolGrey,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "Received: ${item.dateStr}",
                            fontSize = 10.sp,
                            color = CoolGrey
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (item.status != "Responded") {
                                TextButton(onClick = { viewModel.updateSocialInteractionStatus(item, "Responded") }) {
                                    Text("Mark Responded", fontSize = 11.sp, color = DeepForestGreen)
                                }
                            }
                            if (item.type.contains("Prayer", ignoreCase = true) || item.type.contains("Follow", ignoreCase = true)) {
                                TextButton(onClick = { viewModel.updateSocialInteractionStatus(item, "Referred to Member Care") }) {
                                    Text("Refer to Cedrick (Member Care)", fontSize = 11.sp, color = SubtleGold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Schedule Content Dialog
    if (showAddPostDialog) {
        var title by remember { mutableStateOf("") }
        var platform by remember { mutableStateOf("Instagram") }
        var dateStr by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
        var link by remember { mutableStateOf("") }
        var responsible by remember { mutableStateOf("Kellia Mwizerwa") }

        AlertDialog(
            onDismissRequest = { showAddPostDialog = false },
            title = { Text("Schedule Gospel Content") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Content Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Platform:", fontSize = 12.sp, color = CoolGrey)
                    val plats = listOf("Instagram", "TikTok", "YouTube", "WhatsApp", "Facebook")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(plats) { p ->
                            FilterChip(
                                selected = platform == p,
                                onClick = { platform = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Public URL / Link (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = responsible,
                        onValueChange = { responsible = it },
                        label = { Text("Responsible Person") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addSocialPost(
                                SocialPostEntity(
                                    title = title,
                                    platform = platform,
                                    scheduledDate = dateStr,
                                    status = "Approved",
                                    link = link,
                                    responsiblePerson = responsible
                                )
                            )
                            showAddPostDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPostDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Log Interaction Dialog
    if (showAddInteractionDialog) {
        var sender by remember { mutableStateOf("") }
        var msgType by remember { mutableStateOf("Prayer Request") }
        var platform by remember { mutableStateOf("Instagram") }
        var message by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddInteractionDialog = false },
            title = { Text("Log Inbound Message / Prayer Request") },
            text = {
                Column {
                    OutlinedTextField(
                        value = sender,
                        onValueChange = { sender = it },
                        label = { Text("Sender Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Type:", fontSize = 12.sp, color = CoolGrey)
                    val types = listOf("Prayer Request", "Question", "Comment", "New Person", "Follow-up Request")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(types) { t ->
                            FilterChip(
                                selected = msgType == t,
                                onClick = { msgType = t },
                                label = { Text(t, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message Details *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sender.isNotBlank() && message.isNotBlank()) {
                            viewModel.addSocialInteraction(msgType, sender, platform, message)
                            showAddInteractionDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Log")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddInteractionDialog = false }) { Text("Cancel") }
            }
        )
    }
}
