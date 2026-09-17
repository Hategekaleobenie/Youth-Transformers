package com.example.data.local

import com.example.data.model.*

object SeedDataProvider {
    suspend fun populateDatabase(dao: YouthTransformersDao) {
        val defaultSalt = "yt_init_salt_2026"
        val defaultPassHash = SecurityUtils.hashPassword("Transform2026!", defaultSalt)

        // 1. Initial Users with Real UIDs, Roles, and Salted Hashes
        val users = listOf(
            UserEntity(
                uid = "uid_leo_hategeka",
                displayName = "LEO BENIE HATEGEKA",
                email = "hategekabenie@gmail.com",
                role = MinistryRoles.LEADER,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 123 456"
            ),
            UserEntity(
                uid = "uid_green_london",
                displayName = "GREEN LONDON",
                email = "london@youthtransformers.org",
                role = MinistryRoles.COMMITTEE_COORDINATOR,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 234 567"
            ),
            UserEntity(
                uid = "uid_bonheur_ndinzwe",
                displayName = "BONHEUR NDINZWE",
                email = "Ndinzwe@gmail.com",
                role = MinistryRoles.LEVEL1_LEADER,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 345 678"
            ),
            UserEntity(
                uid = "uid_kellia_mwizerwa",
                displayName = "KELLIA MWIZERWA",
                email = "kellia@youthtransformers.org",
                role = MinistryRoles.SOCIAL_MEDIA,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 456 789"
            ),
            UserEntity(
                uid = "uid_cedrick_gisubizo",
                displayName = "CEDRICK GISUBIZO",
                email = "gisubizocedrick720@gmail.com",
                role = MinistryRoles.MEMBER_CARE,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 567 890"
            ),
            UserEntity(
                uid = "uid_liona_akaliza",
                displayName = "LIONA AKALIZA",
                email = "akalizaliona@gmail.com",
                role = MinistryRoles.BIBLE_STUDY,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 678 901"
            ),
            UserEntity(
                uid = "uid_ebenezer_mugisha",
                displayName = "EBENEZER MUGISHA",
                email = "ebenezer@youthtransformers.org",
                role = MinistryRoles.ACCOUNTANT,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 789 012"
            ),
            UserEntity(
                uid = "uid_rene_cyubahiro",
                displayName = "RENE CYUBAHIRO",
                email = "rene@youthtransformers.org",
                role = MinistryRoles.PROJECTS_MANAGER,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 890 123"
            ),
            UserEntity(
                uid = "uid_david_kwizera",
                displayName = "DAVID KWIZERA",
                email = "david.kwizera@gmail.com",
                role = MinistryRoles.MEMBER,
                status = "active",
                passwordHash = defaultPassHash,
                salt = defaultSalt,
                phone = "+250 788 901 234"
            )
        )
        dao.insertUsers(users)

        // 2. Sample 22 Members (Active, Inactive, Follow-up, Level 1, etc.)
        val members = listOf(
            MemberEntity(
                fullName = "David Kwizera",
                email = "david.kwizera@gmail.com",
                phone = "+250 788 901 234",
                dateJoined = "2025-01-15",
                currentResidence = "Kigali, Remera",
                permanentResidence = "Huye, Southern Province",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 88,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Growing strongly in prayer discipleship."
            ),
            MemberEntity(
                fullName = "Aline Umutoni",
                email = "aline.umutoni@gmail.com",
                phone = "+250 789 111 222",
                dateJoined = "2025-02-10",
                currentResidence = "Kigali, Kacyiru",
                permanentResidence = "Musanze, Northern Province",
                employmentStatus = "Employed",
                educationStatus = "Undergraduate",
                familyInfo = "Independent",
                ministryLevel = "Level 1",
                attendancePercent = 92,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Consistent attendance in fellowship."
            ),
            MemberEntity(
                fullName = "Jean Claude Habimana",
                email = "habimana.jc@gmail.com",
                phone = "+250 788 222 333",
                dateJoined = "2024-11-05",
                currentResidence = "Kigali, Kimironko",
                permanentResidence = "Kigali",
                employmentStatus = "Job Seeking",
                educationStatus = "High School",
                familyInfo = "Prefer not to say",
                ministryLevel = "Level 1",
                attendancePercent = 65,
                bibleStudyParticipation = "Occasional",
                evangelismParticipation = "Interested",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Follow-up required",
                notes = "Needs encouragement with transportation and job search support."
            ),
            MemberEntity(
                fullName = "Grace Uwase",
                email = "grace.uwase@gmail.com",
                phone = "+250 788 333 444",
                dateJoined = "2025-03-01",
                currentResidence = "Kigali, Gikondo",
                permanentResidence = "Rwamagana",
                employmentStatus = "Student",
                educationStatus = "High School",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 95,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Active in choir and prayer group."
            ),
            MemberEntity(
                fullName = "Eric Nshimiyimana",
                email = "eric.n@gmail.com",
                phone = "+250 788 444 555",
                dateJoined = "2024-09-12",
                currentResidence = "Kigali, Nyamirambo",
                permanentResidence = "Rubavu",
                employmentStatus = "Employed",
                educationStatus = "Graduate",
                familyInfo = "Independent",
                ministryLevel = "Level 2",
                attendancePercent = 82,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Cedrick Gisubizo",
                memberStatus = "Active",
                notes = "Assists in sound tech."
            ),
            MemberEntity(
                fullName = "Diane Mukamana",
                email = "diane.m@gmail.com",
                phone = "+250 788 555 666",
                dateJoined = "2024-08-20",
                currentResidence = "Kigali, Kanombe",
                permanentResidence = "Kigali",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 2",
                attendancePercent = 78,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Interested",
                assignedLeader = "Cedrick Gisubizo",
                memberStatus = "Active",
                notes = "Participates in youth retreats."
            ),
            MemberEntity(
                fullName = "Patrick Mugabo",
                email = "mugabo.p@gmail.com",
                phone = "+250 788 666 777",
                dateJoined = "2025-04-18",
                currentResidence = "Kigali, Kicukiro",
                permanentResidence = "Kayonza",
                employmentStatus = "Self-employed",
                educationStatus = "Undergraduate",
                familyInfo = "Independent",
                ministryLevel = "Level 1",
                attendancePercent = 50,
                bibleStudyParticipation = "Rare",
                evangelismParticipation = "None",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Follow-up required",
                notes = "Has missed the last three sessions due to work schedule."
            ),
            MemberEntity(
                fullName = "Sandrine Ishimwe",
                email = "sandrine.i@gmail.com",
                phone = "+250 788 777 888",
                dateJoined = "2024-10-02",
                currentResidence = "Kigali, Niboye",
                permanentResidence = "Kigali",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 2",
                attendancePercent = 90,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Liona Akaliza",
                memberStatus = "Active",
                notes = "Helps host home fellowship."
            ),
            MemberEntity(
                fullName = "Samuel Rukundo",
                email = "samuel.r@gmail.com",
                phone = "+250 788 888 999",
                dateJoined = "2024-06-11",
                currentResidence = "Kigali, Nyarutarama",
                permanentResidence = "Huye",
                employmentStatus = "Employed",
                educationStatus = "Graduate",
                familyInfo = "Prefer not to say",
                ministryLevel = "Leadership",
                attendancePercent = 96,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Leo Benie Hategeka",
                memberStatus = "Active",
                notes = "Preaches occasionally and mentors younger youth."
            ),
            MemberEntity(
                fullName = "Claire Keza",
                email = "keza.claire@gmail.com",
                phone = "+250 789 000 111",
                dateJoined = "2025-05-09",
                currentResidence = "Kigali, Batsinda",
                permanentResidence = "Kigali",
                employmentStatus = "Student",
                educationStatus = "High School",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 85,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Interested in social media and photography."
            ),
            MemberEntity(
                fullName = "Innocent Bizimana",
                email = "innocent.b@gmail.com",
                phone = "+250 789 222 333",
                dateJoined = "2024-03-14",
                currentResidence = "Kigali, Masaka",
                permanentResidence = "Bugesera",
                employmentStatus = "Job Seeking",
                educationStatus = "Undergraduate",
                familyInfo = "Independent",
                ministryLevel = "Member",
                attendancePercent = 40,
                bibleStudyParticipation = "Rare",
                evangelismParticipation = "None",
                assignedLeader = "Cedrick Gisubizo",
                memberStatus = "Inactive",
                notes = "Relocated temporarily for internship."
            ),
            MemberEntity(
                fullName = "Faith Uwera",
                email = "faith.u@gmail.com",
                phone = "+250 789 333 444",
                dateJoined = "2025-02-28",
                currentResidence = "Kigali, Kabeza",
                permanentResidence = "Kigali",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 89,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Passionate for outreach and hospital visits."
            ),
            MemberEntity(
                fullName = "Christian Kayitare",
                email = "christian.k@gmail.com",
                phone = "+250 789 444 555",
                dateJoined = "2024-12-01",
                currentResidence = "Kigali, Rebero",
                permanentResidence = "Nyanza",
                employmentStatus = "Employed",
                educationStatus = "Undergraduate",
                familyInfo = "Independent",
                ministryLevel = "Member",
                attendancePercent = 84,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Interested",
                assignedLeader = "Cedrick Gisubizo",
                memberStatus = "Active",
                notes = "Assists in equipment setup and hospitality."
            ),
            MemberEntity(
                fullName = "Peace Ingabire",
                email = "peace.i@gmail.com",
                phone = "+250 789 555 666",
                dateJoined = "2025-01-20",
                currentResidence = "Kigali, Zindiro",
                permanentResidence = "Gatsibo",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 75,
                bibleStudyParticipation = "Occasional",
                evangelismParticipation = "Interested",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Studying discipleship foundations."
            ),
            MemberEntity(
                fullName = "Gervais Manzi",
                email = "gervais.manzi@gmail.com",
                phone = "+250 789 666 777",
                dateJoined = "2024-05-15",
                currentResidence = "Kigali, Kagugu",
                permanentResidence = "Kigali",
                employmentStatus = "Self-employed",
                educationStatus = "Graduate",
                familyInfo = "Independent",
                ministryLevel = "Leadership",
                attendancePercent = 91,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Leo Benie Hategeka",
                memberStatus = "Active",
                notes = "Leads youth worship team."
            ),
            MemberEntity(
                fullName = "Chantal Nyiramugisha",
                email = "chantal.n@gmail.com",
                phone = "+250 789 777 888",
                dateJoined = "2025-03-11",
                currentResidence = "Kigali, Rugando",
                permanentResidence = "Nyagatare",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 93,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Completed Salvation and Prayer curriculum topics."
            ),
            MemberEntity(
                fullName = "Emmanuel Ndayishimiye",
                email = "emmanuel.nd@gmail.com",
                phone = "+250 789 888 999",
                dateJoined = "2024-07-22",
                currentResidence = "Kigali, Biryogo",
                permanentResidence = "Karongi",
                employmentStatus = "Employed",
                educationStatus = "High School",
                familyInfo = "Prefer not to say",
                ministryLevel = "Member",
                attendancePercent = 70,
                bibleStudyParticipation = "Occasional",
                evangelismParticipation = "None",
                assignedLeader = "Cedrick Gisubizo",
                memberStatus = "Active",
                notes = "Evening shift worker."
            ),
            MemberEntity(
                fullName = "Gloria Mutoni",
                email = "gloria.m@gmail.com",
                phone = "+250 789 999 000",
                dateJoined = "2025-04-05",
                currentResidence = "Kigali, Nyabugogo",
                permanentResidence = "Gicumbi",
                employmentStatus = "Student",
                educationStatus = "Undergraduate",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 60,
                bibleStudyParticipation = "Occasional",
                evangelismParticipation = "Interested",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Follow-up required",
                notes = "Bereavement in family last month, needs pastoral visitation."
            ),
            MemberEntity(
                fullName = "Yves Tuyishime",
                email = "yves.t@gmail.com",
                phone = "+250 781 111 222",
                dateJoined = "2024-02-18",
                currentResidence = "Kigali, Gisozi",
                permanentResidence = "Kigali",
                employmentStatus = "Employed",
                educationStatus = "Graduate",
                familyInfo = "Independent",
                ministryLevel = "Level 2",
                attendancePercent = 86,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Liona Akaliza",
                memberStatus = "Active",
                notes = "Assists in media streaming and projection."
            ),
            MemberEntity(
                fullName = "Priscilla Mukunde",
                email = "priscilla.m@gmail.com",
                phone = "+250 781 222 333",
                dateJoined = "2025-05-14",
                currentResidence = "Kigali, Gatenga",
                permanentResidence = "Kamonyi",
                employmentStatus = "Student",
                educationStatus = "High School",
                familyInfo = "Living with family",
                ministryLevel = "Level 1",
                attendancePercent = 94,
                bibleStudyParticipation = "Regular",
                evangelismParticipation = "Active",
                assignedLeader = "Bonheur Ndinzwe",
                memberStatus = "Active",
                notes = "Excelling in memory verses and fellowship."
            )
        )
        dao.insertMembers(members)

        // 3. Level 1 Discipleship Curriculum for Level 1 members
        val topics = listOf(
            "Salvation",
            "Prayer",
            "Bible",
            "Faith",
            "Holy Spirit",
            "Christian Character",
            "Evangelism",
            "Serving",
            "Leadership"
        )
        val discipleshipRecords = mutableListOf<DiscipleshipEntity>()
        val l1Members = listOf(
            Pair(1L, "David Kwizera"),
            Pair(2L, "Aline Umutoni"),
            Pair(3L, "Jean Claude Habimana"),
            Pair(4L, "Grace Uwase"),
            Pair(7L, "Patrick Mugabo"),
            Pair(10L, "Claire Keza")
        )
        l1Members.forEach { (memId, name) ->
            topics.forEachIndexed { index, topic ->
                val status = when {
                    index < 3 -> "Completed"
                    index < 6 -> "In Progress"
                    else -> "Not Started"
                }
                discipleshipRecords.add(
                    DiscipleshipEntity(
                        memberId = memId,
                        memberName = name,
                        topicName = topic,
                        status = status,
                        notes = if (status == "Completed") "Demonstrated firm biblical grasp" else ""
                    )
                )
            }
        }
        dao.insertDiscipleshipList(discipleshipRecords)

        // Progression recommendation
        dao.insertRecommendation(
            ProgressionRecommendationEntity(
                memberId = 2L,
                memberName = "Aline Umutoni",
                recommendedBy = "Bonheur Ndinzwe",
                targetLevel = "Level 2 Discipleship",
                reason = "Has completed 8 of 9 curriculum modules with exceptional character and regular church attendance.",
                status = "Pending",
                dateStr = "2026-09-15"
            )
        )

        // 4. Social Media Posts (Target: 3/week)
        val posts = listOf(
            SocialPostEntity(
                title = "Rejoice in the Lord: Philippians 4:4 Verse Video",
                platform = "Instagram",
                scheduledDate = "2026-09-18",
                status = "Published",
                link = "https://instagram.com/p/youthtransformers",
                responsiblePerson = "Kellia Mwizerwa",
                weekNumber = 38
            ),
            SocialPostEntity(
                title = "Youth Friday Fellowship Highlights Reel",
                platform = "TikTok",
                scheduledDate = "2026-09-19",
                status = "Published",
                link = "https://tiktok.com/@youthtransformers",
                responsiblePerson = "Kellia Mwizerwa",
                weekNumber = 38
            ),
            SocialPostEntity(
                title = "Sunday Study Reflection: Walking in the Spirit",
                platform = "YouTube",
                scheduledDate = "2026-09-21",
                status = "Approved",
                link = "https://youtube.com/watch?v=youthtransformers",
                responsiblePerson = "Kellia Mwizerwa",
                weekNumber = 38
            ),
            SocialPostEntity(
                title = "Weekly Scripture Wallpaper & Devotional",
                platform = "WhatsApp",
                scheduledDate = "2026-09-22",
                status = "Draft",
                responsiblePerson = "Kellia Mwizerwa",
                weekNumber = 38
            )
        )
        dao.insertSocialPosts(posts)

        // Social Interactions / Prayer requests
        val interactions = listOf(
            SocialInteractionEntity(
                type = "Prayer Request",
                senderName = "Mugisha Kevin",
                platform = "Instagram",
                message = "Please pray for my upcoming university examinations and for peace of mind.",
                status = "New",
                dateStr = "2026-09-17"
            ),
            SocialInteractionEntity(
                type = "New Person",
                senderName = "Sonia Uwamahoro",
                platform = "TikTok",
                message = "Hello! Where is your fellowship located in Kigali? Can I join this Friday?",
                status = "Responded",
                dateStr = "2026-09-16"
            ),
            SocialInteractionEntity(
                type = "Follow-up Request",
                senderName = "Alain Karangwa",
                platform = "WhatsApp",
                message = "I attended Sunday's service and would love to be connected to a Bible study cell.",
                status = "Referred to Member Care",
                dateStr = "2026-09-15"
            )
        )
        dao.insertSocialInteractions(interactions)

        // 5. Follow-up Cases (Cedrick - Member Care)
        val cases = listOf(
            FollowUpCaseEntity(
                memberId = 18L,
                memberName = "Gloria Mutoni",
                assignedPerson = "Cedrick Gisubizo",
                reason = "Family",
                dateOpened = "2026-09-10",
                priority = "Urgent",
                status = "In Progress",
                notes = "Bereavement support after loss of relative. Cell leader visited.",
                nextFollowUpDate = "2026-09-20",
                resolution = ""
            ),
            FollowUpCaseEntity(
                memberId = 3L,
                memberName = "Jean Claude Habimana",
                assignedPerson = "Cedrick Gisubizo",
                reason = "Attendance",
                dateOpened = "2026-09-08",
                priority = "Medium",
                status = "In Progress",
                notes = "Missed two consecutive Bible studies; called and encouraged him.",
                nextFollowUpDate = "2026-09-22",
                resolution = ""
            ),
            FollowUpCaseEntity(
                memberId = 7L,
                memberName = "Patrick Mugabo",
                assignedPerson = "Cedrick Gisubizo",
                reason = "Employment",
                dateOpened = "2026-09-05",
                priority = "Medium",
                status = "Open",
                notes = "Work shift overlaps with Friday studies. Exploring alternative cell timing.",
                nextFollowUpDate = "2026-09-25",
                resolution = ""
            ),
            FollowUpCaseEntity(
                memberId = 1L,
                memberName = "David Kwizera",
                assignedPerson = "Cedrick Gisubizo",
                reason = "General support",
                dateOpened = "2026-08-15",
                priority = "Low",
                status = "Resolved",
                notes = "Academic mentoring connection provided with elder brother.",
                nextFollowUpDate = "2026-10-01",
                resolution = "Case resolved successfully. David doing very well."
            )
        )
        dao.insertFollowUpCases(cases)

        // 6. Bible Studies (Liona Akaliza)
        val studies = listOf(
            BibleStudyEntity(
                topic = "The Fruit of the Spirit in Daily Youth Life",
                preacher = "Leo Benie Hategeka",
                host = "Liona Akaliza",
                dateStr = "2026-09-19",
                timeStr = "17:30 - 19:30",
                scriptureRefs = "Galatians 5:22-26, John 15:1-8",
                notes = "Focus on practical love, joy, and self-control in modern university & workplace.",
                isRecurring = true,
                curriculumTrack = "Spiritual Maturity",
                attendeesCount = 28
            ),
            BibleStudyEntity(
                topic = "Standing Firm in Faith: Daniel in Babylon",
                preacher = "Samuel Rukundo",
                host = "Sandrine Ishimwe",
                dateStr = "2026-09-12",
                timeStr = "17:30 - 19:30",
                scriptureRefs = "Daniel 1:8-21, 1 Peter 2:11-12",
                notes = "How to preserve Christian identity amid peer pressure.",
                isRecurring = true,
                curriculumTrack = "Character & Courage",
                attendeesCount = 31
            ),
            BibleStudyEntity(
                topic = "Prayer that Moves Mountains: The Lord's Model",
                preacher = "Bonheur Ndinzwe",
                host = "Aline Umutoni",
                dateStr = "2026-09-05",
                timeStr = "17:30 - 19:30",
                scriptureRefs = "Matthew 6:5-15, Philippians 4:6-7",
                notes = "Hands-on prayer session in small groups of 4.",
                isRecurring = true,
                curriculumTrack = "Prayer & Fellowship",
                attendeesCount = 29
            )
        )
        dao.insertBibleStudies(studies)

        // Attendance records
        val attendances = listOf(
            AttendanceRecordEntity(
                activityType = "Bible Study",
                activityTitle = "The Fruit of the Spirit",
                dateStr = "2026-09-12",
                memberId = 1L,
                memberName = "David Kwizera",
                status = "Present"
            ),
            AttendanceRecordEntity(
                activityType = "Bible Study",
                activityTitle = "The Fruit of the Spirit",
                dateStr = "2026-09-12",
                memberId = 2L,
                memberName = "Aline Umutoni",
                status = "Present"
            ),
            AttendanceRecordEntity(
                activityType = "Bible Study",
                activityTitle = "The Fruit of the Spirit",
                dateStr = "2026-09-12",
                memberId = 3L,
                memberName = "Jean Claude Habimana",
                status = "Excused",
                notes = "Work overtime"
            ),
            AttendanceRecordEntity(
                activityType = "Bible Study",
                activityTitle = "The Fruit of the Spirit",
                dateStr = "2026-09-12",
                memberId = 4L,
                memberName = "Grace Uwase",
                status = "Present"
            ),
            AttendanceRecordEntity(
                activityType = "Sunday Fellowship",
                activityTitle = "Youth Sunday Service",
                dateStr = "2026-09-14",
                memberId = 1L,
                memberName = "David Kwizera",
                status = "Present"
            )
        )
        dao.insertAttendanceRecords(attendances)

        // 7. Finance Transactions (Ebenezer Mugisha)
        val finances = listOf(
            FinancialTransactionEntity(
                dateStr = "2026-09-01",
                description = "Monthly Fellowship Offering & Tithes",
                category = "Donations",
                amount = 450000.0, // RWF
                type = "Income",
                recordedBy = "Ebenezer Mugisha",
                notes = "Envelope offerings counted by Ebenezer and Green London"
            ),
            FinancialTransactionEntity(
                dateStr = "2026-09-04",
                description = "Evangelism Transportation to Kimironko Outreach",
                category = "Transport",
                amount = 65000.0,
                type = "Expense",
                recordedBy = "Ebenezer Mugisha",
                notes = "Minibus hire for 22 youth members"
            ),
            FinancialTransactionEntity(
                dateStr = "2026-09-08",
                description = "Youth Transformers Sound Cable & Mic Stand Repair",
                category = "Equipment expenses",
                amount = 45000.0,
                type = "Expense",
                recordedBy = "Ebenezer Mugisha",
                notes = "Approved by Rene Cyubahiro"
            ),
            FinancialTransactionEntity(
                dateStr = "2026-09-10",
                description = "Community Youth Conference Fundraising Contribution",
                category = "Fundraising",
                amount = 320000.0,
                type = "Income",
                recordedBy = "Ebenezer Mugisha",
                notes = "Support from elder partners"
            ),
            FinancialTransactionEntity(
                dateStr = "2026-09-12",
                description = "Welfare & Hospital Fruit Basket for Bereaved Member",
                category = "Event expenses",
                amount = 30000.0,
                type = "Expense",
                recordedBy = "Ebenezer Mugisha",
                notes = "Coordinated through Member Care (Cedrick)"
            ),
            FinancialTransactionEntity(
                dateStr = "2026-09-15",
                description = "Refreshments for Bible Study Leaders Training",
                category = "Event expenses",
                amount = 28000.0,
                type = "Expense",
                recordedBy = "Ebenezer Mugisha",
                notes = "Water and tea snacks for 14 coordinators"
            )
        )
        dao.insertFinancialTransactions(finances)

        // 8. Projects (Rene Cyubahiro)
        val projects = listOf(
            ProjectEntity(
                name = "Kigali High School Gospel Outreach 2026",
                description = "Coordinated secondary school mission with music, personal testimonies, and youth discipleship books.",
                leader = "Rene Cyubahiro",
                startDate = "2026-08-01",
                deadline = "2026-10-30",
                budget = 800000.0,
                status = "Active",
                progressPercent = 68,
                teamMembers = "Rene, Bonheur, Kellia, David, Grace",
                tasksSummary = "5 of 8 milestones completed"
            ),
            ProjectEntity(
                name = "Media Room & Live Stream Equipment Upgrade",
                description = "Acquiring wireless clip mics, lighting rig, and high-speed audio interface for YouTube preaching recordings.",
                leader = "Rene Cyubahiro",
                startDate = "2026-09-01",
                deadline = "2026-11-15",
                budget = 1200000.0,
                status = "Planning",
                progressPercent = 35,
                teamMembers = "Rene, Kellia, Yves",
                tasksSummary = "Vendor quotations received"
            ),
            ProjectEntity(
                name = "Annual Discipleship Camp 2026 Preparation",
                description = "Weekend spiritual retreat for 60 youth in Musanze focusing on spiritual disciplines and character building.",
                leader = "Green London",
                startDate = "2026-07-15",
                deadline = "2026-12-20",
                budget = 2500000.0,
                status = "Active",
                progressPercent = 50,
                teamMembers = "Leo Benie, Green London, Cedrick, Ebenezer",
                tasksSummary = "Campsite booked and curriculum drafted"
            )
        )
        dao.insertProjects(projects)

        // 9. Equipment Inventory
        val equipment = listOf(
            EquipmentEntity(
                name = "Yamaha 12-Channel Audio Mixer",
                category = "Audio",
                quantity = 1,
                condition = "Excellent",
                location = "Main Sanctuary Booth",
                responsiblePerson = "Rene Cyubahiro",
                status = "Available",
                purchaseDate = "2024-05-10"
            ),
            EquipmentEntity(
                name = "Shure SM58 Wireless Microphones (Pair)",
                category = "Audio",
                quantity = 2,
                condition = "Good",
                location = "Mic Storage Case A",
                responsiblePerson = "Rene Cyubahiro",
                status = "In Use",
                borrowedBy = "Worship Team",
                returnDate = "2026-09-20"
            ),
            EquipmentEntity(
                name = "Sony Alpha Video Camera & Tripod Kit",
                category = "Video",
                quantity = 1,
                condition = "Excellent",
                location = "Media Production Cabinet",
                responsiblePerson = "Kellia Mwizerwa",
                status = "Borrowed",
                borrowedBy = "Kellia Mwizerwa",
                returnDate = "2026-09-19"
            ),
            EquipmentEntity(
                name = "Acoustic Guitar & Hard Case",
                category = "Instruments",
                quantity = 1,
                condition = "Good",
                location = "Worship Room",
                responsiblePerson = "Gervais Manzi",
                status = "Available"
            ),
            EquipmentEntity(
                name = "Epson Full HD Projector & Portable Screen",
                category = "IT",
                quantity = 1,
                condition = "Good",
                location = "Storage Room 2",
                responsiblePerson = "Rene Cyubahiro",
                status = "Available"
            )
        )
        dao.insertEquipmentList(equipment)

        // 10. Committee Reports & Tasks (Green London)
        val reports = listOf(
            CommitteeReportEntity(
                authorName = "BONHEUR NDINZWE",
                authorRole = "Level 1 Leader",
                reportType = "Weekly report",
                dateStr = "2026-09-15",
                accomplished = "Conducted 3 discipleship follow-up sessions. Checked memory verses for 6 students.",
                currentWork = "Teaching 'Christian Character & Integrity' topic.",
                challenges = "2 students commute far and arrived late.",
                supportNeeded = "Transportation fare assistance for Kimironko attendees.",
                nextSteps = "Organize prayer breakfast on Saturday.",
                prayerNotes = "Pray for spiritual perseverance of the new disciples.",
                status = "Reviewed"
            ),
            CommitteeReportEntity(
                authorName = "CEDRICK GISUBIZO",
                authorRole = "Member Care",
                reportType = "Weekly report",
                dateStr = "2026-09-16",
                accomplished = "Made 14 follow-up calls. Visited Gloria's family for condolence prayer.",
                currentWork = "Updating inactive member directory.",
                challenges = "4 phone numbers were temporarily unreachable.",
                supportNeeded = "Airtime allowance allocation.",
                nextSteps = "Set up weekly prayer buddy pairings.",
                prayerNotes = "Peace and restoration for grieving youth families.",
                status = "Submitted"
            )
        )
        dao.insertCommitteeReports(reports)

        val committeeTasks = listOf(
            CommitteeTaskEntity(
                title = "Submit Q3 Ministry Expenditure Breakdown",
                assignedToName = "EBENEZER MUGISHA",
                assignedToRole = "Accountant",
                deadline = "2026-09-25",
                priority = "High",
                isCompleted = false
            ),
            CommitteeTaskEntity(
                title = "Publish 3 Gospel Reels for Week 38",
                assignedToName = "KELLIA MWIZERWA",
                assignedToRole = "Social Media & Interaction",
                deadline = "2026-09-21",
                priority = "High",
                isCompleted = true
            ),
            CommitteeTaskEntity(
                title = "Inspect and Clean Audio Cables Before Sunday",
                assignedToName = "RENE CYUBAHIRO",
                assignedToRole = "Projects & Equipment",
                deadline = "2026-09-19",
                priority = "Normal",
                isCompleted = false
            )
        )
        dao.insertCommitteeTasks(committeeTasks)

        // 11. Evangelism & Outreach
        val outreaches = listOf(
            EvangelismEntity(
                title = "Kimironko Market & Taxi Park Saturday Outreach",
                dateStr = "2026-09-06",
                location = "Kimironko Commercial Center",
                teamLead = "Bonheur Ndinzwe",
                participantsCount = 18,
                peopleReachedCount = 142,
                followUpsCount = 24,
                newContactsCount = 17,
                testimonies = "Two university students prayed to receive Christ as Lord and Savior.",
                notes = "Shared bilingual gospel tracts in Kinyarwanda & English."
            ),
            EvangelismEntity(
                title = "Nyamirambo Youth Sports & Tracts Mission",
                dateStr = "2026-08-23",
                location = "Nyamirambo Community Ground",
                teamLead = "Leo Benie Hategeka",
                participantsCount = 25,
                peopleReachedCount = 210,
                followUpsCount = 38,
                newContactsCount = 29,
                testimonies = "Friendly basketball tournament opened deep gospel conversations with 4 youth gangs.",
                notes = "Connected 8 youth directly to the local cell."
            )
        )
        dao.insertEvangelismList(outreaches)

        // 12. Ministry Events
        val events = listOf(
            MinistryEventEntity(
                name = "Night of Praise & Intercession: 'Awaken'",
                description = "All-night youth prayer, acoustic worship, scripture recitation, and ministry commissioning.",
                dateStr = "2026-09-26",
                timeStr = "20:00 - 05:00",
                location = "Youth Transformers Fellowship Hall",
                organizer = "Leo Benie Hategeka",
                budget = 180000.0,
                expectedAttendance = 85,
                actualAttendance = 0,
                status = "Upcoming"
            ),
            MinistryEventEntity(
                name = "Youth Leaders Disciple-Makers Workshop",
                description = "Intensive leadership training on pastoral care, small group facilitation, and biblical counseling.",
                dateStr = "2026-10-03",
                timeStr = "09:00 - 15:00",
                location = "Youth Transformers Conference Room",
                organizer = "Green London",
                budget = 120000.0,
                expectedAttendance = 25,
                actualAttendance = 0,
                status = "Upcoming"
            )
        )
        dao.insertMinistryEvents(events)

        // 13. Announcements
        val announcements = listOf(
            AnnouncementEntity(
                title = "Welcome to Youth Transformers Discipleship Platform!",
                content = "Grace and peace to our beloved youth ministry family. We thank God for this digital home to strengthen fellowship, shepherd every soul with excellence, and coordinate our gospel mission.",
                targetAudience = "Entire Ministry",
                authorName = "Leo Benie Hategeka",
                dateStr = "2026-09-17",
                isImportant = true
            ),
            AnnouncementEntity(
                title = "Committee Coordination Meeting on Saturday 16:00",
                content = "All committee leads please bring your updated weekly reports and upcoming project budget forecasts.",
                targetAudience = "Committee",
                authorName = "Green London",
                dateStr = "2026-09-16",
                isImportant = true
            ),
            AnnouncementEntity(
                title = "Level 1 Scripture Memorization Recitation",
                content = "This Friday in Bible Study, Level 1 brethren will recite Romans 8:1-2 and Ephesians 2:8-10.",
                targetAudience = "Level 1",
                authorName = "Bonheur Ndinzwe",
                dateStr = "2026-09-15",
                isImportant = false
            )
        )
        dao.insertAnnouncements(announcements)

        // 14. Activity Logs
        val logs = listOf(
            ActivityLogEntity(
                actorUid = "uid_leo_hategeka",
                actorName = "LEO BENIE HATEGEKA",
                action = "Platform Initialized",
                resourceType = "System",
                resourceId = "sys_init",
                details = "Youth Transformers platform database initialized with role-based security.",
                result = "SUCCESS"
            ),
            ActivityLogEntity(
                actorUid = "uid_green_london",
                actorName = "GREEN LONDON",
                action = "Committee Task Assigned",
                resourceType = "Task",
                resourceId = "task_q3",
                details = "Assigned Q3 financial report preparation to Ebenezer Mugisha.",
                result = "SUCCESS"
            ),
            ActivityLogEntity(
                actorUid = "uid_cedrick_gisubizo",
                actorName = "CEDRICK GISUBIZO",
                action = "Follow-up Case Opened",
                resourceType = "MemberCare",
                resourceId = "case_gloria",
                details = "Opened pastoral care case for Gloria Mutoni.",
                result = "SUCCESS"
            )
        )
        dao.insertActivityLogs(logs)
    }
}
