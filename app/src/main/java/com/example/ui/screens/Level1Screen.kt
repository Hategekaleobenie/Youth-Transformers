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
fun Level1Screen(
    viewModel: MinistryViewModel,
    members: List<MemberEntity>,
    discipleship: List<DiscipleshipEntity>,
    recommendations: List<ProgressionRecommendationEntity>,
    currentLang: AppLanguage
) {
    val level1Members = members.filter { it.ministryLevel == "Level 1" && it.memberStatus != "Deactivated" }
    var selectedMemberId by remember { mutableStateOf(level1Members.firstOrNull()?.id ?: 0L) }
    var showRecommendDialog by remember { mutableStateOf(false) }

    val currentMemberDiscipleship = discipleship.filter { it.memberId == selectedMemberId }
    val currentMember = level1Members.firstOrNull { it.id == selectedMemberId }

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
                        Icon(Icons.Default.School, contentDescription = null, tint = DeepForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Level 1 Discipleship Academy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkCharcoal
                        )
                        Text(
                            text = "Leader: BONHEUR NDINZWE • Foundations Curriculum",
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
                    title = "Level 1 Members",
                    value = "${level1Members.size}",
                    subtitle = "Active disciples",
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Curriculum Topics",
                    value = "9 Modules",
                    subtitle = "Salvation to Leadership",
                    icon = Icons.Default.MenuBook,
                    iconColor = SubtleGold,
                    iconBgColor = GoldContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Member Selector Strip
        item {
            Column {
                Text(
                    text = "Select Disciple to Track Curriculum:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(level1Members) { member ->
                        val isSelected = member.id == selectedMemberId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMemberId = member.id },
                            label = { Text(member.fullName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepForestGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Curriculum Matrix for Selected Member
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Curriculum Progress: ${currentMember?.fullName ?: "Select member"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = DarkCharcoal
                            )
                            val completedCount = currentMemberDiscipleship.count { it.status == "Completed" }
                            Text(
                                text = "$completedCount of 9 topics completed",
                                fontSize = 12.sp,
                                color = DeepForestGreen
                            )
                        }

                        Button(
                            onClick = { showRecommendDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SubtleGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Recommend, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Recommend", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentMemberDiscipleship.isEmpty()) {
                        Text(
                            text = "No curriculum entries yet for this member. Topics will initialize automatically.",
                            fontSize = 12.sp,
                            color = CoolGrey
                        )
                    } else {
                        currentMemberDiscipleship.forEach { record ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = SoftBackground,
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, BorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = record.topicName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = DarkCharcoal
                                        )
                                        if (record.notes.isNotBlank()) {
                                            Text(
                                                text = record.notes,
                                                fontSize = 11.sp,
                                                color = CoolGrey
                                            )
                                        }
                                    }

                                    // Quick status cycler: Not Started -> In Progress -> Completed
                                    val nextStatus = when (record.status) {
                                        "Not Started" -> "In Progress"
                                        "In Progress" -> "Completed"
                                        else -> "Not Started"
                                    }

                                    TextButton(
                                        onClick = { viewModel.updateDiscipleshipStatus(record, nextStatus) }
                                    ) {
                                        StatusBadge(status = record.status)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Progression Recommendations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Progression Recommendations (To Ministry Leadership)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkCharcoal
                    )
                    Text(
                        text = "Final spiritual and pastoral advancement decisions remain with ministry leadership.",
                        fontSize = 11.sp,
                        color = CoolGrey,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (recommendations.isEmpty()) {
                        Text("No recommendations submitted yet.", fontSize = 12.sp, color = CoolGrey)
                    } else {
                        recommendations.forEach { rec ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = SoftBackground
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = rec.memberName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        StatusBadge(status = rec.status)
                                    }
                                    Text("Recommended for: ${rec.targetLevel}", fontSize = 12.sp, color = DeepForestGreen, fontWeight = FontWeight.Medium)
                                    Text("Justification: ${rec.reason}", fontSize = 11.sp, color = CoolGrey)
                                    Text("Date: ${rec.dateStr} • By ${rec.recommendedBy}", fontSize = 10.sp, color = SubtleGold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Recommendation Dialog
    if (showRecommendDialog && currentMember != null) {
        var targetLevel by remember { mutableStateOf("Level 2 Discipleship") }
        var reason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRecommendDialog = false },
            title = { Text("Recommend ${currentMember.fullName} for Progression") },
            text = {
                Column {
                    Text("Select target level and provide pastoral basis for leadership review:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetLevel,
                        onValueChange = { targetLevel = it },
                        label = { Text("Target Level") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason & Evidence of Growth") },
                        placeholder = { Text("E.g. Completed foundations, active prayer life...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reason.isNotBlank()) {
                            viewModel.recommendProgression(currentMember.id, currentMember.fullName, targetLevel, reason)
                            showRecommendDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Submit to Leader")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecommendDialog = false }) { Text("Cancel") }
            }
        )
    }
}
