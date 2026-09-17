package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MinistryRoles
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.MinistryNavSection
import com.example.ui.viewmodel.MinistryViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouthTransformersTheme {
                YouthTransformersApp()
            }
        }
    }
}

@Composable
fun YouthTransformersApp(
    viewModel: MinistryViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentSection by viewModel.selectedSection.collectAsState()
    val currentLang by viewModel.appLanguage.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val attemptedSection by viewModel.attemptedSection.collectAsState()

    // Data streams
    val users by viewModel.users.collectAsState()
    val members by viewModel.members.collectAsState()
    val discipleship by viewModel.discipleship.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val socialPosts by viewModel.socialPosts.collectAsState()
    val socialInteractions by viewModel.socialInteractions.collectAsState()
    val followUpCases by viewModel.followUpCases.collectAsState()
    val bibleStudies by viewModel.bibleStudies.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val financialTransactions by viewModel.financialTransactions.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val equipment by viewModel.equipment.collectAsState()
    val committeeReports by viewModel.committeeReports.collectAsState()
    val committeeTasks by viewModel.committeeTasks.collectAsState()
    val evangelismOutreaches by viewModel.evangelismOutreaches.collectAsState()
    val ministryEvents by viewModel.ministryEvents.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val activityLogs by viewModel.activityLogs.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showMustChangePasswordDialog by remember { mutableStateOf(false) }

    // Snackbar listener
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (currentUser == null) {
        // Enforce authentic credentials login flow
        LoginScreen(
            viewModel = viewModel,
            users = users,
            currentLang = currentLang
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavDrawerContent(
                    currentSection = currentSection,
                    currentUser = currentUser,
                    currentLang = currentLang,
                    onSelectSection = { section ->
                        viewModel.selectSection(section)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    MinistryTopAppBar(
                        currentSection = currentSection,
                        currentUser = currentUser,
                        currentLang = currentLang,
                        onToggleLanguage = {
                            val nextLang = if (currentLang == AppLanguage.ENGLISH) AppLanguage.KINYARWANDA else AppLanguage.ENGLISH
                            viewModel.setLanguage(nextLang)
                        },
                        onMenuClick = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onChangePasswordClick = {
                            showMustChangePasswordDialog = true
                        },
                        onLogoutClick = {
                            viewModel.logout()
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftBackground)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentSection) {
                        MinistryNavSection.FIRST_LOGIN_PASSWORD -> {
                            FirstLoginPasswordScreen(
                                currentUser = currentUser,
                                onPasswordChanged = { newPass ->
                                    viewModel.changePassword(newPass) { }
                                }
                            )
                        }
                        MinistryNavSection.ACCESS_DENIED -> {
                            AccessDeniedScreen(
                                attemptedSection = attemptedSection,
                                currentUser = currentUser,
                                onReturnToDashboard = {
                                    viewModel.selectSection(MinistryNavSection.DASHBOARD)
                                }
                            )
                        }
                        MinistryNavSection.DASHBOARD -> {
                            // Role-specific home dashboards
                            when (currentUser?.role) {
                                MinistryRoles.LEADER -> LeaderDashboardScreen(
                                    viewModel = viewModel,
                                    members = members,
                                    socialPosts = socialPosts,
                                    followUpCases = followUpCases,
                                    bibleStudies = bibleStudies,
                                    financialTransactions = financialTransactions,
                                    projects = projects,
                                    reports = committeeReports,
                                    announcements = announcements,
                                    currentLang = currentLang
                                )
                                MinistryRoles.COMMITTEE_COORDINATOR -> CommitteeScreen(
                                    viewModel = viewModel,
                                    tasks = committeeTasks,
                                    reports = committeeReports,
                                    users = users,
                                    currentUser = currentUser,
                                    currentLang = currentLang
                                )
                                MinistryRoles.LEVEL1_LEADER -> Level1Screen(
                                    viewModel = viewModel,
                                    members = members,
                                    discipleship = discipleship,
                                    recommendations = recommendations,
                                    currentLang = currentLang
                                )
                                MinistryRoles.SOCIAL_MEDIA -> SocialMediaScreen(
                                    viewModel = viewModel,
                                    posts = socialPosts,
                                    interactions = socialInteractions,
                                    currentLang = currentLang
                                )
                                MinistryRoles.MEMBER_CARE -> MemberCareScreen(
                                    viewModel = viewModel,
                                    members = members,
                                    followUps = followUpCases,
                                    currentLang = currentLang
                                )
                                MinistryRoles.BIBLE_STUDY -> BibleStudyScreen(
                                    viewModel = viewModel,
                                    bibleStudies = bibleStudies,
                                    members = members,
                                    currentLang = currentLang
                                )
                                MinistryRoles.ACCOUNTANT -> FinanceScreen(
                                    viewModel = viewModel,
                                    transactions = financialTransactions,
                                    currentLang = currentLang
                                )
                                MinistryRoles.PROJECTS_MANAGER -> ProjectsEquipmentScreen(
                                    viewModel = viewModel,
                                    projects = projects,
                                    equipment = equipment,
                                    currentLang = currentLang
                                )
                                else -> AnnouncementsScreen(
                                    viewModel = viewModel,
                                    announcements = announcements,
                                    currentUser = currentUser,
                                    currentLang = currentLang
                                )
                            }
                        }
                        MinistryNavSection.MEMBERS -> MemberManagementScreen(
                            viewModel = viewModel,
                            members = members,
                            discipleship = discipleship,
                            attendanceRecords = attendanceRecords,
                            followUpCases = followUpCases,
                            currentLang = currentLang
                        )
                        MinistryNavSection.LEVEL_1 -> Level1Screen(
                            viewModel = viewModel,
                            members = members,
                            discipleship = discipleship,
                            recommendations = recommendations,
                            currentLang = currentLang
                        )
                        MinistryNavSection.SOCIAL_MEDIA -> SocialMediaScreen(
                            viewModel = viewModel,
                            posts = socialPosts,
                            interactions = socialInteractions,
                            currentLang = currentLang
                        )
                        MinistryNavSection.MEMBER_CARE -> MemberCareScreen(
                            viewModel = viewModel,
                            members = members,
                            followUps = followUpCases,
                            currentLang = currentLang
                        )
                        MinistryNavSection.BIBLE_STUDY -> BibleStudyScreen(
                            viewModel = viewModel,
                            bibleStudies = bibleStudies,
                            members = members,
                            currentLang = currentLang
                        )
                        MinistryNavSection.ATTENDANCE -> AttendanceScreen(
                            viewModel = viewModel,
                            attendanceRecords = attendanceRecords,
                            members = members,
                            currentLang = currentLang
                        )
                        MinistryNavSection.FINANCE -> FinanceScreen(
                            viewModel = viewModel,
                            transactions = financialTransactions,
                            currentLang = currentLang
                        )
                        MinistryNavSection.PROJECTS, MinistryNavSection.EQUIPMENT -> ProjectsEquipmentScreen(
                            viewModel = viewModel,
                            projects = projects,
                            equipment = equipment,
                            currentLang = currentLang
                        )
                        MinistryNavSection.COMMITTEE, MinistryNavSection.REPORTS -> CommitteeScreen(
                            viewModel = viewModel,
                            tasks = committeeTasks,
                            reports = committeeReports,
                            users = users,
                            currentUser = currentUser,
                            currentLang = currentLang
                        )
                        MinistryNavSection.EVANGELISM, MinistryNavSection.EVENTS -> EvangelismEventsScreen(
                            viewModel = viewModel,
                            evangelism = evangelismOutreaches,
                            events = ministryEvents,
                            currentLang = currentLang
                        )
                        MinistryNavSection.ANNOUNCEMENTS -> AnnouncementsScreen(
                            viewModel = viewModel,
                            announcements = announcements,
                            currentUser = currentUser,
                            currentLang = currentLang
                        )
                        MinistryNavSection.USERS -> UserManagementScreen(
                            viewModel = viewModel,
                            users = users,
                            currentUser = currentUser,
                            currentLang = currentLang
                        )
                        MinistryNavSection.ACTIVITY_LOG -> ActivityLogScreen(
                            viewModel = viewModel,
                            activityLogs = activityLogs,
                            currentLang = currentLang
                        )
                        MinistryNavSection.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            currentUser = currentUser,
                            currentLang = currentLang
                        )
                        MinistryNavSection.MY_PROFILE -> SettingsScreen(
                            viewModel = viewModel,
                            currentUser = currentUser,
                            currentLang = currentLang
                        )
                    }
                }
            }
        }
    }

    // Modal Password change dialog for on-demand update
    if (showMustChangePasswordDialog) {
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = {
                showMustChangePasswordDialog = false
            },
            title = {
                Text(text = "Change Account Password")
            },
            text = {
                Column {
                    Text(
                        text = "Enter a new secure password for your account (minimum 6 characters):",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it; passError = null },
                        label = { Text("New Password *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it; passError = null },
                        label = { Text("Confirm New Password *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(passError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 6) {
                            passError = "Password must be at least 6 characters."
                        } else if (newPass != confirmPass) {
                            passError = "Passwords do not match."
                        } else {
                            viewModel.changePassword(newPass) {
                                showMustChangePasswordDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepForestGreen)
                ) {
                    Text("Update Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMustChangePasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Youth Transformers: Transforming young lives through Christ.", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YouthTransformersTheme { Greeting("Android") }
}
