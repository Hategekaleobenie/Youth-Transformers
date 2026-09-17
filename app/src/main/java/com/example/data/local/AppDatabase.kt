package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        MemberEntity::class,
        DiscipleshipEntity::class,
        ProgressionRecommendationEntity::class,
        SocialPostEntity::class,
        SocialInteractionEntity::class,
        FollowUpCaseEntity::class,
        BibleStudyEntity::class,
        AttendanceRecordEntity::class,
        FinancialTransactionEntity::class,
        ProjectEntity::class,
        EquipmentEntity::class,
        CommitteeReportEntity::class,
        CommitteeTaskEntity::class,
        EvangelismEntity::class,
        MinistryEventEntity::class,
        AnnouncementEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): YouthTransformersDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "youth_transformers_db"
                ).addCallback(DatabaseCallback(scope))
                 .fallbackToDestructiveMigration(dropAllTables = true)
                 .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        SeedDataProvider.populateDatabase(database.dao())
                    }
                }
            }
        }
    }
}
