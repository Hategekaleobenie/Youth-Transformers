package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.MessageDigest

object SecurityUtils {
    private const val DEFAULT_SALT = "YouthTransformers_Salt_2026"

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        java.security.SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String = DEFAULT_SALT): String {
        val bytes = "$salt:$password".toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String, salt: String = DEFAULT_SALT): Boolean {
        return hashPassword(password, salt) == storedHash
    }
}

object MinistryRoles {
    const val LEADER = "leader"
    const val COMMITTEE_COORDINATOR = "committee_coordinator"
    const val LEVEL1_LEADER = "level1_leader"
    const val SOCIAL_MEDIA = "social_media"
    const val MEMBER_CARE = "member_care"
    const val BIBLE_STUDY = "bible_study"
    const val ACCOUNTANT = "accountant"
    const val PROJECTS_MANAGER = "projects_manager"
    const val MEMBER = "member"

    val ALL_ROLES = listOf(
        LEADER,
        COMMITTEE_COORDINATOR,
        LEVEL1_LEADER,
        SOCIAL_MEDIA,
        MEMBER_CARE,
        BIBLE_STUDY,
        ACCOUNTANT,
        PROJECTS_MANAGER,
        MEMBER
    )

    fun getDisplayName(role: String): String = when (role) {
        LEADER -> "Ministry Leader"
        COMMITTEE_COORDINATOR -> "Committee Coordinator"
        LEVEL1_LEADER -> "Level 1 Leader"
        SOCIAL_MEDIA -> "Social Media & Interaction"
        MEMBER_CARE -> "Member Care"
        BIBLE_STUDY -> "Bible Study Coordinator"
        ACCOUNTANT -> "Accountant"
        PROJECTS_MANAGER -> "Projects & Equipment Manager"
        MEMBER -> "Ministry Member"
        else -> role.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

enum class MinistryRole(val title: String, val code: String) {
    MINISTRY_LEADER("Ministry Leader", MinistryRoles.LEADER),
    COMMITTEE_COORDINATOR("Committee Coordinator", MinistryRoles.COMMITTEE_COORDINATOR),
    LEVEL_1_LEADER("Level 1 Leader", MinistryRoles.LEVEL1_LEADER),
    SOCIAL_MEDIA("Social Media & Interaction", MinistryRoles.SOCIAL_MEDIA),
    MEMBER_CARE("Member Care", MinistryRoles.MEMBER_CARE),
    BIBLE_STUDY_COORDINATOR("Bible Study Coordinator", MinistryRoles.BIBLE_STUDY),
    ACCOUNTANT("Accountant", MinistryRoles.ACCOUNTANT),
    PROJECTS_EQUIPMENT_MANAGER("Projects & Equipment", MinistryRoles.PROJECTS_MANAGER),
    MEMBER("Member", MinistryRoles.MEMBER);

    companion object {
        fun fromString(role: String): MinistryRole {
            return entries.firstOrNull {
                it.code.equals(role, ignoreCase = true) ||
                it.name.equals(role, ignoreCase = true) ||
                it.title.equals(role, ignoreCase = true)
            } ?: MEMBER
        }
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val displayName: String,
    val email: String,
    val role: String, // leader, committee_coordinator, level1_leader, social_media, member_care, bible_study, accountant, projects_manager, member
    val status: String = "active", // active, disabled
    val mustChangePassword: Boolean = false,
    val passwordHash: String,
    val salt: String = "yt_default_salt",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = 0L
) {
    val id: Long get() = (uid.hashCode().toLong() and 0x7FFFFFFF)
    val fullName: String get() = displayName
    val username: String get() = email.substringBefore("@")
    val isActive: Boolean get() = status == "active"
}

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val dateJoined: String,
    val currentResidence: String,
    val permanentResidence: String,
    val employmentStatus: String, // Employed, Student, Job Seeking, Prefer not to say
    val educationStatus: String, // High School, Undergraduate, Graduate, Prefer not to say
    val familyInfo: String, // Living with family, Independent, Prefer not to say
    val ministryLevel: String, // Level 1, Level 2, Leadership, Member
    val attendancePercent: Int = 80,
    val bibleStudyParticipation: String = "Regular", // Regular, Occasional, Rare
    val evangelismParticipation: String = "Active", // Active, Interested, None
    val assignedLeader: String = "Bonheur Ndinzwe",
    val memberStatus: String = "Active", // Active, Inactive, Follow-up required, Deactivated
    val notes: String = ""
)

@Entity(tableName = "discipleship_records")
data class DiscipleshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val topicName: String, // Salvation, Prayer, Bible, Faith, Holy Spirit, Christian Character, Evangelism, Serving, Leadership
    val status: String, // Not Started, In Progress, Completed
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "progression_recommendations")
data class ProgressionRecommendationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val recommendedBy: String,
    val targetLevel: String,
    val reason: String,
    val status: String = "Pending", // Pending, Approved, Reviewed
    val dateStr: String
)

