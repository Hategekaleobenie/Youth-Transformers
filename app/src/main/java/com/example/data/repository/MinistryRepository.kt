package com.example.data.repository

import com.example.data.local.YouthTransformersDao
import com.example.data.local.SeedDataProvider
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class MinistryRepository(private val dao: YouthTransformersDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private fun currentDateTime() = dateFormat.format(Date())

    // Initial check: if empty, seed database
    suspend fun checkAndSeedDatabase() {
        val users = dao.getAllUsers().firstOrNull()
        if (users.isNullOrEmpty()) {
            SeedDataProvider.populateDatabase(dao)
        }
    }

    // Authentication
    suspend fun authenticate(identifier: String, rawPassword: String): UserEntity? {
        val user = dao.getUserByEmailOrUsername(identifier.trim()) ?: return null
        if (!user.isActive) return null
        return if (SecurityUtils.verifyPassword(rawPassword, user.passwordHash)) {
            logActivity(user.fullName, "Login Successful", "Authentication", "User logged in to platform")
            user
        } else null
    }

    suspend fun changePassword(userId: Long, newRawPass: String, operatorName: String): Boolean {
        val hash = SecurityUtils.hashPassword(newRawPass)
        dao.updatePassword(userId, hash)
        logActivity(operatorName, "Password Changed", "User Account", "User id: $userId changed password")
        return true
    }

    suspend fun resetPasswordByLeader(userId: Long, newRawPass: String, leaderName: String): Boolean {
        val hash = SecurityUtils.hashPassword(newRawPass)
        dao.updatePassword(userId, hash)
        logActivity(leaderName, "Password Reset by Leader", "User Account", "Admin reset password for user id: $userId")
        return true
    }

    // Users
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun createUser(user: UserEntity, operatorName: String): Long {
        val id = dao.insertUser(user)
        logActivity(operatorName, "User Created", "User Account", "Created account for ${user.fullName} (${user.role})")
        return id
    }

    suspend fun updateUserRole(userId: Long, newRole: String, operatorName: String) {
        dao.updateUserRole(userId, newRole)
        logActivity(operatorName, "Role Changed", "User Account", "User $userId role updated to $newRole")
    }

    suspend fun setUserActive(userId: Long, isActive: Boolean, operatorName: String) {
        dao.setUserActive(userId, isActive)
        val statusText = if (isActive) "Reactivated" else "Deactivated"
        logActivity(operatorName, "Account $statusText", "User Account", "User $userId account status changed to $isActive")
    }

    // Members
    fun getAllMembers(): Flow<List<MemberEntity>> = dao.getAllMembers()
    fun getLevel1Members(): Flow<List<MemberEntity>> = dao.getLevel1Members()

    suspend fun addMember(member: MemberEntity, operatorName: String): Long {
        val id = dao.insertMember(member)
        logActivity(operatorName, "Member Added", "Member", "Added member ${member.fullName}")
        return id
    }

    suspend fun updateMember(member: MemberEntity, operatorName: String) {
        dao.updateMember(member)
        logActivity(operatorName, "Member Updated", "Member", "Updated details for ${member.fullName}")
    }

    suspend fun setMemberStatus(memberId: Long, memberName: String, status: String, operatorName: String) {
        dao.updateMemberStatus(memberId, status)
        logActivity(operatorName, "Member Status Changed", "Member", "Changed $memberName status to $status")
    }

    // Discipleship
    fun getDiscipleshipForMember(memberId: Long): Flow<List<DiscipleshipEntity>> = dao.getDiscipleshipForMember(memberId)
    fun getAllDiscipleship(): Flow<List<DiscipleshipEntity>> = dao.getAllDiscipleship()

    suspend fun updateDiscipleship(record: DiscipleshipEntity, operatorName: String) {
        dao.updateDiscipleship(record)
        logActivity(operatorName, "Curriculum Progress Updated", "Discipleship", "${record.memberName} - ${record.topicName} marked as ${record.status}")
    }

    suspend fun addRecommendation(rec: ProgressionRecommendationEntity, operatorName: String) {
        dao.insertRecommendation(rec)
        logActivity(operatorName, "Progression Recommended", "Discipleship", "Recommended ${rec.memberName} for ${rec.targetLevel}")
    }

    fun getAllRecommendations(): Flow<List<ProgressionRecommendationEntity>> = dao.getAllRecommendations()

    // Social Media
    fun getAllSocialPosts(): Flow<List<SocialPostEntity>> = dao.getAllSocialPosts()
    fun getAllSocialInteractions(): Flow<List<SocialInteractionEntity>> = dao.getAllSocialInteractions()

    suspend fun addSocialPost(post: SocialPostEntity, operatorName: String) {
        dao.insertSocialPost(post)
        logActivity(operatorName, "Social Post Created", "Social Media", "Post: ${post.title} on ${post.platform}")
    }

    suspend fun updateSocialPost(post: SocialPostEntity, operatorName: String) {
        dao.updateSocialPost(post)
        logActivity(operatorName, "Social Post Updated", "Social Media", "Post: ${post.title} status ${post.status}")
    }

    suspend fun addSocialInteraction(interaction: SocialInteractionEntity, operatorName: String) {
        dao.insertSocialInteraction(interaction)
        logActivity(operatorName, "Interaction Logged", "Social Interaction", "${interaction.type} from ${interaction.senderName}")
    }

    suspend fun updateSocialInteraction(interaction: SocialInteractionEntity) {
        dao.updateSocialInteraction(interaction)
    }

    // Member Care & Follow-ups
    fun getAllFollowUpCases(): Flow<List<FollowUpCaseEntity>> = dao.getAllFollowUpCases()
    fun getFollowUpsForMember(memberId: Long): Flow<List<FollowUpCaseEntity>> = dao.getFollowUpsForMember(memberId)

    suspend fun addFollowUpCase(case: FollowUpCaseEntity, operatorName: String) {
        dao.insertFollowUpCase(case)
        logActivity(operatorName, "Follow-up Case Opened", "Member Care", "Case for ${case.memberName} (${case.reason})")
    }

    suspend fun updateFollowUpCase(case: FollowUpCaseEntity, operatorName: String) {
        dao.updateFollowUpCase(case)
        logActivity(operatorName, "Follow-up Case Updated", "Member Care", "Case ${case.id} (${case.memberName}) status: ${case.status}")
    }

    // Bible Studies
    fun getAllBibleStudies(): Flow<List<BibleStudyEntity>> = dao.getAllBibleStudies()

    suspend fun addBibleStudy(study: BibleStudyEntity, operatorName: String) {
        dao.insertBibleStudy(study)
        logActivity(operatorName, "Bible Study Scheduled", "Bible Study", "Topic: ${study.topic} by ${study.preacher}")
    }

    // Attendance
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>> = dao.getAllAttendanceRecords()
    fun getAttendanceForMember(memberId: Long): Flow<List<AttendanceRecordEntity>> = dao.getAttendanceForMember(memberId)

    suspend fun recordAttendance(record: AttendanceRecordEntity, operatorName: String) {
        dao.insertAttendanceRecord(record)
        logActivity(operatorName, "Attendance Recorded", "Attendance", "${record.memberName} marked as ${record.status} in ${record.activityTitle}")
    }

    // Finance
    fun getAllFinancialTransactions(): Flow<List<FinancialTransactionEntity>> = dao.getAllFinancialTransactions()

    suspend fun addTransaction(tx: FinancialTransactionEntity, operatorName: String) {
        dao.insertFinancialTransaction(tx)
        logActivity(operatorName, "Financial Transaction Created", "Finance", "${tx.type} ${tx.amount} RWF - ${tx.description}")
    }

    // Projects & Equipment
    fun getAllProjects(): Flow<List<ProjectEntity>> = dao.getAllProjects()
    fun getAllEquipment(): Flow<List<EquipmentEntity>> = dao.getAllEquipment()

    suspend fun addProject(project: ProjectEntity, operatorName: String) {
        dao.insertProject(project)
        logActivity(operatorName, "Project Created", "Project", project.name)
    }

    suspend fun updateProject(project: ProjectEntity, operatorName: String) {
        dao.updateProject(project)
        logActivity(operatorName, "Project Updated", "Project", "${project.name} (${project.progressPercent}%)")
    }

    suspend fun addEquipment(item: EquipmentEntity, operatorName: String) {
        dao.insertEquipment(item)
        logActivity(operatorName, "Equipment Added", "Equipment", "${item.name} (${item.category})")
    }

    suspend fun updateEquipment(item: EquipmentEntity, operatorName: String) {
        dao.updateEquipment(item)
        logActivity(operatorName, "Equipment Updated", "Equipment", "${item.name} status: ${item.status}")
    }

    // Committee Reports & Tasks
    fun getAllCommitteeReports(): Flow<List<CommitteeReportEntity>> = dao.getAllCommitteeReports()
    fun getAllCommitteeTasks(): Flow<List<CommitteeTaskEntity>> = dao.getAllCommitteeTasks()

    suspend fun submitReport(report: CommitteeReportEntity) {
        dao.insertCommitteeReport(report)
        logActivity(report.authorName, "Report Submitted", "Committee", "${report.reportType} submitted by ${report.authorName}")
    }

    suspend fun addTask(task: CommitteeTaskEntity, operatorName: String) {
        dao.insertCommitteeTask(task)
        logActivity(operatorName, "Committee Task Assigned", "Committee", "${task.title} to ${task.assignedToName}")
    }

    suspend fun updateTask(task: CommitteeTaskEntity, operatorName: String) {
        dao.updateCommitteeTask(task)
        val statusStr = if (task.isCompleted) "Completed" else "In Progress"
        logActivity(operatorName, "Task Status Updated", "Committee", "${task.title} marked as $statusStr")
    }

    // Evangelism & Events
    fun getAllEvangelism(): Flow<List<EvangelismEntity>> = dao.getAllEvangelism()
    fun getAllMinistryEvents(): Flow<List<MinistryEventEntity>> = dao.getAllMinistryEvents()

    suspend fun addEvangelism(outreach: EvangelismEntity, operatorName: String) {
        dao.insertEvangelism(outreach)
        logActivity(operatorName, "Evangelism Outreach Logged", "Evangelism", outreach.title)
    }

    suspend fun addMinistryEvent(event: MinistryEventEntity, operatorName: String) {
        dao.insertMinistryEvent(event)
        logActivity(operatorName, "Event Created", "Events", event.name)
    }

    suspend fun updateMinistryEvent(event: MinistryEventEntity, operatorName: String) {
        dao.updateMinistryEvent(event)
        logActivity(operatorName, "Event Updated", "Events", "${event.name} (${event.status})")
    }

    // Announcements
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()

    suspend fun addAnnouncement(announcement: AnnouncementEntity, operatorName: String) {
        dao.insertAnnouncement(announcement)
        logActivity(operatorName, "Announcement Published", "Announcements", announcement.title)
    }

    // Activity Logs
    fun getAllActivityLogs(): Flow<List<ActivityLogEntity>> = dao.getAllActivityLogs()

    private suspend fun logActivity(user: String, action: String, objectType: String, details: String) {
        dao.insertActivityLog(
            ActivityLogEntity(
                userName = user,
                action = action,
                objectType = objectType,
                dateStr = currentDateTime(),
                details = details
            )
        )
    }
}
