package com.example.data.repository

import com.example.data.local.SeedDataProvider
import com.example.data.local.YouthTransformersDao
import com.example.data.model.*
import com.example.security.AuthorizationService
import com.example.security.Permission
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

    // Security check helper
    private suspend fun authorizeOrThrow(actor: UserEntity, permission: Permission, resource: String) {
        try {
            AuthorizationService.assertAuthorized(actor, permission, resource)
        } catch (e: SecurityException) {
            logActivity(
                actorUid = actor.uid,
                actorName = actor.displayName,
                action = "ACCESS_DENIED",
                resourceType = resource,
                details = e.message ?: "Access denied",
                result = "ACCESS_DENIED"
            )
            throw e
        }
    }

    // Authentication
    suspend fun authenticate(identifier: String, rawPassword: String): Result<UserEntity> {
        val trimmed = identifier.trim().lowercase()
        val user = dao.getUserByEmail(trimmed)
            ?: dao.getUserByEmailOrUid(identifier.trim())
            ?: return Result.failure(IllegalArgumentException("Invalid email or password"))

        if (user.status != "active") {
            logActivity(
                actorUid = user.uid,
                actorName = user.displayName,
                action = "LOGIN_FAILED",
                resourceType = "SESSION",
                details = "Account is disabled",
                result = "ACCESS_DENIED"
            )
            return Result.failure(IllegalStateException("Account is disabled. Please contact the Ministry Leader."))
        }

        if (!SecurityUtils.verifyPassword(rawPassword, user.passwordHash, user.salt)) {
            logActivity(
                actorUid = user.uid,
                actorName = user.displayName,
                action = "LOGIN_FAILED",
                resourceType = "SESSION",
                details = "Invalid password credentials attempt",
                result = "ACCESS_DENIED"
            )
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        dao.updateLastLogin(user.uid, System.currentTimeMillis())
        logActivity(
            actorUid = user.uid,
            actorName = user.displayName,
            action = "LOGIN_SUCCESS",
            resourceType = "SESSION",
            details = "User logged in with role: ${user.role}",
            result = "SUCCESS"
        )
        return Result.success(user)
    }

    suspend fun getUserByUid(uid: String): UserEntity? = dao.getUserByUid(uid)

    suspend fun changePassword(actor: UserEntity, newRawPass: String): Result<Boolean> {
        if (newRawPass.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newRawPass, newSalt)
        dao.updatePassword(actor.uid, newHash, newSalt)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "PASSWORD_CHANGED",
            resourceType = "USER_ACCOUNT",
            details = "User successfully updated password",
            result = "SUCCESS"
        )
        return Result.success(true)
    }

    suspend fun resetPasswordByLeader(targetUid: String, newRawPass: String, actor: UserEntity): Result<Boolean> {
        authorizeOrThrow(actor, Permission.MANAGE_USERS, "users")
        val targetUser = dao.getUserByUid(targetUid) ?: return Result.failure(IllegalArgumentException("User not found"))
        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newRawPass, newSalt)
        dao.updatePassword(targetUid, newHash, newSalt)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "PASSWORD_RESET_BY_LEADER",
            resourceType = "USER_ACCOUNT",
            resourceId = targetUid,
            details = "Leader reset password for ${targetUser.displayName}",
            result = "SUCCESS"
        )
        return Result.success(true)
    }

    // Users Management (Leader Only)
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun createUser(user: UserEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_USERS, "users")
        dao.insertUser(user)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "USER_CREATED",
            resourceType = "USER_ACCOUNT",
            resourceId = user.uid,
            details = "Created account for ${user.displayName} (${user.role})",
            result = "SUCCESS"
        )
    }

    suspend fun updateUserRole(targetUid: String, newRole: String, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_USERS, "users")
        dao.updateUserRole(targetUid, newRole)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "ROLE_CHANGED",
            resourceType = "USER_ACCOUNT",
            resourceId = targetUid,
            details = "Updated user $targetUid role to $newRole",
            result = "SUCCESS"
        )
    }

    suspend fun setUserStatus(targetUid: String, status: String, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_USERS, "users")
        dao.setUserStatus(targetUid, status)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "ACCOUNT_STATUS_CHANGED",
            resourceType = "USER_ACCOUNT",
            resourceId = targetUid,
            details = "User $targetUid status changed to $status",
            result = "SUCCESS"
        )
    }

    // Members
    fun getAllMembers(): Flow<List<MemberEntity>> = dao.getAllMembers()
    fun getLevel1Members(): Flow<List<MemberEntity>> = dao.getLevel1Members()

    suspend fun addMember(member: MemberEntity, actor: UserEntity): Long {
        authorizeOrThrow(actor, Permission.ADD_EDIT_MEMBERS, "members")
        val id = dao.insertMember(member)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "MEMBER_ADDED",
            resourceType = "MEMBER",
            resourceId = id.toString(),
            details = "Added member ${member.fullName}",
            result = "SUCCESS"
        )
        return id
    }

    suspend fun updateMember(member: MemberEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.ADD_EDIT_MEMBERS, "members")
        dao.updateMember(member)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "MEMBER_UPDATED",
            resourceType = "MEMBER",
            resourceId = member.id.toString(),
            details = "Updated details for ${member.fullName}",
            result = "SUCCESS"
        )
    }

    suspend fun setMemberStatus(memberId: Long, memberName: String, status: String, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.DEACTIVATE_MEMBERS, "members")
        dao.updateMemberStatus(memberId, status)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "MEMBER_STATUS_CHANGED",
            resourceType = "MEMBER",
            resourceId = memberId.toString(),
            details = "Changed $memberName status to $status",
            result = "SUCCESS"
        )
    }

    // Discipleship
    fun getDiscipleshipForMember(memberId: Long): Flow<List<DiscipleshipEntity>> = dao.getDiscipleshipForMember(memberId)
    fun getAllDiscipleship(): Flow<List<DiscipleshipEntity>> = dao.getAllDiscipleship()

    suspend fun updateDiscipleship(record: DiscipleshipEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_LEVEL1_CURRICULUM, "discipleship")
        dao.updateDiscipleship(record)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "DISCIPLESHIP_PROGRESS_UPDATED",
            resourceType = "DISCIPLESHIP",
            details = "${record.memberName} - ${record.topicName} marked as ${record.status}",
            result = "SUCCESS"
        )
    }

    suspend fun addRecommendation(rec: ProgressionRecommendationEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.RECOMMEND_PROGRESSION, "discipleship")
        dao.insertRecommendation(rec)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "PROGRESSION_RECOMMENDED",
            resourceType = "DISCIPLESHIP",
            details = "Recommended ${rec.memberName} for ${rec.targetLevel}",
            result = "SUCCESS"
        )
    }

    fun getAllRecommendations(): Flow<List<ProgressionRecommendationEntity>> = dao.getAllRecommendations()

    // Social Media
    fun getAllSocialPosts(): Flow<List<SocialPostEntity>> = dao.getAllSocialPosts()
    fun getAllSocialInteractions(): Flow<List<SocialInteractionEntity>> = dao.getAllSocialInteractions()

    suspend fun addSocialPost(post: SocialPostEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_CONTENT_CALENDAR, "social_media")
        dao.insertSocialPost(post)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "SOCIAL_POST_CREATED",
            resourceType = "SOCIAL_MEDIA",
            details = "Post: ${post.title} on ${post.platform}",
            result = "SUCCESS"
        )
    }

    suspend fun updateSocialPost(post: SocialPostEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_CONTENT_CALENDAR, "social_media")
        dao.updateSocialPost(post)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "SOCIAL_POST_UPDATED",
            resourceType = "SOCIAL_MEDIA",
            details = "Post: ${post.title} status ${post.status}",
            result = "SUCCESS"
        )
    }

    suspend fun addSocialInteraction(interaction: SocialInteractionEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.TRIAGE_SOCIAL_INTERACTIONS, "social_media")
        dao.insertSocialInteraction(interaction)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "INTERACTION_LOGGED",
            resourceType = "SOCIAL_INTERACTION",
            details = "${interaction.type} from ${interaction.senderName}",
            result = "SUCCESS"
        )
    }

    suspend fun updateSocialInteraction(interaction: SocialInteractionEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.TRIAGE_SOCIAL_INTERACTIONS, "social_media")
        dao.updateSocialInteraction(interaction)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "INTERACTION_UPDATED",
            resourceType = "SOCIAL_INTERACTION",
            details = "Interaction from ${interaction.senderName} marked as ${interaction.status}",
            result = "SUCCESS"
        )
    }

    // Member Care & Follow-ups
    fun getAllFollowUpCases(): Flow<List<FollowUpCaseEntity>> = dao.getAllFollowUpCases()

    suspend fun addFollowUpCase(case: FollowUpCaseEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_FOLLOW_UPS, "member_care")
        dao.insertFollowUpCase(case)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "FOLLOW_UP_CASE_OPENED",
            resourceType = "MEMBER_CARE",
            details = "Opened case for ${case.memberName} (${case.priority})",
            result = "SUCCESS"
        )
    }

    suspend fun updateFollowUpCase(case: FollowUpCaseEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_FOLLOW_UPS, "member_care")
        dao.updateFollowUpCase(case)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "FOLLOW_UP_CASE_UPDATED",
            resourceType = "MEMBER_CARE",
            details = "Updated case for ${case.memberName} status: ${case.status}",
            result = "SUCCESS"
        )
    }

    // Bible Studies & Attendance
    fun getAllBibleStudies(): Flow<List<BibleStudyEntity>> = dao.getAllBibleStudies()
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>> = dao.getAllAttendanceRecords()

    suspend fun addBibleStudy(study: BibleStudyEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_BIBLE_STUDIES, "bible_study")
        dao.insertBibleStudy(study)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "BIBLE_STUDY_SCHEDULED",
            resourceType = "BIBLE_STUDY",
            details = "Study: ${study.topic} on ${study.dateStr}",
            result = "SUCCESS"
        )
    }

    suspend fun addAttendanceRecord(record: AttendanceRecordEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.LOG_ATTENDANCE, "attendance")
        dao.insertAttendanceRecord(record)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "ATTENDANCE_LOGGED",
            resourceType = "ATTENDANCE",
            details = "Attendance logged for ${record.activityTitle} on ${record.dateStr}",
            result = "SUCCESS"
        )
    }

    // Finance (Restricted to Accountant & Leader)
    fun getAllFinancialTransactions(): Flow<List<FinancialTransactionEntity>> = dao.getAllFinancialTransactions()

    suspend fun addTransaction(tx: FinancialTransactionEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_TRANSACTIONS, "finance")
        dao.insertFinancialTransaction(tx)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "TRANSACTION_LOGGED",
            resourceType = "FINANCE",
            details = "${tx.type}: ${tx.amount} RWF - ${tx.description}",
            result = "SUCCESS"
        )
    }

    // Projects & Equipment
    fun getAllProjects(): Flow<List<ProjectEntity>> = dao.getAllProjects()
    fun getAllEquipment(): Flow<List<EquipmentEntity>> = dao.getAllEquipment()

    suspend fun addProject(project: ProjectEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_PROJECTS, "projects")
        dao.insertProject(project)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "PROJECT_CREATED",
            resourceType = "PROJECTS",
            details = "Project: ${project.name}",
            result = "SUCCESS"
        )
    }

    suspend fun addEquipment(equipment: EquipmentEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_EQUIPMENT, "equipment")
        dao.insertEquipment(equipment)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "EQUIPMENT_ADDED",
            resourceType = "EQUIPMENT",
            details = "Item: ${equipment.name} (${equipment.category})",
            result = "SUCCESS"
        )
    }

    suspend fun updateEquipment(equipment: EquipmentEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_EQUIPMENT, "equipment")
        dao.updateEquipment(equipment)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "EQUIPMENT_UPDATED",
            resourceType = "EQUIPMENT",
            details = "Item: ${equipment.name} status: ${equipment.status}",
            result = "SUCCESS"
        )
    }

    // Committee Tasks & Reports
    fun getAllCommitteeTasks(): Flow<List<CommitteeTaskEntity>> = dao.getAllCommitteeTasks()
    fun getAllCommitteeReports(): Flow<List<CommitteeReportEntity>> = dao.getAllCommitteeReports()

    suspend fun addCommitteeReport(report: CommitteeReportEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.SUBMIT_COMMITTEE_REPORTS, "committee")
        dao.insertCommitteeReport(report)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "COMMITTEE_REPORT_SUBMITTED",
            resourceType = "COMMITTEE",
            details = "Report: ${report.reportType} by ${report.authorName}",
            result = "SUCCESS"
        )
    }

    suspend fun addCommitteeTask(task: CommitteeTaskEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_COMMITTEE_TASKS, "committee")
        dao.insertCommitteeTask(task)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "COMMITTEE_TASK_ASSIGNED",
            resourceType = "COMMITTEE",
            details = "${task.title} to ${task.assignedToName}",
            result = "SUCCESS"
        )
    }

    suspend fun updateTask(task: CommitteeTaskEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_COMMITTEE_TASKS, "committee")
        dao.updateCommitteeTask(task)
        val statusStr = if (task.isCompleted) "Completed" else "In Progress"
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "TASK_STATUS_UPDATED",
            resourceType = "COMMITTEE",
            details = "${task.title} marked as $statusStr",
            result = "SUCCESS"
        )
    }

    // Evangelism & Events
    fun getAllEvangelism(): Flow<List<EvangelismEntity>> = dao.getAllEvangelism()
    fun getAllMinistryEvents(): Flow<List<MinistryEventEntity>> = dao.getAllMinistryEvents()

    suspend fun addEvangelism(outreach: EvangelismEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.LOG_EVANGELISM, "evangelism")
        dao.insertEvangelism(outreach)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "EVANGELISM_OUTREACH_LOGGED",
            resourceType = "EVANGELISM",
            details = outreach.title,
            result = "SUCCESS"
        )
    }

    suspend fun addMinistryEvent(event: MinistryEventEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_EVENTS, "events")
        dao.insertMinistryEvent(event)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "EVENT_CREATED",
            resourceType = "EVENTS",
            details = event.name,
            result = "SUCCESS"
        )
    }

    suspend fun updateMinistryEvent(event: MinistryEventEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.MANAGE_EVENTS, "events")
        dao.updateMinistryEvent(event)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "EVENT_UPDATED",
            resourceType = "EVENTS",
            details = "${event.name} (${event.status})",
            result = "SUCCESS"
        )
    }

    // Announcements
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()

    suspend fun addAnnouncement(announcement: AnnouncementEntity, actor: UserEntity) {
        authorizeOrThrow(actor, Permission.POST_ANNOUNCEMENTS, "announcements")
        dao.insertAnnouncement(announcement)
        logActivity(
            actorUid = actor.uid,
            actorName = actor.displayName,
            action = "ANNOUNCEMENT_PUBLISHED",
            resourceType = "ANNOUNCEMENTS",
            details = announcement.title,
            result = "SUCCESS"
        )
    }

    // Activity Logs (Exclusively Leader)
    fun getAllActivityLogs(): Flow<List<ActivityLogEntity>> = dao.getAllActivityLogs()

    private suspend fun logActivity(
        actorUid: String,
        actorName: String,
        action: String,
        resourceType: String,
        resourceId: String = "",
        details: String = "",
        result: String = "SUCCESS"
    ) {
        dao.insertActivityLog(
            ActivityLogEntity(
                actorUid = actorUid,
                actorName = actorName,
                action = action,
                resourceType = resourceType,
                resourceId = resourceId,
                details = details,
                timestamp = System.currentTimeMillis(),
                result = result
            )
        )
    }
}
