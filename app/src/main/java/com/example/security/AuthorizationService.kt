package com.example.security

import com.example.data.model.MinistryRoles
import com.example.data.model.UserEntity

enum class Permission {
    // Leader-only system permissions
    ACCESS_LEADER_DASHBOARD,
    MANAGE_USERS,
    VIEW_ACTIVITY_LOGS,
    SYSTEM_SETTINGS,
    PERMANENT_DELETE_MEMBERS,

    // Member Care permissions
    ACCESS_MEMBER_CARE_DASHBOARD,
    VIEW_MEMBERS,
    ADD_EDIT_MEMBERS,
    DEACTIVATE_MEMBERS,
    MANAGE_FOLLOW_UPS,

    // Level 1 Discipleship permissions
    ACCESS_LEVEL1_DASHBOARD,
    MANAGE_LEVEL1_CURRICULUM,
    RECOMMEND_PROGRESSION,

    // Bible Study permissions
    ACCESS_BIBLE_STUDY_DASHBOARD,
    MANAGE_BIBLE_STUDIES,
    LOG_ATTENDANCE,

    // Committee permissions
    ACCESS_COMMITTEE_DASHBOARD,
    MANAGE_COMMITTEE_TASKS,
    SUBMIT_COMMITTEE_REPORTS,

    // Social Media permissions
    ACCESS_SOCIAL_MEDIA_DASHBOARD,
    MANAGE_CONTENT_CALENDAR,
    TRIAGE_SOCIAL_INTERACTIONS,

    // Projects & Equipment permissions
    ACCESS_PROJECTS_DASHBOARD,
    MANAGE_PROJECTS,
    MANAGE_EQUIPMENT,

    // Finance permissions
    ACCESS_FINANCE_DASHBOARD,
    MANAGE_TRANSACTIONS,
    VIEW_FINANCIAL_REPORTS,

    // Evangelism & Events
    LOG_EVANGELISM,
    MANAGE_EVENTS,

    // Announcements
    POST_ANNOUNCEMENTS,
    VIEW_ANNOUNCEMENTS
}

object AuthorizationService {

    fun isAuthorized(user: UserEntity?, permission: Permission): Boolean {
        if (user == null || user.status != "active") return false
        val role = user.role.lowercase().trim()

        // LEADER has full administrative access to all ministry capabilities
        if (role == MinistryRoles.LEADER) return true

        return when (permission) {
            Permission.ACCESS_LEADER_DASHBOARD,
            Permission.MANAGE_USERS,
            Permission.VIEW_ACTIVITY_LOGS,
            Permission.SYSTEM_SETTINGS,
            Permission.PERMANENT_DELETE_MEMBERS -> false // Exclusively Leader

            Permission.ACCESS_MEMBER_CARE_DASHBOARD,
            Permission.MANAGE_FOLLOW_UPS -> role == MinistryRoles.MEMBER_CARE

            Permission.VIEW_MEMBERS -> role in listOf(
                MinistryRoles.MEMBER_CARE,
                MinistryRoles.LEVEL1_LEADER,
                MinistryRoles.BIBLE_STUDY
            )

            Permission.ADD_EDIT_MEMBERS,
            Permission.DEACTIVATE_MEMBERS -> role == MinistryRoles.MEMBER_CARE

            Permission.ACCESS_LEVEL1_DASHBOARD,
            Permission.MANAGE_LEVEL1_CURRICULUM,
            Permission.RECOMMEND_PROGRESSION -> role == MinistryRoles.LEVEL1_LEADER

            Permission.ACCESS_BIBLE_STUDY_DASHBOARD,
            Permission.MANAGE_BIBLE_STUDIES,
            Permission.LOG_ATTENDANCE -> role == MinistryRoles.BIBLE_STUDY

            Permission.ACCESS_COMMITTEE_DASHBOARD,
            Permission.MANAGE_COMMITTEE_TASKS,
            Permission.SUBMIT_COMMITTEE_REPORTS -> role == MinistryRoles.COMMITTEE_COORDINATOR

            Permission.ACCESS_SOCIAL_MEDIA_DASHBOARD,
            Permission.MANAGE_CONTENT_CALENDAR,
            Permission.TRIAGE_SOCIAL_INTERACTIONS -> role == MinistryRoles.SOCIAL_MEDIA

            Permission.ACCESS_PROJECTS_DASHBOARD,
            Permission.MANAGE_PROJECTS,
            Permission.MANAGE_EQUIPMENT -> role == MinistryRoles.PROJECTS_MANAGER

            Permission.ACCESS_FINANCE_DASHBOARD,
            Permission.MANAGE_TRANSACTIONS,
            Permission.VIEW_FINANCIAL_REPORTS -> role == MinistryRoles.ACCOUNTANT

            Permission.LOG_EVANGELISM -> role in listOf(
                MinistryRoles.LEVEL1_LEADER,
                MinistryRoles.BIBLE_STUDY,
                MinistryRoles.COMMITTEE_COORDINATOR
            )

            Permission.MANAGE_EVENTS -> role in listOf(
                MinistryRoles.COMMITTEE_COORDINATOR,
                MinistryRoles.PROJECTS_MANAGER
            )

            Permission.POST_ANNOUNCEMENTS -> role in listOf(
                MinistryRoles.COMMITTEE_COORDINATOR,
                MinistryRoles.SOCIAL_MEDIA
            )

            Permission.VIEW_ANNOUNCEMENTS -> true
        }
    }

    fun assertAuthorized(actor: UserEntity?, permission: Permission, resource: String) {
        if (actor == null) {
            throw SecurityException("401: Unauthorized. Authentication session required.")
        }
        if (actor.status != "active") {
            throw SecurityException("403: Access Denied. Account is disabled.")
        }
        if (!isAuthorized(actor, permission)) {
            throw SecurityException("403: Access Denied. Role '${actor.role}' is not authorized to access or modify $resource.")
        }
    }
}
