package com.example

import com.example.data.model.MinistryRoles
import com.example.data.model.SecurityUtils
import com.example.data.model.UserEntity
import com.example.security.AuthorizationService
import com.example.security.Permission
import org.junit.Assert.*
import org.junit.Test

class SecurityAuthorizationTest {

    private fun createUser(role: String, status: String = "active"): UserEntity {
        return UserEntity(
            uid = "test_${role}_uid",
            displayName = "Test $role",
            email = "$role@youthtransformers.org",
            role = role,
            status = status,
            passwordHash = SecurityUtils.hashPassword("Password123!", "salt_123"),
            salt = "salt_123"
        )
    }

    @Test
    fun testPasswordHashingAndVerification() {
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword("SecurePass2026!", salt)

        assertTrue(SecurityUtils.verifyPassword("SecurePass2026!", hash, salt))
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", hash, salt))
    }

    @Test
    fun testLeaderHasFullAdministrativePermissions() {
        val leader = createUser(MinistryRoles.LEADER)

        assertTrue(AuthorizationService.isAuthorized(leader, Permission.ACCESS_LEADER_DASHBOARD))
        assertTrue(AuthorizationService.isAuthorized(leader, Permission.MANAGE_USERS))
        assertTrue(AuthorizationService.isAuthorized(leader, Permission.VIEW_ACTIVITY_LOGS))
        assertTrue(AuthorizationService.isAuthorized(leader, Permission.ACCESS_FINANCE_DASHBOARD))
        assertTrue(AuthorizationService.isAuthorized(leader, Permission.VIEW_MEMBERS))
        assertTrue(AuthorizationService.isAuthorized(leader, Permission.ACCESS_BIBLE_STUDY_DASHBOARD))
    }

    @Test
    fun testMemberCareRoleCannotAccessFinanceOrUserManagement() {
        val cedrick = createUser(MinistryRoles.MEMBER_CARE)

        // Member care is authorized for pastoral care and member directory
        assertTrue(AuthorizationService.isAuthorized(cedrick, Permission.ACCESS_MEMBER_CARE_DASHBOARD))
        assertTrue(AuthorizationService.isAuthorized(cedrick, Permission.VIEW_MEMBERS))
        assertTrue(AuthorizationService.isAuthorized(cedrick, Permission.MANAGE_FOLLOW_UPS))

        // Member care CANNOT access Leader Dashboard, Finance, or User Management
        assertFalse(AuthorizationService.isAuthorized(cedrick, Permission.ACCESS_LEADER_DASHBOARD))
        assertFalse(AuthorizationService.isAuthorized(cedrick, Permission.MANAGE_USERS))
        assertFalse(AuthorizationService.isAuthorized(cedrick, Permission.ACCESS_FINANCE_DASHBOARD))
        assertFalse(AuthorizationService.isAuthorized(cedrick, Permission.VIEW_ACTIVITY_LOGS))

        // Assert throws SecurityException
        try {
            AuthorizationService.assertAuthorized(cedrick, Permission.MANAGE_USERS, "users")
            fail("Expected SecurityException when Member Care accesses User Management")
        } catch (e: SecurityException) {
            assertTrue(e.message?.contains("403") == true)
        }
    }

    @Test
    fun testAccountantRoleCannotAccessMemberCareOrUserManagement() {
        val ebenezer = createUser(MinistryRoles.ACCOUNTANT)

        // Accountant is authorized for Finance
        assertTrue(AuthorizationService.isAuthorized(ebenezer, Permission.ACCESS_FINANCE_DASHBOARD))
        assertTrue(AuthorizationService.isAuthorized(ebenezer, Permission.MANAGE_TRANSACTIONS))

        // Accountant CANNOT access Member Care, User Management, or Leader Dashboard
        assertFalse(AuthorizationService.isAuthorized(ebenezer, Permission.ACCESS_LEADER_DASHBOARD))
        assertFalse(AuthorizationService.isAuthorized(ebenezer, Permission.MANAGE_USERS))
        assertFalse(AuthorizationService.isAuthorized(ebenezer, Permission.ACCESS_MEMBER_CARE_DASHBOARD))

        try {
            AuthorizationService.assertAuthorized(ebenezer, Permission.MANAGE_USERS, "users")
            fail("Expected SecurityException when Accountant accesses User Management")
        } catch (e: SecurityException) {
            assertTrue(e.message?.contains("403") == true)
        }
    }

    @Test
    fun testDisabledUserIsRejectedForAllPermissions() {
        val disabledLeader = createUser(MinistryRoles.LEADER, status = "disabled")

        assertFalse(AuthorizationService.isAuthorized(disabledLeader, Permission.ACCESS_LEADER_DASHBOARD))
        assertFalse(AuthorizationService.isAuthorized(disabledLeader, Permission.VIEW_ANNOUNCEMENTS))

        try {
            AuthorizationService.assertAuthorized(disabledLeader, Permission.VIEW_ANNOUNCEMENTS, "announcements")
            fail("Expected SecurityException for disabled user")
        } catch (e: SecurityException) {
            assertTrue(e.message?.contains("disabled") == true)
        }
    }
}