@Entity(tableName = "social_posts")
data class SocialPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val platform: String, // Instagram, Facebook, TikTok, YouTube, WhatsApp, Other
    val scheduledDate: String,
    val status: String, // Draft, In Review, Approved, Published
    val link: String = "",
    val responsiblePerson: String,
    val approvalStatus: String = "Approved",
    val weekNumber: Int = 1
)

@Entity(tableName = "social_interactions")
data class SocialInteractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // Prayer Request, Question, Comment, New Person, Follow-up Request
    val senderName: String,
    val platform: String,
    val message: String,
    val status: String = "New", // New, Responded, Referred to Member Care
    val dateStr: String
)

@Entity(tableName = "follow_up_cases")
data class FollowUpCaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val assignedPerson: String,
    val reason: String, // Attendance, Academic, Employment, Financial, Family, Spiritual, General support, Prefer not to say
    val dateOpened: String,
    val priority: String, // Low, Medium, High, Urgent
    val status: String = "Open", // Open, In Progress, Waiting, Resolved, Closed
    val notes: String,
    val nextFollowUpDate: String,
    val resolution: String = ""
)

@Entity(tableName = "bible_studies")
data class BibleStudyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String,
    val preacher: String,
    val host: String,
    val dateStr: String,
    val timeStr: String,
    val scriptureRefs: String,
    val notes: String = "",
    val isRecurring: Boolean = false,
    val curriculumTrack: String = "Christian Foundations",
    val attendeesCount: Int = 0
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityType: String, // Bible Study, Gathering, Evangelism, Committee Meeting, Event
    val activityTitle: String,
    val dateStr: String,
    val memberId: Long,
    val memberName: String,
    val status: String, // Present, Absent, Excused
    val notes: String = ""
)

@Entity(tableName = "financial_transactions")
data class FinancialTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String,
    val description: String,
    val category: String, // Donations, Fundraising, Event expenses, Equipment expenses, Transport, Other
    val amount: Double,
    val type: String, // Income, Expense
    val recordedBy: String,
    val notes: String = ""
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val leader: String,
    val startDate: String,
    val deadline: String,
    val budget: Double,
    val status: String, // Planning, Active, Delayed, Completed, Cancelled
    val progressPercent: Int = 0,
    val teamMembers: String = "",
    val tasksSummary: String = ""
)

@Entity(tableName = "equipment_items")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Audio, Video, Instruments, Furniture, IT, Other
    val quantity: Int,
    val condition: String, // Available, In Use, Borrowed, Maintenance, Missing, Damaged
    val location: String,
    val responsiblePerson: String,
    val status: String, // Available, In Use, Borrowed, Maintenance, Missing, Damaged
    val borrowedBy: String = "",
    val returnDate: String = "",
    val purchaseDate: String = "",
    val notes: String = ""
)

@Entity(tableName = "committee_reports")
data class CommitteeReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorRole: String,
    val reportType: String, // Daily update, Weekly report, Monthly report
    val dateStr: String,
    val accomplished: String,
    val currentWork: String,
    val challenges: String,
    val supportNeeded: String,
    val nextSteps: String,
    val prayerNotes: String,
    val status: String = "Submitted"
)

@Entity(tableName = "committee_tasks")
data class CommitteeTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val assignedToName: String,
    val assignedToRole: String,
    val deadline: String,
    val priority: String = "Normal", // Normal, High, Urgent
    val isCompleted: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "evangelism_outreach")
data class EvangelismEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateStr: String,
    val location: String,
    val teamLead: String,
    val participantsCount: Int,
    val peopleReachedCount: Int,
    val followUpsCount: Int,
    val newContactsCount: Int,
    val testimonies: String = "",
    val notes: String = ""
)

@Entity(tableName = "ministry_events")
data class MinistryEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val dateStr: String,
    val timeStr: String,
    val location: String,
    val organizer: String,
    val budget: Double,
    val expectedAttendance: Int,
    val actualAttendance: Int = 0,
    val status: String = "Upcoming" // Upcoming, In Progress, Completed, Cancelled
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val targetAudience: String, // Entire Ministry, Committee, Level 1, Media Team, Bible Study Team, Member Care Team
    val authorName: String,
    val dateStr: String,
    val isImportant: Boolean = false
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorUid: String,
    val actorName: String,
    val action: String,
    val resourceType: String,
    val resourceId: String = "",
    val details: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val result: String = "SUCCESS" // SUCCESS or ACCESS_DENIED
) {
    val userName: String get() = actorName
    val objectType: String get() = resourceType
    val dateStr: String get() = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
}
