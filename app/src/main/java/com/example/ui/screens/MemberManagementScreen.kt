package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.util.MinistryStrings
import com.example.ui.viewmodel.MinistryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberManagementScreen(
    viewModel: MinistryViewModel,
    members: List<MemberEntity>,
    discipleship: List<DiscipleshipEntity>,
    attendanceRecords: List<AttendanceRecordEntity>,
    followUpCases: List<FollowUpCaseEntity>,
    currentLang: AppLanguage
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var selectedLevelFilter by remember { mutableStateOf("All") }

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var memberToEdit by remember { mutableStateOf<MemberEntity?>(null) }
    var memberToDeactivate by remember { mutableStateOf<MemberEntity?>(null) }

    val selectedMemberDetail by viewModel.selectedMemberDetail.collectAsState()

    val filteredMembers = members.filter { member ->
        val matchesSearch = member.fullName.contains(searchQuery, ignoreCase = true) ||
                member.phone.contains(searchQuery, ignoreCase = true) ||
                member.currentResidence.contains(searchQuery, ignoreCase = true) ||
                member.email.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (selectedStatusFilter) {
            "All" -> true
            else -> member.memberStatus.equals(selectedStatusFilter, ignoreCase = true)
        }

        val matchesLevel = when (selectedLevelFilter) {
            "All" -> true
            else -> member.ministryLevel.equals(selectedLevelFilter, ignoreCase = true)
        }

        matchesSearch && matchesStatus && matchesLevel
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = DeepForestGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text(MinistryStrings.t("add_member", currentLang), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(MinistryStrings.t("search_hint", currentLang)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepForestGreen) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CleanWhite,
                        unfocusedContainerColor = CleanWhite
                    )
                )
            }

            // Status Filter Chips
            item {
                Column {
                    Text("Status Filter:", fontSize = 12.sp, color = CoolGrey, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    val statuses = listOf("All", "Active", "Follow-up required", "Inactive", "Deactivated")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(statuses) { status ->
                            FilterChip(
                                selected = selectedStatusFilter == status,
                                onClick = { selectedStatusFilter = status },
                                label = { Text(status, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SageContainer,
                                    selectedLabelColor = DeepForestGreen
                                )
                            )
                        }
                    }
                }
            }

            // Level Filter Chips
            item {
                Column {
                    Text("Ministry Level Filter:", fontSize = 12.sp, color = CoolGrey, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    val levels = listOf("All", "Level 1", "Level 2", "Leadership", "Member")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(levels) { level ->
                            FilterChip(
                                selected = selectedLevelFilter == level,
                                onClick = { selectedLevelFilter = level },
                                label = { Text(level, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldContainer,
                                    selectedLabelColor = OnGoldContainer
                                )
                            )
                        }
                    }
                }
            }

            // Results count
            item {
                Text(
                    text = "${filteredMembers.size} members found",
                    fontSize = 13.sp,
                    color = CoolGrey,
                    fontWeight = FontWeight.Medium
                )
            }

            // Members List
            items(filteredMembers, key = { it.id }) { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectMemberDetail(member) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SageContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.fullName.take(2).uppercase(),
                                    color = DeepForestGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = "${member.ministryLevel} • Assigned to ${member.assignedLeader}",
                                    fontSize = 12.sp,
                                    color = CoolGrey
                                )
                            }
                            StatusBadge(status = member.memberStatus)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp, color = BorderSubtle)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Phone: ${member.phone}",
                                    fontSize = 12.sp,
                                    color = DarkCharcoal
                                )
                                Text(
                                    text = "Residence: ${member.currentResidence}",
                                    fontSize = 11.sp,
                                    color = CoolGrey
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Att: ${member.attendancePercent}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (member.attendancePercent >= 80) DeepForestGreen else SubtleGold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(onClick = { memberToEdit = member }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CoolGrey, modifier = Modifier.size(18.dp))
                                }
                                if (member.memberStatus != "Deactivated") {
                                    IconButton(onClick = { memberToDeactivate = member }) {
                                        Icon(Icons.Default.PersonOff, contentDescription = "Deactivate", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                } else {
                                    IconButton(onClick = { viewModel.reactivateMember(member) }) {
                                        Icon(Icons.Default.Restore, contentDescription = "Reactivate", tint = DeepForestGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Add Member Dialog
    if (showAddMemberDialog) {
        MemberFormDialog(
            title = "Register New Member",
            initialMember = null,
            onDismiss = { showAddMemberDialog = false },
            onSave = { newMember ->
                viewModel.addMember(newMember)
                showAddMemberDialog = false
            }
        )
    }

    // Edit Member Dialog
    memberToEdit?.let { member ->
        MemberFormDialog(
            title = "Edit Member: ${member.fullName}",
            initialMember = member,
            onDismiss = { memberToEdit = null },
            onSave = { updated ->
                viewModel.updateMember(updated)
                memberToEdit = null
            }
        )
    }

    // Deactivation Confirmation Dialog
    memberToDeactivate?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToDeactivate = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Deactivate Member?") },
            text = {
                Text(
                    "Are you sure you want to deactivate ${member.fullName}? Deactivation preserves all historical attendance, discipleship curriculum, and ministry records while marking the member as inactive.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deactivateMember(member)
                        memberToDeactivate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Deactivate")
                }
            },
            dismissButton = {
                TextButton(onClick = { memberToDeactivate = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Member Profile View Dialog
    selectedMemberDetail?.let { member ->
        MemberProfileDetailDialog(
            member = member,
            discipleshipList = discipleship.filter { it.memberId == member.id },
            attendanceList = attendanceRecords.filter { it.memberId == member.id },
            followUps = followUpCases.filter { it.memberId == member.id },
            onDismiss = { viewModel.selectMemberDetail(null) }
        )
    }
}

@Composable
fun MemberProfileDetailDialog(
    member: MemberEntity,
    discipleshipList: List<DiscipleshipEntity>,
    attendanceList: List<AttendanceRecordEntity>,
    followUps: List<FollowUpCaseEntity>,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Profile", "Discipleship", "Attendance", "Follow-ups")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
            ) {
                Text("Close")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SageContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.fullName.take(2).uppercase(),
                        color = DeepForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(member.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${member.ministryLevel} • ${member.memberStatus}", fontSize = 11.sp, color = SubtleGold)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CleanWhite,
                    contentColor = DeepForestGreen
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> { // Profile Details
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { ProfileField("Full Name", member.fullName) }
                            item { ProfileField("Phone Number", member.phone) }
                            item { ProfileField("Email Address", member.email) }
                            item { ProfileField("Date Joined", member.dateJoined) }
                            item { ProfileField("Current Residence", member.currentResidence) }
                            item { ProfileField("Permanent Residence", member.permanentResidence) }
                            item { ProfileField("Employment Status", member.employmentStatus) }
                            item { ProfileField("Education Status", member.educationStatus) }
                            item { ProfileField("Family Information", member.familyInfo) }
                            item { ProfileField("Ministry Level", member.ministryLevel) }
                            item { ProfileField("Assigned Leader", member.assignedLeader) }
                            item { ProfileField("Bible Study Participation", member.bibleStudyParticipation) }
                            item { ProfileField("Evangelism Outreach", member.evangelismParticipation) }
                            item { ProfileField("Attendance Average", "${member.attendancePercent}%") }
                            item { ProfileField("Personal / Pastoral Notes", member.notes.ifBlank { "None" }) }
                            item {
                                Surface(
                                    color = SageContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(
                                        "Note: Ministry records are kept for pastoral support, encouragement, and shepherding, not as a measure of spiritual worth.",
                                        fontSize = 11.sp,
                                        color = OnSageContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                    1 -> { // Discipleship
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                Text("Level 1 Discipleship Curriculum Progress", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            if (discipleshipList.isEmpty()) {
                                item {
                                    Text("No specific curriculum tracks recorded yet.", fontSize = 12.sp, color = CoolGrey)
                                }
                            } else {
                                items(discipleshipList) { disc ->
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = CleanWhite)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(disc.topicName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                            StatusBadge(status = disc.status)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Attendance
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (attendanceList.isEmpty()) {
                                item {
                                    Text("No attendance entries found for this member.", fontSize = 12.sp, color = CoolGrey)
                                }
                            } else {
                                items(attendanceList) { att ->
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = CleanWhite)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(att.activityTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("${att.activityType} • ${att.dateStr}", fontSize = 11.sp, color = CoolGrey)
                                            }
                                            StatusBadge(status = att.status)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> { // Follow-ups
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (followUps.isEmpty()) {
                                item {
                                    Text("No pastoral follow-up cases on file.", fontSize = 12.sp, color = CoolGrey)
                                }
                            } else {
                                items(followUps) { fCase ->
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = CleanWhite)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Reason: ${fCase.reason}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                StatusBadge(status = fCase.status)
                                            }
                                            Text("Priority: ${fCase.priority} • Assigned: ${fCase.assignedPerson}", fontSize = 11.sp, color = CoolGrey)
                                            Text("Notes: ${fCase.notes}", fontSize = 12.sp, color = DarkCharcoal, modifier = Modifier.padding(top = 4.dp))
                                            if (fCase.resolution.isNotBlank()) {
                                                Text("Resolution: ${fCase.resolution}", fontSize = 11.sp, color = DeepForestGreen)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ProfileField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = CoolGrey, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DarkCharcoal, modifier = Modifier.weight(1.2f))
    }
}

@Composable
fun MemberFormDialog(
    title: String,
    initialMember: MemberEntity?,
    onDismiss: () -> Unit,
    onSave: (MemberEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(initialMember?.fullName ?: "") }
    var phone by remember { mutableStateOf(initialMember?.phone ?: "") }
    var email by remember { mutableStateOf(initialMember?.email ?: "") }
    var currentResidence by remember { mutableStateOf(initialMember?.currentResidence ?: "Kigali") }
    var permanentResidence by remember { mutableStateOf(initialMember?.permanentResidence ?: "") }
    var employmentStatus by remember { mutableStateOf(initialMember?.employmentStatus ?: "Student") }
    var educationStatus by remember { mutableStateOf(initialMember?.educationStatus ?: "Undergraduate") }
    var familyInfo by remember { mutableStateOf(initialMember?.familyInfo ?: "Living with family") }
    var ministryLevel by remember { mutableStateOf(initialMember?.ministryLevel ?: "Level 1") }
    var assignedLeader by remember { mutableStateOf(initialMember?.assignedLeader ?: "Bonheur Ndinzwe") }
    var memberStatus by remember { mutableStateOf(initialMember?.memberStatus ?: "Active") }
    var notes by remember { mutableStateOf(initialMember?.notes ?: "") }

    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank() || phone.isBlank()) {
                        error = "Full Name and Phone are required."
                    } else {
                        val member = initialMember?.copy(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                            currentResidence = currentResidence.trim(),
                            permanentResidence = permanentResidence.trim(),
                            employmentStatus = employmentStatus,
                            educationStatus = educationStatus,
                            familyInfo = familyInfo,
                            ministryLevel = ministryLevel,
                            assignedLeader = assignedLeader,
                            memberStatus = memberStatus,
                            notes = notes.trim()
                        ) ?: MemberEntity(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                            dateJoined = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            currentResidence = currentResidence.trim(),
                            permanentResidence = permanentResidence.trim(),
                            employmentStatus = employmentStatus,
                            educationStatus = educationStatus,
                            familyInfo = familyInfo,
                            ministryLevel = ministryLevel,
                            assignedLeader = assignedLeader,
                            memberStatus = memberStatus,
                            notes = notes.trim()
                        )
                        onSave(member)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
            ) {
                Text("Save Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (error != null) {
                    item {
                        Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        placeholder = { Text("+250 788 ...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = currentResidence,
                        onValueChange = { currentResidence = it },
                        label = { Text("Current Residence") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = permanentResidence,
                        onValueChange = { permanentResidence = it },
                        label = { Text("Permanent Residence") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Ministry Level:", fontSize = 12.sp, color = CoolGrey)
                    val levels = listOf("Level 1", "Level 2", "Leadership", "Member")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(levels) { lvl ->
                            FilterChip(
                                selected = ministryLevel == lvl,
                                onClick = { ministryLevel = lvl },
                                label = { Text(lvl, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Text("Employment Status:", fontSize = 12.sp, color = CoolGrey)
                    val employments = listOf("Student", "Employed", "Self-employed", "Job Seeking", "Prefer not to say")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(employments) { emp ->
                            FilterChip(
                                selected = employmentStatus == emp,
                                onClick = { employmentStatus = emp },
                                label = { Text(emp, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Text("Education Status:", fontSize = 12.sp, color = CoolGrey)
                    val educations = listOf("High School", "Undergraduate", "Graduate", "Prefer not to say")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(educations) { edu ->
                            FilterChip(
                                selected = educationStatus == edu,
                                onClick = { educationStatus = edu },
                                label = { Text(edu, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Text("Family Info (Sensitive):", fontSize = 12.sp, color = CoolGrey)
                    val familyOptions = listOf("Living with family", "Independent", "Prefer not to say")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(familyOptions) { fam ->
                            FilterChip(
                                selected = familyInfo == fam,
                                onClick = { familyInfo = fam },
                                label = { Text(fam, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = assignedLeader,
                        onValueChange = { assignedLeader = it },
                        label = { Text("Assigned Leader") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (Pastoral / Encouragement)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
