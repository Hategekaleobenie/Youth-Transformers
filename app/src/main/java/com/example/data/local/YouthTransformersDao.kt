package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface YouthTransformersDao {

    // Users
    @Query("SELECT * FROM users ORDER BY displayName ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE (email = :identifier OR uid = :identifier) LIMIT 1")
    suspend fun getUserByEmailOrUid(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET passwordHash = :newHash, salt = :salt, mustChangePassword = 0, updatedAt = :updatedAt WHERE uid = :uid")
    suspend fun updatePassword(uid: String, newHash: String, salt: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET status = :status, updatedAt = :updatedAt WHERE uid = :uid")
    suspend fun setUserStatus(uid: String, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET role = :role, updatedAt = :updatedAt WHERE uid = :uid")
    suspend fun updateUserRole(uid: String, role: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE uid = :uid")
    suspend fun updateLastLogin(uid: String, timestamp: Long)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserByUid(uid: String)

    // Members
    @Query("SELECT * FROM members ORDER BY id DESC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: Long): MemberEntity?

    @Query("SELECT * FROM members WHERE ministryLevel = 'Level 1' AND memberStatus != 'Deactivated' ORDER BY fullName ASC")
    fun getLevel1Members(): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Query("UPDATE members SET memberStatus = :status WHERE id = :id")
    suspend fun updateMemberStatus(id: Long, status: String)

    // Discipleship
    @Query("SELECT * FROM discipleship_records WHERE memberId = :memberId ORDER BY id ASC")
    fun getDiscipleshipForMember(memberId: Long): Flow<List<DiscipleshipEntity>>

    @Query("SELECT * FROM discipleship_records ORDER BY id ASC")
    fun getAllDiscipleship(): Flow<List<DiscipleshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscipleship(record: DiscipleshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscipleshipList(records: List<DiscipleshipEntity>)

    @Update
    suspend fun updateDiscipleship(record: DiscipleshipEntity)

    // Progression Recommendations
    @Query("SELECT * FROM progression_recommendations ORDER BY id DESC")
    fun getAllRecommendations(): Flow<List<ProgressionRecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(rec: ProgressionRecommendationEntity)

    @Update
    suspend fun updateRecommendation(rec: ProgressionRecommendationEntity)

    // Social Media Posts
    @Query("SELECT * FROM social_posts ORDER BY scheduledDate DESC")
    fun getAllSocialPosts(): Flow<List<SocialPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPost(post: SocialPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialPosts(posts: List<SocialPostEntity>)

    @Update
    suspend fun updateSocialPost(post: SocialPostEntity)

    // Social Interactions
    @Query("SELECT * FROM social_interactions ORDER BY id DESC")
    fun getAllSocialInteractions(): Flow<List<SocialInteractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialInteraction(interaction: SocialInteractionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialInteractions(interactions: List<SocialInteractionEntity>)

    @Update
    suspend fun updateSocialInteraction(interaction: SocialInteractionEntity)

    // Follow-up Cases
    @Query("SELECT * FROM follow_up_cases ORDER BY id DESC")
    fun getAllFollowUpCases(): Flow<List<FollowUpCaseEntity>>

    @Query("SELECT * FROM follow_up_cases WHERE memberId = :memberId")
    fun getFollowUpsForMember(memberId: Long): Flow<List<FollowUpCaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowUpCase(case: FollowUpCaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowUpCases(cases: List<FollowUpCaseEntity>)

    @Update
    suspend fun updateFollowUpCase(case: FollowUpCaseEntity)

    // Bible Studies
    @Query("SELECT * FROM bible_studies ORDER BY dateStr DESC")
    fun getAllBibleStudies(): Flow<List<BibleStudyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBibleStudy(study: BibleStudyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBibleStudies(studies: List<BibleStudyEntity>)

    @Update
    suspend fun updateBibleStudy(study: BibleStudyEntity)

    // Attendance Records
    @Query("SELECT * FROM attendance_records ORDER BY dateStr DESC")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE memberId = :memberId")
    fun getAttendanceForMember(memberId: Long): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecordEntity>)

    // Financial Transactions
    @Query("SELECT * FROM financial_transactions ORDER BY dateStr DESC")
    fun getAllFinancialTransactions(): Flow<List<FinancialTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialTransaction(tx: FinancialTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialTransactions(txs: List<FinancialTransactionEntity>)

    // Projects
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    // Equipment
    @Query("SELECT * FROM equipment_items ORDER BY name ASC")
    fun getAllEquipment(): Flow<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(item: EquipmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentList(items: List<EquipmentEntity>)

    @Update
    suspend fun updateEquipment(item: EquipmentEntity)

    // Committee Reports
    @Query("SELECT * FROM committee_reports ORDER BY id DESC")
    fun getAllCommitteeReports(): Flow<List<CommitteeReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommitteeReport(report: CommitteeReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommitteeReports(reports: List<CommitteeReportEntity>)

    // Committee Tasks
    @Query("SELECT * FROM committee_tasks ORDER BY isCompleted ASC, deadline ASC")
    fun getAllCommitteeTasks(): Flow<List<CommitteeTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommitteeTask(task: CommitteeTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommitteeTasks(tasks: List<CommitteeTaskEntity>)

    @Update
    suspend fun updateCommitteeTask(task: CommitteeTaskEntity)

    // Evangelism
    @Query("SELECT * FROM evangelism_outreach ORDER BY dateStr DESC")
    fun getAllEvangelism(): Flow<List<EvangelismEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvangelism(outreach: EvangelismEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvangelismList(list: List<EvangelismEntity>)

    // Ministry Events
    @Query("SELECT * FROM ministry_events ORDER BY dateStr ASC")
    fun getAllMinistryEvents(): Flow<List<MinistryEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMinistryEvent(event: MinistryEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMinistryEvents(events: List<MinistryEventEntity>)

    @Update
    suspend fun updateMinistryEvent(event: MinistryEventEntity)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    // Activity Logs
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLogs(logs: List<ActivityLogEntity>)
}
