package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.MinistryRepository
import com.example.security.AuthorizationService
import com.example.security.Permission
import com.example.ui.util.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MinistryNavSection(val labelKey: String) {
    DASHBOARD("nav_dashboard"),
    MEMBERS("nav_members"),
    LEVEL_1("discipleship"),
    SOCIAL_MEDIA("nav_social_media"),
    MEMBER_CARE("nav_member_care"),
    BIBLE_STUDY("nav_bible_study"),
    ATTENDANCE("nav_attendance"),
    FINANCE("nav_finance"),
    PROJECTS("nav_projects"),
    EQUIPMENT("nav_equipment"),
    COMMITTEE("nav_committee"),
    REPORTS("nav_reports"),
    EVANGELISM("nav_evangelism"),
    EVENTS("nav_events"),
    ANNOUNCEMENTS("nav_announcements"),
    USERS("nav_users"),
    ACTIVITY_LOG("nav_activity_log"),
    SETTINGS("nav_settings"),
    MY_PROFILE("my_profile"),
    ACCESS_DENIED("access_denied"),
    FIRST_LOGIN_PASSWORD("first_login_password")
}

class MinistryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MinistryRepository

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _selectedSection = MutableStateFlow(MinistryNavSection.DASHBOARD)
    val selectedSection: StateFlow<MinistryNavSection> = _selectedSection.asStateFlow()

    private val _attemptedSection = MutableStateFlow("Dashboard")
    val attemptedSection: StateFlow<String> = _attemptedSection.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _selectedMemberDetail = MutableStateFlow<MemberEntity?>(null)
    val selectedMemberDetail: StateFlow<MemberEntity?> = _selectedMemberDetail.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = MinistryRepository(database.dao())
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkAndSeedDatabase()
        }
    }

    // Flows
    val users = repository.getAllUsers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val members = repository.getAllMembers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val discipleship = repository.getAllDiscipleship().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val recommendations = repository.getAllRecommendations().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val socialPosts = repository.getAllSocialPosts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val socialInteractions = repository.getAllSocialInteractions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val followUpCases = repository.getAllFollowUpCases().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bibleStudies = repository.getAllBibleStudies().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val attendanceRecords = repository.getAllAttendanceRecords().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val financialTransactions = repository.getAllFinancialTransactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val projects = repository.getAllProjects().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val equipment = repository.getAllEquipment().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val committeeReports = repository.getAllCommitteeReports().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val committeeTasks = repository.getAllCommitteeTasks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val evangelismOutreaches = repository.getAllEvangelism().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val ministryEvents = repository.getAllMinistryEvents().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val announcements = repository.getAllAnnouncements().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activityLogs = repository.getAllActivityLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun getRequiredPermission(section: MinistryNavSection): Permission? = when (section) {
        MinistryNavSection.DASHBOARD -> null
        MinistryNavSection.MEMBERS -> Permission.VIEW_MEMBERS
        MinistryNavSection.LEVEL_1 -> Permission.ACCESS_LEVEL1_DASHBOARD
        MinistryNavSection.SOCIAL_MEDIA -> Permission.ACCESS_SOCIAL_MEDIA_DASHBOARD
        MinistryNavSection.MEMBER_CARE -> Permission.ACCESS_MEMBER_CARE_DASHBOARD
        MinistryNavSection.BIBLE_STUDY -> Permission.ACCESS_BIBLE_STUDY_DASHBOARD
        MinistryNavSection.ATTENDANCE -> Permission.LOG_ATTENDANCE
        MinistryNavSection.FINANCE -> Permission.ACCESS_FINANCE_DASHBOARD
        MinistryNavSection.PROJECTS, MinistryNavSection.EQUIPMENT -> Permission.ACCESS_PROJECTS_DASHBOARD
        MinistryNavSection.COMMITTEE -> Permission.ACCESS_COMMITTEE_DASHBOARD
        MinistryNavSection.REPORTS -> null // Individual reports filter by user role
        MinistryNavSection.EVANGELISM -> Permission.LOG_EVANGELISM
        MinistryNavSection.EVENTS -> Permission.MANAGE_EVENTS
        MinistryNavSection.ANNOUNCEMENTS -> Permission.VIEW_ANNOUNCEMENTS
        MinistryNavSection.USERS -> Permission.MANAGE_USERS
        MinistryNavSection.ACTIVITY_LOG -> Permission.VIEW_ACTIVITY_LOGS
        MinistryNavSection.SETTINGS -> Permission.SYSTEM_SETTINGS
        MinistryNavSection.MY_PROFILE -> null
        MinistryNavSection.ACCESS_DENIED -> null
        MinistryNavSection.FIRST_LOGIN_PASSWORD -> null
    }

    fun selectSection(section: MinistryNavSection) {
        val user = _currentUser.value
        if (user == null) {
            _selectedSection.value = MinistryNavSection.DASHBOARD
            return
        }

        // If forced to change password, lock to first login screen
        if (user.mustChangePassword && section != MinistryNavSection.FIRST_LOGIN_PASSWORD) {
            _selectedSection.value = MinistryNavSection.FIRST_LOGIN_PASSWORD
            return
        }

        val requiredPerm = getRequiredPermission(section)
        if (requiredPerm != null && !AuthorizationService.isAuthorized(user, requiredPerm)) {
            _attemptedSection.value = section.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
            _selectedSection.value = MinistryNavSection.ACCESS_DENIED
            return
        }
        _selectedSection.value = section
    }

    fun selectMemberDetail(member: MemberEntity?) {
        _selectedMemberDetail.value = member
    }

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    // Authentication
    fun login(identifier: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.authenticate(identifier, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                if (user.mustChangePassword) {
                    _selectedSection.value = MinistryNavSection.FIRST_LOGIN_PASSWORD
                } else {
                    _selectedSection.value = MinistryNavSection.DASHBOARD
                }
                onResult(true, null)
            }.onFailure { error ->
                onResult(false, error.message ?: "Authentication failed")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _selectedMemberDetail.value = null
        _selectedSection.value = MinistryNavSection.DASHBOARD
        showMessage("Logged out successfully.")
    }

    fun changePassword(newPass: String, onComplete: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.changePassword(user, newPass)
            res.onSuccess {
                _currentUser.value = user.copy(mustChangePassword = false)
                _selectedSection.value = MinistryNavSection.DASHBOARD
                showMessage("Password updated successfully.")
                onComplete()
            }.onFailure { err ->
                showMessage("Error: ${err.message}")
            }
        }
    }

    // Member actions
    fun addMember(member: MemberEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = repository.addMember(member, user)
                if (member.ministryLevel == "Level 1") {
                    val topics = listOf("Salvation", "Prayer", "Bible", "Faith", "Holy Spirit", "Christian Character", "Evangelism", "Serving", "Leadership")
                    topics.forEach { topic ->
                        repository.updateDiscipleship(
                            DiscipleshipEntity(memberId = id, memberName = member.fullName, topicName = topic, status = "Not Started"),
                            user
                        )
                    }
                }
                showMessage("Member ${member.fullName} added successfully.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateMember(member: MemberEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateMember(member, user)
                _selectedMemberDetail.value = member
                showMessage("Member ${member.fullName} updated.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun deactivateMember(member: MemberEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.setMemberStatus(member.id, member.fullName, "Deactivated", user)
                _selectedMemberDetail.value = member.copy(memberStatus = "Deactivated")
                showMessage("Member ${member.fullName} deactivated.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun reactivateMember(member: MemberEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.setMemberStatus(member.id, member.fullName, "Active", user)
                _selectedMemberDetail.value = member.copy(memberStatus = "Active")
                showMessage("Member ${member.fullName} reactivated.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Discipleship
    fun updateDiscipleshipStatus(record: DiscipleshipEntity, newStatus: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateDiscipleship(record.copy(status = newStatus), user)
                showMessage("Updated ${record.topicName} to $newStatus")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun recommendProgression(memberId: Long, memberName: String, targetLevel: String, reason: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addRecommendation(
                    ProgressionRecommendationEntity(
                        memberId = memberId,
                        memberName = memberName,
                        recommendedBy = user.displayName,
                        targetLevel = targetLevel,
                        reason = reason,
                        dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    ),
                    user
                )
                showMessage("Recommendation submitted for $memberName")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Social Media
    fun addSocialPost(post: SocialPostEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addSocialPost(post, user)
                showMessage("Gospel post scheduled.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateSocialPostStatus(post: SocialPostEntity, newStatus: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateSocialPost(post.copy(status = newStatus), user)
                showMessage("Post status set to $newStatus.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun addSocialInteraction(type: String, sender: String, platform: String, message: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addSocialInteraction(
                    SocialInteractionEntity(
                        type = type,
                        senderName = sender,
                        platform = platform,
                        message = message,
                        dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    ),
                    user
                )
                showMessage("Social interaction logged.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateSocialInteractionStatus(interaction: SocialInteractionEntity, status: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateSocialInteraction(interaction.copy(status = status), user)
                showMessage("Interaction marked as $status.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Member Care
    fun addFollowUpCase(case: FollowUpCaseEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addFollowUpCase(case, user)
                showMessage("Follow-up case created.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateFollowUpCase(case: FollowUpCaseEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateFollowUpCase(case, user)
                showMessage("Case updated.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Bible Study & Attendance
    fun addBibleStudy(study: BibleStudyEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addBibleStudy(study, user)
                showMessage("Bible study added.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun recordAttendance(record: AttendanceRecordEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addAttendanceRecord(record, user)
                showMessage("Attendance marked for ${record.memberName}.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Finance
    fun addTransaction(tx: FinancialTransactionEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addTransaction(tx, user)
                showMessage("Financial transaction recorded.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Projects & Equipment
    fun addProject(project: ProjectEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addProject(project, user)
                showMessage("Project created.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateProjectProgress(project: ProjectEntity, percent: Int, status: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addProject(project.copy(progressPercent = percent, status = status), user)
                showMessage("Project updated to $percent%.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun addEquipment(item: EquipmentEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addEquipment(item, user)
                showMessage("Equipment item registered.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun checkoutEquipment(item: EquipmentEntity, borrower: String, returnDate: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateEquipment(
                    item.copy(status = "Borrowed", borrowedBy = borrower, returnDate = returnDate),
                    user
                )
                showMessage("${item.name} checked out to $borrower.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun checkinEquipment(item: EquipmentEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateEquipment(
                    item.copy(status = "Available", borrowedBy = "", returnDate = ""),
                    user
                )
                showMessage("${item.name} checked back in.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Committee
    fun submitCommitteeReport(report: CommitteeReportEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addCommitteeReport(report, user)
                showMessage("Committee report submitted successfully.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun addCommitteeTask(task: CommitteeTaskEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addCommitteeTask(task, user)
                showMessage("Task assigned to ${task.assignedToName}.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun toggleTaskCompleted(task: CommitteeTaskEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateTask(task.copy(isCompleted = !task.isCompleted), user)
                val state = if (!task.isCompleted) "Completed" else "Reopened"
                showMessage("Task marked as $state.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Evangelism & Events
    fun addEvangelism(outreach: EvangelismEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addEvangelism(outreach, user)
                showMessage("Evangelism mission logged.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun addMinistryEvent(event: MinistryEventEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addMinistryEvent(event, user)
                showMessage("Ministry event created.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // Announcements
    fun addAnnouncement(announcement: AnnouncementEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addAnnouncement(announcement, user)
                showMessage("Announcement published to ${announcement.targetAudience}.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    // User Management (Exclusively Leader)
    fun createUser(user: UserEntity, rawPass: String) {
        val actor = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val salt = SecurityUtils.generateSalt()
                val hash = SecurityUtils.hashPassword(rawPass, salt)
                repository.createUser(user.copy(passwordHash = hash, salt = salt), actor)
                showMessage("User ${user.displayName} created successfully.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateUserRole(uid: String, newRole: String) {
        val actor = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateUserRole(uid, newRole, actor)
                showMessage("User role changed to $newRole.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun updateUserRole(id: Long, newRole: String) {
        val targetUser = users.value.firstOrNull { it.id == id } ?: return
        updateUserRole(targetUser.uid, newRole)
    }

    fun setUserActive(uid: String, isActive: Boolean) {
        val actor = _currentUser.value ?: return
        val status = if (isActive) "active" else "disabled"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.setUserStatus(uid, status, actor)
                val state = if (isActive) "Reactivated" else "Disabled"
                showMessage("Account $state.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun setUserActive(id: Long, isActive: Boolean) {
        val targetUser = users.value.firstOrNull { it.id == id } ?: return
        setUserActive(targetUser.uid, isActive)
    }

    fun resetUserPassword(uid: String, newRawPass: String) {
        val actor = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.resetPasswordByLeader(uid, newRawPass, actor)
                showMessage("Password reset successfully.")
            } catch (e: SecurityException) {
                showMessage("403 Access Denied: ${e.message}")
            }
        }
    }

    fun resetUserPassword(id: Long, newRawPass: String) {
        val targetUser = users.value.firstOrNull { it.id == id } ?: return
        resetUserPassword(targetUser.uid, newRawPass)
    }
}
