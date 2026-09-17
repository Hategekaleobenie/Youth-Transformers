package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.MinistryRepository
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
    SETTINGS("nav_settings")
}

class MinistryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MinistryRepository

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _selectedSection = MutableStateFlow(MinistryNavSection.DASHBOARD)
    val selectedSection: StateFlow<MinistryNavSection> = _selectedSection.asStateFlow()

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

    fun selectSection(section: MinistryNavSection) {
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

    // Auth
    fun login(identifier: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.authenticate(identifier, pass)
            if (user != null) {
                _currentUser.value = user
                _selectedSection.value = MinistryNavSection.DASHBOARD
                onResult(true, null)
            } else {
                onResult(false, "Invalid credentials or deactivated account.")
            }
        }
    }

    fun quickSwitchUser(user: UserEntity) {
        _currentUser.value = user
        _selectedSection.value = MinistryNavSection.DASHBOARD
        showMessage("Logged in as ${user.fullName} (${user.role})")
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
            repository.changePassword(user.id, newPass, user.fullName)
            _currentUser.value = user.copy(mustChangePassword = false)
            showMessage("Password updated successfully.")
            onComplete()
        }
    }

    // Member actions
    fun addMember(member: MemberEntity) {
        val operator = _currentUser.value?.fullName ?: "Administrator"
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.addMember(member, operator)
            // If Level 1, seed curriculum
            if (member.ministryLevel == "Level 1") {
                val topics = listOf("Salvation", "Prayer", "Bible", "Faith", "Holy Spirit", "Christian Character", "Evangelism", "Serving", "Leadership")
                topics.forEach { topic ->
                    repository.updateDiscipleship(
                        DiscipleshipEntity(memberId = id, memberName = member.fullName, topicName = topic, status = "Not Started"),
                        operator
                    )
                }
            }
            showMessage("Member ${member.fullName} added successfully.")
        }
    }

    fun updateMember(member: MemberEntity) {
        val operator = _currentUser.value?.fullName ?: "Administrator"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMember(member, operator)
            _selectedMemberDetail.value = member
            showMessage("Member ${member.fullName} updated.")
        }
    }

    fun deactivateMember(member: MemberEntity) {
        val operator = _currentUser.value?.fullName ?: "Administrator"
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMemberStatus(member.id, member.fullName, "Deactivated", operator)
            _selectedMemberDetail.value = member.copy(memberStatus = "Deactivated")
            showMessage("Member ${member.fullName} deactivated.")
        }
    }

    fun reactivateMember(member: MemberEntity) {
        val operator = _currentUser.value?.fullName ?: "Administrator"
        viewModelScope.launch(Dispatchers.IO) {
            repository.setMemberStatus(member.id, member.fullName, "Active", operator)
            _selectedMemberDetail.value = member.copy(memberStatus = "Active")
            showMessage("Member ${member.fullName} reactivated.")
        }
    }

    // Discipleship
    fun updateDiscipleshipStatus(record: DiscipleshipEntity, newStatus: String) {
        val operator = _currentUser.value?.fullName ?: "Level 1 Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDiscipleship(record.copy(status = newStatus), operator)
            showMessage("Updated ${record.topicName} to $newStatus")
        }
    }

    fun recommendProgression(memberId: Long, memberName: String, targetLevel: String, reason: String) {
        val operator = _currentUser.value?.fullName ?: "Level 1 Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecommendation(
                ProgressionRecommendationEntity(
                    memberId = memberId,
                    memberName = memberName,
                    recommendedBy = operator,
                    targetLevel = targetLevel,
                    reason = reason,
                    dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                ),
                operator
            )
            showMessage("Recommendation submitted for $memberName")
        }
    }

    // Social Media
    fun addSocialPost(post: SocialPostEntity) {
        val operator = _currentUser.value?.fullName ?: "Social Media Lead"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSocialPost(post, operator)
            showMessage("Gospel post scheduled.")
        }
    }

    fun updateSocialPostStatus(post: SocialPostEntity, newStatus: String) {
        val operator = _currentUser.value?.fullName ?: "Social Media Lead"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSocialPost(post.copy(status = newStatus), operator)
            showMessage("Post status set to $newStatus.")
        }
    }

    fun addSocialInteraction(type: String, sender: String, platform: String, message: String) {
        val operator = _currentUser.value?.fullName ?: "Social Media Lead"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSocialInteraction(
                SocialInteractionEntity(
                    type = type,
                    senderName = sender,
                    platform = platform,
                    message = message,
                    dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                ),
                operator
            )
            showMessage("Social interaction logged.")
        }
    }

    fun updateSocialInteractionStatus(interaction: SocialInteractionEntity, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSocialInteraction(interaction.copy(status = status))
            showMessage("Interaction marked as $status.")
        }
    }

    // Member Care
    fun addFollowUpCase(case: FollowUpCaseEntity) {
        val operator = _currentUser.value?.fullName ?: "Member Care"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFollowUpCase(case, operator)
            showMessage("Follow-up case created.")
        }
    }

    fun updateFollowUpCase(case: FollowUpCaseEntity) {
        val operator = _currentUser.value?.fullName ?: "Member Care"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFollowUpCase(case, operator)
            showMessage("Case updated.")
        }
    }

    // Bible Study & Attendance
    fun addBibleStudy(study: BibleStudyEntity) {
        val operator = _currentUser.value?.fullName ?: "Bible Study Lead"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBibleStudy(study, operator)
            showMessage("Bible study added.")
        }
    }

    fun recordAttendance(record: AttendanceRecordEntity) {
        val operator = _currentUser.value?.fullName ?: "Secretary"
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordAttendance(record, operator)
            showMessage("Attendance marked for ${record.memberName}.")
        }
    }

    // Finance
    fun addTransaction(tx: FinancialTransactionEntity) {
        val operator = _currentUser.value?.fullName ?: "Accountant"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(tx, operator)
            showMessage("Financial transaction recorded.")
        }
    }

    // Projects & Equipment
    fun addProject(project: ProjectEntity) {
        val operator = _currentUser.value?.fullName ?: "Projects Manager"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addProject(project, operator)
            showMessage("Project created.")
        }
    }

    fun updateProjectProgress(project: ProjectEntity, percent: Int, status: String) {
        val operator = _currentUser.value?.fullName ?: "Projects Manager"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProject(project.copy(progressPercent = percent, status = status), operator)
            showMessage("Project updated to $percent%.")
        }
    }

    fun addEquipment(item: EquipmentEntity) {
        val operator = _currentUser.value?.fullName ?: "Equipment Manager"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEquipment(item, operator)
            showMessage("Equipment item registered.")
        }
    }

    fun checkoutEquipment(item: EquipmentEntity, borrower: String, returnDate: String) {
        val operator = _currentUser.value?.fullName ?: "Equipment Manager"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEquipment(
                item.copy(status = "Borrowed", borrowedBy = borrower, returnDate = returnDate),
                operator
            )
            showMessage("${item.name} checked out to $borrower.")
        }
    }

    fun checkinEquipment(item: EquipmentEntity) {
        val operator = _currentUser.value?.fullName ?: "Equipment Manager"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEquipment(
                item.copy(status = "Available", borrowedBy = "", returnDate = ""),
                operator
            )
            showMessage("${item.name} checked back in.")
        }
    }

    // Committee
    fun submitCommitteeReport(report: CommitteeReportEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitReport(report)
            showMessage("Committee report submitted successfully.")
        }
    }

    fun addCommitteeTask(task: CommitteeTaskEntity) {
        val operator = _currentUser.value?.fullName ?: "Committee Coordinator"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task, operator)
            showMessage("Task assigned to ${task.assignedToName}.")
        }
    }

    fun toggleTaskCompleted(task: CommitteeTaskEntity) {
        val operator = _currentUser.value?.fullName ?: "Committee Coordinator"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted), operator)
            val state = if (!task.isCompleted) "Completed" else "Reopened"
            showMessage("Task marked as $state.")
        }
    }

    // Evangelism & Events
    fun addEvangelism(outreach: EvangelismEntity) {
        val operator = _currentUser.value?.fullName ?: "Evangelism Lead"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEvangelism(outreach, operator)
            showMessage("Evangelism mission logged.")
        }
    }

    fun addMinistryEvent(event: MinistryEventEntity) {
        val operator = _currentUser.value?.fullName ?: "Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMinistryEvent(event, operator)
            showMessage("Ministry event created.")
        }
    }

    // Announcements
    fun addAnnouncement(announcement: AnnouncementEntity) {
        val operator = _currentUser.value?.fullName ?: "Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAnnouncement(announcement, operator)
            showMessage("Announcement published to ${announcement.targetAudience}.")
        }
    }

    // User Management & Security
    fun createUser(user: UserEntity, rawPass: String) {
        val operator = _currentUser.value?.fullName ?: "Ministry Leader"
        val hash = SecurityUtils.hashPassword(rawPass)
        viewModelScope.launch(Dispatchers.IO) {
            repository.createUser(user.copy(passwordHash = hash), operator)
            showMessage("User ${user.fullName} created.")
        }
    }

    fun updateUserRole(userId: Long, newRole: String) {
        val operator = _currentUser.value?.fullName ?: "Ministry Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserRole(userId, newRole, operator)
            showMessage("User role changed to $newRole.")
        }
    }

    fun setUserActive(userId: Long, isActive: Boolean) {
        val operator = _currentUser.value?.fullName ?: "Ministry Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.setUserActive(userId, isActive, operator)
            val state = if (isActive) "Reactivated" else "Disabled"
            showMessage("Account $state.")
        }
    }

    fun resetUserPassword(userId: Long, newRawPass: String) {
        val operator = _currentUser.value?.fullName ?: "Ministry Leader"
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetPasswordByLeader(userId, newRawPass, operator)
            showMessage("Password reset successfully.")
        }
    }
}
