package com.example.ui.util

enum class AppLanguage(val label: String, val nativeName: String) {
    ENGLISH("English", "English"),
    KINYARWANDA("Kinyarwanda", "Ikinyarwanda")
}

object MinistryStrings {
    fun t(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ENGLISH -> englishMap[key] ?: key
            AppLanguage.KINYARWANDA -> kinyarwandaMap[key] ?: englishMap[key] ?: key
        }
    }

    private val englishMap = mapOf(
        "app_name" to "Youth Transformers",
        "app_tagline" to "Transforming young lives through Christ.",
        "nav_dashboard" to "Dashboard",
        "nav_members" to "Members",
        "nav_committee" to "Committee & Tasks",
        "nav_bible_study" to "Bible Study",
        "nav_attendance" to "Attendance",
        "nav_evangelism" to "Evangelism",
        "nav_social_media" to "Social Media",
        "nav_member_care" to "Member Care",
        "nav_finance" to "Finance",
        "nav_projects" to "Projects",
        "nav_equipment" to "Equipment",
        "nav_events" to "Events",
        "nav_announcements" to "Announcements",
        "nav_reports" to "Reports",
        "nav_users" to "User Management",
        "nav_activity_log" to "Activity Log",
        "nav_settings" to "Settings",
        "login" to "Log In",
        "logout" to "Log Out",
        "welcome" to "Welcome",
        "welcome_back" to "Welcome Back",
        "total_members" to "Total Members",
        "active_members" to "Active Members",
        "new_members" to "New Members",
        "follow_up_needed" to "Need Follow-up",
        "weekly_target" to "Weekly Target",
        "published" to "Published",
        "remaining" to "Remaining",
        "quick_demo_login" to "Quick Role Switcher (Demo)",
        "switch_role_hint" to "Click any leader below to switch instantly or log in with credentials:",
        "search_hint" to "Search by name, phone, or keyword...",
        "add_member" to "Add Member",
        "status_active" to "Active",
        "status_inactive" to "Inactive",
        "status_follow_up" to "Follow-up required",
        "status_deactivated" to "Deactivated",
        "save" to "Save",
        "cancel" to "Cancel",
        "edit" to "Edit",
        "deactivate" to "Deactivate",
        "reactivate" to "Reactivate",
        "profile" to "Profile",
        "discipleship" to "Discipleship",
        "history" to "History"
    )

    private val kinyarwandaMap = mapOf(
        "app_name" to "Youth Transformers",
        "app_tagline" to "Guhindura imibereho y'urubyiruko muri Kristo.",
        "nav_dashboard" to "Imbonerahamwe",
        "nav_members" to "Abanyamuryango",
        "nav_committee" to "Komite n'Imirimo",
        "nav_bible_study" to "Inyigisho za Bibiliya",
        "nav_attendance" to "Ubwitabire",
        "nav_evangelism" to "Ivugabutumwa",
        "nav_social_media" to "Imbuga Nkoranyambaga",
        "nav_member_care" to "Kwitaho Abantu",
        "nav_finance" to "Imari n'Umutungo",
        "nav_projects" to "Imishinga",
        "nav_equipment" to "Ibikoresho",
        "nav_events" to "Ibirori n'Ibikorwa",
        "nav_announcements" to "Amatangazo",
        "nav_reports" to "Raporo z'Ubuyobozi",
        "nav_users" to "Abakoresha Sisitemu",
        "nav_activity_log" to "Ibyakozwe Byose",
        "nav_settings" to "Igenamiterere",
        "login" to "Injira",
        "logout" to "Sohoka",
        "welcome" to "Murakaza Neza",
        "welcome_back" to "Muraho Neza",
        "total_members" to "Abanyamuryango Bose",
        "active_members" to "Abanyamuryango Bakora",
        "new_members" to "Abanyamuryango Bashya",
        "follow_up_needed" to "Bakeneye Kwitabwaho",
        "weekly_target" to "Intego y'Icyumweru",
        "published" to "Ibyasohotse",
        "remaining" to "Ibisigaye",
        "quick_demo_login" to "Guhindura Uruhare Byihuse (Demo)",
        "switch_role_hint" to "Kanda kuri umwe mu bayobozi hejuru cyangwa wandike imyirondoro:",
        "search_hint" to "Shakisha izina, telefone cyangwa ijambo...",
        "add_member" to "Ongeraho Umunyamuryango",
        "status_active" to "Arakora",
        "status_inactive" to "Ntacyo akora",
        "status_follow_up" to "Akeneye Kwitabwaho",
        "status_deactivated" to "Yarahagaritswe",
        "save" to "Bika",
        "cancel" to "Hagarika",
        "edit" to "Hindura",
        "deactivate" to "Hagarika",
        "reactivate" to "Subizaho",
        "profile" to "Umwirondoro",
        "discipleship" to "Gukurikira Kristo",
        "history" to "Amateka"
    )
}
