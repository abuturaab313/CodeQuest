package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.AchievementEntity
import com.example.data.models.ChallengeProgressEntity
import com.example.data.models.ChapterEntity
import com.example.data.models.CodingChallengeEntity
import com.example.data.models.CourseEntity
import com.example.data.models.DailyQuestEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.LessonEntity
import com.example.data.models.LessonProgressEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.ProjectFileEntity
import com.example.data.models.ProjectProgressEntity
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.SubmissionRecordEntity
import com.example.data.models.UserEntity
import com.example.data.models.UserMistakeEntity
import com.example.data.models.AIFeedbackEntity
import com.example.data.models.DailyPracticeSessionEntity
import com.example.data.models.LearnerMemoryEntity
import com.example.data.models.WorldEntity
import com.example.data.models.EventEntity
import com.example.data.models.FriendEntity
import com.example.data.models.UnlockedCosmeticEntity
import com.example.data.models.LeaderboardCompetitorEntity
import com.example.data.models.DailyRewardClaimEntity
import com.example.data.models.ReviewQueueEntity
import com.example.data.models.BookmarkEntity
import com.example.data.models.LessonNoteEntity
import com.example.data.local.ReviewQueueDao
import com.example.data.local.BookmarkDao
import com.example.data.local.LessonNoteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    CourseEntity::class,
    WorldEntity::class,
    ChapterEntity::class,
    LessonEntity::class,
    ExerciseEntity::class,
    DailyQuestEntity::class,
    AchievementEntity::class,
    SkillMasteryEntity::class,
    ProjectEntity::class,
    ProjectFileEntity::class,
    ProjectProgressEntity::class,
    LessonProgressEntity::class,
    UserMistakeEntity::class,
    CodingChallengeEntity::class,
    ChallengeProgressEntity::class,
    SubmissionRecordEntity::class,
    LearnerMemoryEntity::class,
    AIFeedbackEntity::class,
    DailyPracticeSessionEntity::class,
    EventEntity::class,
    FriendEntity::class,
    UnlockedCosmeticEntity::class,
    LeaderboardCompetitorEntity::class,
    DailyRewardClaimEntity::class,
    ReviewQueueEntity::class,
    BookmarkEntity::class,
    LessonNoteEntity::class
  ],
  version = 8,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CodeQuestDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun courseDao(): CourseDao
  abstract fun gamificationDao(): GamificationDao
  abstract fun challengeDao(): ChallengeDao
  abstract fun projectDao(): ProjectDao
  abstract fun learnerDao(): LearnerDao
  abstract fun learnerMemoryDao(): LearnerMemoryDao
  abstract fun socialProgressionDao(): SocialProgressionDao
  abstract fun reviewQueueDao(): ReviewQueueDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun lessonNoteDao(): LessonNoteDao

  companion object {
    @Volatile
    private var INSTANCE: CodeQuestDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): CodeQuestDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CodeQuestDatabase::class.java,
          "codequest_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(scope))
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
            populateDatabase(database)
          }
        }
      }

      suspend fun populateDatabase(db: CodeQuestDatabase) {
        db.userDao().insertUser(InitialData.defaultUser())
        db.courseDao().insertCourses(InitialData.defaultCourses())
        db.courseDao().insertWorlds(InitialData.defaultWorlds())
        db.courseDao().insertChapters(InitialData.defaultChapters())
        db.courseDao().insertLessons(InitialData.defaultLessons())
        db.courseDao().insertExercises(InitialData.defaultExercises())
        db.gamificationDao().insertDailyQuests(InitialData.defaultDailyQuests())
        db.gamificationDao().insertAchievements(InitialData.defaultAchievements())
        db.gamificationDao().insertSkills(InitialData.defaultSkills())
        db.gamificationDao().insertProjects(InitialData.defaultProjects())
        db.challengeDao().insertChallenges(InitialChallengeData.defaultChallenges())
        db.socialProgressionDao().insertDailyRewardClaims(InitialSocialData.defaultDailyRewards())
        db.socialProgressionDao().insertEvents(InitialSocialData.defaultEvents())
        db.socialProgressionDao().insertFriends(InitialSocialData.defaultFriends())
        db.socialProgressionDao().insertCompetitors(InitialSocialData.defaultCompetitors())
        db.socialProgressionDao().insertUnlockedCosmetics(InitialSocialData.defaultCosmetics())
      }
    }
  }
}
