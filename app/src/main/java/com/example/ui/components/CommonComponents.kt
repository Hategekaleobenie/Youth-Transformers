package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MinistryRoles
import com.example.data.model.UserEntity
import com.example.security.AuthorizationService
import com.example.security.Permission
import com.example.ui.theme.*
import com.example.ui.util.AppLanguage
import com.example.ui.util.MinistryStrings
import com.example.ui.viewmodel.MinistryNavSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinistryTopAppBar(
    currentSection: MinistryNavSection,
    currentUser: UserEntity?,
    currentLang: AppLanguage,
    onToggleLanguage: () -> Unit,
    onMenuClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showUserMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = MinistryStrings.t(currentSection.labelKey, currentLang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Youth Transformers",
                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleGold),
                    fontSize = 11.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = DeepForestGreen
                )
            }
        },
        actions = {
            // Language selector button
            FilledTonalButton(
                onClick = onToggleLanguage,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = SageContainer,
                    contentColor = DeepForestGreen
                ),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (currentLang == AppLanguage.ENGLISH) "EN" else "RW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Current user avatar & dropdown
            Box {
                IconButton(onClick = { showUserMenu = true }) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DeepForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.displayName?.take(2)?.uppercase() ?: "YT",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                DropdownMenu(
                    expanded = showUserMenu,
                    onDismissRequest = { showUserMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = currentUser?.displayName ?: "User",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = currentUser?.email ?: "",
                                    fontSize = 11.sp,
                                    color = CoolGrey
                                )
                                Text(
                                    text = MinistryRoles.getDisplayName(currentUser?.role ?: "none"),
                                    fontSize = 11.sp,
                                    color = SubtleGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        onClick = {},
                        enabled = false
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Change Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        onClick = {
                            showUserMenu = false
                            onChangePasswordClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(MinistryStrings.t("logout", currentLang), color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            showUserMenu = false
                            onLogoutClick()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CleanWhite,
            titleContentColor = DarkCharcoal
        )
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconColor: Color = DeepForestGreen,
    iconBgColor: Color = SageContainer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(color = CoolGrey),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(color = SubtleGold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "active", "completed", "published", "approved", "resolved", "available" -> Pair(Color(0xFFE3F7EB), Color(0xFF0F6E3B))
        "follow-up required", "in progress", "in review", "borrowed", "planning", "medium" -> Pair(Color(0xFFFEF3D6), Color(0xFF8A5B00))
        "urgent", "high", "delayed", "missing", "damaged" -> Pair(Color(0xFFFDE8E8), Color(0xFFB81D1D))
        "deactivated", "cancelled", "inactive", "disabled" -> Pair(Color(0xFFEAEAEA), Color(0xFF5A5A5A))
        else -> Pair(Color(0xFFEBF2F7), Color(0xFF2A527A))
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun NavDrawerContent(
    currentSection: MinistryNavSection,
    currentUser: UserEntity?,
    currentLang: AppLanguage,
    onSelectSection: (MinistryNavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.widthIn(max = 300.dp),
        drawerContainerColor = SoftBackground
    ) {
        // Drawer Header with Authenticated Identity
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepForestGreen)
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Church,
                        contentDescription = null,
                        tint = RadiantGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YOUTH TRANSFORMERS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentUser?.displayName ?: "Ministry Portal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = currentUser?.email ?: "",
                    color = SageContainer,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = "Role: ${MinistryRoles.getDisplayName(currentUser?.role ?: "none")}",
                        color = RadiantGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Role-Based Navigation Items (strictly authorized views)
        val items = buildList {
            add(Pair(MinistryNavSection.DASHBOARD, Icons.Default.Dashboard))
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.VIEW_MEMBERS)) {
                add(Pair(MinistryNavSection.MEMBERS, Icons.Default.People))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_LEVEL1_DASHBOARD)) {
                add(Pair(MinistryNavSection.LEVEL_1, Icons.Default.School))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_BIBLE_STUDY_DASHBOARD)) {
                add(Pair(MinistryNavSection.BIBLE_STUDY, Icons.Default.MenuBook))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.LOG_ATTENDANCE)) {
                add(Pair(MinistryNavSection.ATTENDANCE, Icons.Default.CheckCircle))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_MEMBER_CARE_DASHBOARD)) {
                add(Pair(MinistryNavSection.MEMBER_CARE, Icons.Default.Favorite))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_SOCIAL_MEDIA_DASHBOARD)) {
                add(Pair(MinistryNavSection.SOCIAL_MEDIA, Icons.Default.Share))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_FINANCE_DASHBOARD)) {
                add(Pair(MinistryNavSection.FINANCE, Icons.Default.AttachMoney))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_PROJECTS_DASHBOARD)) {
                add(Pair(MinistryNavSection.PROJECTS, Icons.Default.Assignment))
                add(Pair(MinistryNavSection.EQUIPMENT, Icons.Default.Inventory2))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.ACCESS_COMMITTEE_DASHBOARD)) {
                add(Pair(MinistryNavSection.COMMITTEE, Icons.Default.Groups))
                add(Pair(MinistryNavSection.REPORTS, Icons.Default.Description))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.LOG_EVANGELISM)) {
                add(Pair(MinistryNavSection.EVANGELISM, Icons.Default.Public))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.MANAGE_EVENTS)) {
                add(Pair(MinistryNavSection.EVENTS, Icons.Default.Event))
            }
            add(Pair(MinistryNavSection.ANNOUNCEMENTS, Icons.Default.Campaign))
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.MANAGE_USERS)) {
                add(Pair(MinistryNavSection.USERS, Icons.Default.ManageAccounts))
            }
            if (currentUser != null && AuthorizationService.isAuthorized(currentUser, Permission.VIEW_ACTIVITY_LOGS)) {
                add(Pair(MinistryNavSection.ACTIVITY_LOG, Icons.Default.History))
            }
            add(Pair(MinistryNavSection.SETTINGS, Icons.Default.Settings))
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            items.forEach { (section, icon) ->
                val selected = currentSection == section
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selected) DeepForestGreen else CoolGrey
                        )
                    },
                    label = {
                        Text(
                            text = MinistryStrings.t(section.labelKey, currentLang),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) DeepForestGreen else DarkCharcoal,
                            fontSize = 13.sp
                        )
                    },
                    selected = selected,
                    onClick = { onSelectSection(section) },
                    shape = RoundedCornerShape(10.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = SageContainer,
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
