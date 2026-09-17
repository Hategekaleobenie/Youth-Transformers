package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.util.MinistryStrings
import com.example.ui.viewmodel.MinistryNavSection
import com.example.ui.viewmodel.MinistryViewModel

@Composable
fun LeaderDashboardScreen(
    viewModel: MinistryViewModel,
    members: List<MemberEntity>,
    socialPosts: List<SocialPostEntity>,
    followUpCases: List<FollowUpCaseEntity>,
    bibleStudies: List<BibleStudyEntity>,
    financialTransactions: List<FinancialTransactionEntity>,
    projects: List<ProjectEntity>,
    reports: List<CommitteeReportEntity>,
    announcements: List<AnnouncementEntity>,
    currentLang: AppLanguage
) {
    val totalMembers = members.size
    val activeMembers = members.count { it.memberStatus == "Active" }
    val newMembers = members.count { it.dateJoined.startsWith("2025") || it.dateJoined.startsWith("2026") }
    val followUpNeeded = members.count { it.memberStatus == "Follow-up required" }
    val urgentFollowUps = followUpCases.filter { it.priority == "Urgent" && it.status != "Resolved" }

    val totalIncome = financialTransactions.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = financialTransactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val activeProjectsCount = projects.count { it.status == "Active" }
    val completedProjectsCount = projects.count { it.status == "Completed" }

    val publishedPostsCount = socialPosts.count { it.status == "Published" }
    val pendingReportsCount = reports.count { it.status == "Submitted" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fellowship Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_fellowship),
                            contentDescription = "Fellowship Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xCC133E2B))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Youth Transformers Leader Portal",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "General Administrative Oversight & Ministry Health",
                                color = RadiantGold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Important Alerts Banner
        if (urgentFollowUps.isNotEmpty() || followUpNeeded > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF4E6)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF7CE98))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFC05621),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Important Ministry Alerts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF7B341E)
                            )
                            Text(
                                text = "$followUpNeeded members need follow-up. ${urgentFollowUps.size} urgent pastoral care cases pending.",
                                fontSize = 12.sp,
                                color = Color(0xFF7B341E)
                            )
                        }
                        TextButton(onClick = { viewModel.selectSection(MinistryNavSection.MEMBER_CARE) }) {
                            Text("Review", color = Color(0xFFC05621), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Primary Stats Grid
        item {
            Text(
                text = "Key Operational Metrics",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DarkCharcoal
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Members",
                        value = "$totalMembers",
                        subtitle = "$activeMembers active souls",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "New Members",
                        value = "$newMembers",
                        subtitle = "Joined this year",
                        icon = Icons.Default.PersonAdd,
                        iconColor = SubtleGold,
                        iconBgColor = GoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Need Follow-up",
                        value = "$followUpNeeded",
                        subtitle = "${urgentFollowUps.size} urgent pastoral",
                        icon = Icons.Default.Favorite,
                        iconColor = Color(0xFFC05621),
                        iconBgColor = Color(0xFFFDE8D7),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Average Attendance",
                        value = "84%",
                        subtitle = "Across all cells",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Bible Studies",
                        value = "${bibleStudies.size}",
                        subtitle = "Scheduled sessions",
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Gospel Media",
                        value = "$publishedPostsCount",
                        subtitle = "Weekly target: 3/wk",
                        icon = Icons.Default.Share,
                        iconColor = SubtleGold,
                        iconBgColor = GoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Active Projects",
                        value = "$activeProjectsCount",
                        subtitle = "$completedProjectsCount completed",
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Treasury Balance",
                        value = "${String.format("%,.0f", balance)} RWF",
                        subtitle = "Income: ${String.format("%,.0f", totalIncome)}",
                        icon = Icons.Default.AccountBalance,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section 17: Leader Ministry Health Overview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = DeepForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ministry Operational Progress Targets",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DarkCharcoal
                        )
                    }

                    Text(
                        text = "Note: These percentages represent operational progress toward defined ministry targets, not spiritual rankings.",
                        fontSize = 11.sp,
                        color = CoolGrey,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val targets = listOf(
                        Pair("Bible Study", 0.90f),
                        Pair("Member Care", 0.70f),
                        Pair("Evangelism", 0.80f),
                        Pair("Social Media", 1.00f),
                        Pair("Finance & Accounting", 0.82f),
                        Pair("Projects & Infrastructure", 0.72f),
                        Pair("General Attendance", 0.81f)
                    )

                    targets.forEach { (label, progress) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (progress >= 0.85f) DeepForestGreen else SubtleGold,
                                trackColor = SageContainer
                            )
                        }
                    }
                }
            }
        }

        // Recent Announcements
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = DeepForestGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recent Announcements",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        TextButton(onClick = { viewModel.selectSection(MinistryNavSection.ANNOUNCEMENTS) }) {
                            Text("View All", color = SubtleGold, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    announcements.take(3).forEach { ann ->
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
                                        text = ann.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DarkCharcoal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(status = ann.targetAudience)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ann.content,
                                    fontSize = 12.sp,
                                    color = CoolGrey,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "By ${ann.authorName} • ${ann.dateStr}",
                                    fontSize = 10.sp,
                                    color = SubtleGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Text(
                text = "Ministry Department Portals",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DarkCharcoal
            )
        }

        item {
            val departments = listOf(
                Triple("Members Directory", Icons.Default.People, MinistryNavSection.MEMBERS),
                Triple("Level 1 Discipleship", Icons.Default.School, MinistryNavSection.LEVEL_1),
                Triple("Bible Study Ministry", Icons.Default.MenuBook, MinistryNavSection.BIBLE_STUDY),
                Triple("Member Care & Follow-up", Icons.Default.Favorite, MinistryNavSection.MEMBER_CARE),
                Triple("Social Media Content", Icons.Default.Share, MinistryNavSection.SOCIAL_MEDIA),
                Triple("Treasury & Finance", Icons.Default.AttachMoney, MinistryNavSection.FINANCE),
                Triple("Projects & Equipment", Icons.Default.Assignment, MinistryNavSection.PROJECTS),
                Triple("Committee Coordination", Icons.Default.Groups, MinistryNavSection.COMMITTEE)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                departments.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { (name, icon, section) ->
                            OutlinedCard(
                                onClick = { viewModel.selectSection(section) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = DeepForestGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
