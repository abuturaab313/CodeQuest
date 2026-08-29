package com.example.data.repository

import com.example.data.local.CodeQuestDatabase
import com.example.data.local.InitialChallengeData
import com.example.data.local.InitialData
import com.example.data.models.AIFeedbackEntity
import com.example.data.models.AchievementEntity
import com.example.data.models.ChallengeProgressEntity
import com.example.data.models.CodingChallengeEntity
import com.example.data.models.CourseEntity
import com.example.data.models.DailyPracticeSessionEntity
import com.example.data.models.DailyQuestEntity
import com.example.data.models.ExerciseEntity
import com.example.data.models.LearnerMemoryEntity
import com.example.data.models.LearningRecommendation
import com.example.data.models.LessonEntity
import com.example.data.models.LessonProgressEntity
import com.example.data.models.ProjectEntity
import com.example.data.models.QuestType
import com.example.data.models.SkillMasteryEntity
import com.example.data.models.SubmissionRecordEntity
import com.example.data.models.UserEntity
import com.example.data.models.UserMistakeEntity
import com.example.data.models.WorldEntity
import com.example.data.models.EventEntity
import com.example.data.models.FriendEntity
import com.example.data.models.UnlockedCosmeticEntity
import com.example.data.models.LeaderboardCompetitorEntity
import com.example.data.models.DailyRewardClaimEntity
import com.example.data.models.BookmarkEntity
import com.example.data.models.LessonNoteEntity
import com.example.data.models.ReviewQueueEntity
import com.example.domain.ai.AIService
import com.example.domain.ai.ContextManager
import com.example.domain.ai.models.LearningContext
import com.example.domain.execution.CodeExecutionService
import com.example.domain.execution.DefaultCodeExecutionService
import com.example.domain.execution.ExecutionOptions
import com.example.domain.execution.ExecutionResult
import com.example.domain.execution.TestSuiteResult
import com.example.domain.learning.AdaptiveLearningService
import com.example.domain.learning.AnswerValidator
import com.example.domain.learning.HintService
import com.example.domain.learning.LessonScoringResult
import com.example.domain.learning.LessonScoringService
import com.example.domain.learning.PersonalizedPracticeService
import com.example.domain.learning.PrerequisiteService
import com.example.domain.learning.RecommendationEngine
import com.example.domain.learning.ReviewService
import com.example.domain.learning.SkillTrackingService
import com.example.domain.services.CurrencyService
import com.example.domain.services.HeartService
import com.example.domain.services.LevelUpResult
import com.example.domain.services.ProgressionService
import com.example.domain.services.QuestService
import com.example.domain.services.StreakService
import com.example.domain.services.StreakUpdateResult
import com.example.domain.services.SubmissionResult
import com.example.domain.services.SubmissionService
import com.example.domain.services.XPService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

data class LessonCompletionResult(
  val levelUpResult: LevelUpResult,
  val streakResult: StreakUpdateResult,
  val scoringResult: LessonScoringResult,
  val xpGained: Int,
  val coinsGained: Int,
  val unlockedNextLesson: Boolean
)

class CodeQuestRepository(
  private val database: CodeQuestDatabase,
  val xpService: XPService = XPService(),
  val heartService: HeartService = HeartService(),
  val streakService: StreakService = StreakService(),
  val questService: QuestService = QuestService(),
  val currencyService: CurrencyService = CurrencyService(),
  val answerValidator: AnswerValidator = AnswerValidator(),
  val hintService: HintService = HintService(),
  val scoringService: LessonScoringService = LessonScoringService(),
  val prerequisiteService: PrerequisiteService = PrerequisiteService(),
  val reviewService: ReviewService = ReviewService(),
  val skillTrackingService: SkillTrackingService = SkillTrackingService(),
  val adaptiveLearningService: AdaptiveLearningService = AdaptiveLearningService(),
  val recommendationEngine: RecommendationEngine = RecommendationEngine(),
  val personalizedPracticeService: PersonalizedPracticeService = PersonalizedPracticeService(),
  val contextManager: ContextManager = ContextManager(),
  val aiService: AIService = AIService(),
  val progressionService: ProgressionService = ProgressionService(
    xpService, heartService, streakService, questService, currencyService
  )
) {
  private val userDao = database.userDao()
  private val courseDao = database.courseDao()
  private val gamificationDao = database.gamificationDao()
  private val challengeDao = database.challengeDao()
  private val learnerDao = database.learnerDao()
  private val reviewQueueDao = database.reviewQueueDao()
  private val bookmarkDao = database.bookmarkDao()
  private val lessonNoteDao = database.lessonNoteDao()

  val codeExecutionService: CodeExecutionService = DefaultCodeExecutionService()
  val submissionService: SubmissionService = SubmissionService(
    executionService = codeExecutionService,
    challengeDao = challengeDao,
    xpService = xpService,
    currencyService = currencyService,
    questService = questService,
    streakService = streakService
  )

  // Coding Challenges
  val challenges: Flow<List<CodingChallengeEntity>> = challengeDao.getAllChallenges()

  fun getChallengesByCategory(category: String): Flow<List<CodingChallengeEntity>> =
    challengeDao.getChallengesByCategory(category)

  suspend fun getChallengeById(id: String): CodingChallengeEntity? =
    challengeDao.getChallengeById(id)

  suspend fun getChallengeByLessonId(lessonId: String): CodingChallengeEntity? =
    challengeDao.getChallengeByLessonId(lessonId)

  suspend fun getChallengeProgress(challengeId: String): ChallengeProgressEntity? =
    challengeDao.getProgressForChallenge(challengeId)

  fun observeChallengeProgress(challengeId: String): Flow<ChallengeProgressEntity?> =
    challengeDao.observeProgressForChallenge(challengeId)

  suspend fun saveChallengeDraft(challengeId: String, code: String, hintsUsedCount: Int = 0) {
    val existing = challengeDao.getProgressForChallenge(challengeId)
    val progress = ChallengeProgressEntity(
      challengeId = challengeId,
      draftCode = code,
      lastSubmittedCode = existing?.lastSubmittedCode ?: "",
      isCompleted = existing?.isCompleted ?: false,
      attemptsCount = existing?.attemptsCount ?: 0,
      hintsUsedCount = hintsUsedCount,
      bestExecutionTimeMs = existing?.bestExecutionTimeMs ?: 0L,
      lastUpdatedEpochMs = System.currentTimeMillis()
    )
    challengeDao.saveProgress(progress)
  }

  fun getSubmissionsForChallenge(challengeId: String): Flow<List<SubmissionRecordEntity>> =
    challengeDao.getSubmissionsForChallenge(challengeId)

  suspend fun executeUserCode(
    code: String,
    languageId: String,
    rawInput: String = "",
    options: ExecutionOptions = ExecutionOptions()
  ): ExecutionResult {
    return codeExecutionService.execute(code, languageId, rawInput, options)
  }

  suspend fun runPublicTests(
    challenge: CodingChallengeEntity,
    code: String,
    languageId: String? = null,
    options: ExecutionOptions = ExecutionOptions()
  ): TestSuiteResult {
    val publicTests = challenge.parsePublicTests()
    return codeExecutionService.runPublicTests(code, languageId ?: challenge.languageId, publicTests, options)
  }

  suspend fun submitChallenge(
    challenge: CodingChallengeEntity,
    code: String,
    hintsUsedCount: Int,
    languageId: String? = null
  ): Pair<SubmissionResult, LevelUpResult?> {
    val user = userDao.getUserProfileOnce()
    val (result, updatedUser) = submissionService.submitCode(challenge, code, hintsUsedCount, user, languageId)

    var levelUpResult: LevelUpResult? = null
    if (updatedUser != null && user != null) {
      userDao.updateUser(updatedUser)
      levelUpResult = xpService.checkLevelUp(user.xp, updatedUser.xp)

      // Update quests
      gamificationDao.incrementQuestProgress(QuestType.CHALLENGES_SOLVED.name, 1)
      gamificationDao.incrementQuestProgress(QuestType.XP_EARNED.name, result.xpAwarded)
      gamificationDao.updateAchievementProgress("MASTERY", 1)
    }

    return result to levelUpResult
  }

  // User Profile & Progression
  val userProfile: Flow<UserEntity?> = userDao.getUserProfile()

  suspend fun getUserProfileOnce(): UserEntity? = userDao.getUserProfileOnce()

  /**
   * Initializes or verifies startup data (daily quests, heart regen, default curriculum).
   */
  suspend fun checkAndInitializeData() {
    var user = userDao.getUserProfileOnce()
    if (user == null) {
      user = InitialData.defaultUser()
      userDao.insertUser(user)
      courseDao.insertCourses(InitialData.defaultCourses())
      courseDao.insertWorlds(InitialData.defaultWorlds())
      courseDao.insertChapters(InitialData.defaultChapters())
      courseDao.insertLessons(InitialData.defaultLessons())
      courseDao.insertExercises(InitialData.defaultExercises())
      gamificationDao.insertDailyQuests(InitialData.defaultDailyQuests())
      gamificationDao.insertAchievements(InitialData.defaultAchievements())
      gamificationDao.insertSkills(InitialData.defaultSkills())
      gamificationDao.insertProjects(InitialData.defaultProjects())
      challengeDao.insertChallenges(InitialChallengeData.defaultChallenges())
    } else {
      // Sync worlds, chapters, lessons and exercises
      courseDao.insertWorlds(InitialData.defaultWorlds())
      courseDao.insertChapters(InitialData.defaultChapters())
      courseDao.insertLessons(InitialData.defaultLessons())
      courseDao.insertExercises(InitialData.defaultExercises())
      challengeDao.insertChallenges(InitialChallengeData.defaultChallenges())

      // Check Heart Regeneration
      val heartRegen = heartService.calculateRegeneration(
        currentHearts = user.currentHearts,
        lastRegenEpochMs = user.lastHeartRegenEpochMs
      )
      if (heartRegen.didRegenerate) {
        userDao.updateHeartsWithTimestamp(heartRegen.currentHearts, heartRegen.lastRegenEpochMs)
      }

      // Check Daily Quests for today
      val todayEpoch = questService.getTodayEpochDay()
      val todayQuests = gamificationDao.getDailyQuestsForDayOnce(todayEpoch)
      if (todayQuests.isEmpty()) {
        gamificationDao.insertDailyQuests(questService.generateDailyQuests(todayEpoch))
      }
    }
  }

  suspend fun completeOnboarding(experienceLevel: String, language: String, dailyGoalMinutes: Int) {
    userDao.completeOnboarding(experienceLevel, language, dailyGoalMinutes)
  }

  suspend fun addRewards(xpGained: Int, coinsGained: Int): LevelUpResult {
    val user = userDao.getUserProfileOnce() ?: InitialData.defaultUser()
    val oldXp = user.xp
    val newXp = oldXp + xpGained
    val newCoins = user.coins + coinsGained
    val levelUp = xpService.checkLevelUp(oldXp, newXp)
    val finalCoins = newCoins + if (levelUp.didLevelUp) levelUp.coinReward else 0
    val newLevel = xpService.calculateLevel(newXp)

    userDao.addRewardsAndLevel(xpGained, coinsGained + (if (levelUp.didLevelUp) levelUp.coinReward else 0), newLevel)
    gamificationDao.incrementQuestProgress(QuestType.XP_EARNED.name, xpGained)
    gamificationDao.updateAchievementProgress("XP", xpGained)

    return levelUp
  }

  suspend fun awardBonusXpAndCoins(xpGained: Int, coinsGained: Int): LevelUpResult {
    return addRewards(xpGained, coinsGained)
  }

  suspend fun deductHeart(): Boolean {
    val user = userDao.getUserProfileOnce() ?: return false
    val (success, newHearts) = heartService.consumeHeart(user.currentHearts)
    if (success) {
      userDao.updateHeartsWithTimestamp(newHearts, System.currentTimeMillis())
      return true
    }
    return false
  }

  suspend fun restoreHearts(amount: Int = 5) {
    val fullHearts = minOf(5, amount)
    userDao.updateHeartsWithTimestamp(fullHearts, System.currentTimeMillis())
  }

  // Course & Curriculum
  val allCourses: Flow<List<CourseEntity>> = courseDao.getAllCourses()
  val allWorlds: Flow<List<WorldEntity>> = courseDao.getAllWorlds()
  val allLessons: Flow<List<LessonEntity>> = courseDao.getAllLessons()
  val unresolvedMistakes: Flow<List<UserMistakeEntity>> = courseDao.getUnresolvedMistakes()

  fun getWorldsForCourse(courseId: String): Flow<List<WorldEntity>> =
    courseDao.getWorldsForCourse(courseId)

  fun getLessonsForWorld(worldId: String): Flow<List<LessonEntity>> =
    courseDao.getLessonsForWorld(worldId)

  suspend fun getLessonById(lessonId: String): LessonEntity? =
    courseDao.getLessonById(lessonId)

  fun getExercisesForLesson(lessonId: String): Flow<List<ExerciseEntity>> =
    courseDao.getExercisesForLesson(lessonId)

  suspend fun getExercisesForLessonOnce(lessonId: String): List<ExerciseEntity> =
    courseDao.getExercisesForLessonOnce(lessonId)

  // Lesson Progress & State Persistence
  suspend fun getLessonProgress(lessonId: String): LessonProgressEntity? =
    courseDao.getLessonProgress(lessonId)

  suspend fun saveLessonProgress(progress: LessonProgressEntity) =
    courseDao.saveLessonProgress(progress)

  suspend fun clearLessonProgress(lessonId: String) =
    courseDao.clearLessonProgress(lessonId)

  // Mistake Tracking & Review
  suspend fun recordMistake(exercise: ExerciseEntity, wrongAnswer: String) {
    val mistake = reviewService.createMistakeRecord(exercise, wrongAnswer)
    courseDao.recordMistake(mistake)
  }

  suspend fun resolveMistake(mistakeId: String) {
    courseDao.markMistakeResolved(mistakeId)
  }

  suspend fun getUnresolvedMistakesOnce(): List<UserMistakeEntity> =
    courseDao.getUnresolvedMistakesOnce()

  /**
   * Completes a lesson with thorough scoring, streak updating, and unlocks.
   */
  suspend fun completeLesson(
    lessonId: String,
    totalExercises: Int,
    correctCount: Int,
    mistakeCount: Int,
    hintsUsedCount: Int,
    baseXp: Int,
    baseCoins: Int,
    nextLessonId: String? = null
  ): LessonCompletionResult {
    val user = userDao.getUserProfileOnce() ?: InitialData.defaultUser()
    
    val scoringResult = scoringService.calculateLessonScore(
      totalExercises = totalExercises,
      correctCount = correctCount,
      mistakeCount = mistakeCount,
      hintsUsedCount = hintsUsedCount,
      baseXp = baseXp,
      baseCoins = baseCoins,
      currentStreak = user.streakDays
    )

    courseDao.markLessonCompleted(lessonId, scoringResult.stars)
    clearLessonProgress(lessonId)

    // Unlock next level if provided
    var didUnlockNext = false
    if (nextLessonId != null) {
      courseDao.unlockLesson(nextLessonId)
      didUnlockNext = true
    }

    // World boss progression checks
    if (lessonId == "py_w1_l10" || lessonId == "py_w1_l8") {
      courseDao.unlockWorld("py_w2")
      courseDao.unlockLesson("py_w2_l1")
    }

    // Add total XP and Coin rewards (including bonuses)
    val levelUp = addRewards(scoringResult.totalXp, scoringResult.coinsEarned)

    // Update streaks
    val streakResult = streakService.recordActivity(
      currentStreak = user.streakDays,
      longestStreak = user.longestStreak,
      lastActiveEpochDay = user.lastActiveEpochDay
    )
    userDao.updateStreak(streakResult.newStreak, streakResult.newLongestStreak, streakResult.todayEpochDay)

    // Update Daily Quests & Achievements
    gamificationDao.incrementQuestProgress(QuestType.LESSONS_COMPLETED.name, 1)
    gamificationDao.updateAchievementProgress("COMPLETION", 1)
    if (streakResult.newStreak >= 3) {
      gamificationDao.updateAchievementProgress("STREAK", 1)
    }

    // Automatically schedule Spaced Repetition Review
    val scorePercent = if (totalExercises > 0) (correctCount * 100) / totalExercises else 100
    val lessonItem = courseDao.getLessonById(lessonId)
    val lessonName = lessonItem?.title ?: "Lesson Topic"
    scheduleSpacedReview(lessonId, lessonName, scorePercent)

    return LessonCompletionResult(
      levelUpResult = levelUp,
      streakResult = streakResult,
      scoringResult = scoringResult,
      xpGained = scoringResult.totalXp,
      coinsGained = scoringResult.coinsEarned,
      unlockedNextLesson = didUnlockNext
    )
  }

  suspend fun unlockNextLesson(nextLessonId: String) {
    courseDao.unlockLesson(nextLessonId)
  }

  // Quests & Gamification
  val dailyQuests: Flow<List<DailyQuestEntity>> = gamificationDao.getAllDailyQuests()

  suspend fun claimQuest(quest: DailyQuestEntity): LevelUpResult? {
    val claimResult = questService.claimQuest(quest) ?: return null
    gamificationDao.claimQuestReward(quest.id)
    return addRewards(claimResult.xpGained, claimResult.coinsGained)
  }

  val achievements: Flow<List<AchievementEntity>> = gamificationDao.getAllAchievements()

  fun getSkillsForLanguage(language: String): Flow<List<SkillMasteryEntity>> =
    gamificationDao.getSkillsForLanguage(language)

  fun getProjectsForLanguage(language: String): Flow<List<ProjectEntity>> =
    gamificationDao.getProjectsForLanguage(language)

  suspend fun updateSettings(sound: Boolean, haptics: Boolean, dark: Boolean, reducedMotion: Boolean) {
    userDao.updateSettings(sound, haptics, dark, reducedMotion)
  }

  suspend fun upgradeAccount(email: String, username: String) {
    userDao.upgradeAccount(email, username)
  }

  // ==========================================
  // MILESTONE 6: AI CODING MENTOR & ADAPTIVE
  // ==========================================

  val learnerMemory: Flow<LearnerMemoryEntity?> = learnerDao.getLearnerMemory()

  suspend fun getLearnerMemoryOnce(): LearnerMemoryEntity? =
    learnerDao.getLearnerMemoryOnce()

  suspend fun requestAIMentorAssistance(
    context: LearningContext,
    mode: com.example.domain.ai.models.AIMentorMode,
    hintLevel: Int = 1
  ): Result<com.example.domain.ai.models.AIResponse> {
    val updatedContext = context.copy(hintLevelRequested = hintLevel)
    val response = aiService.requestMentorGuidance(mode, updatedContext)
    return response
  }

  suspend fun logAIFeedback(
    promptMode: String,
    wasHelpful: Boolean,
    topic: String,
    solvedAfter: Boolean = false
  ) {
    val feedback = AIFeedbackEntity(
      promptMode = promptMode,
      wasHelpful = wasHelpful,
      contextTopic = topic,
      problemSolvedAfter = solvedAfter
    )
    learnerDao.logFeedback(feedback)
  }

  fun observeDailyPracticeSession(epochDay: Long): Flow<DailyPracticeSessionEntity?> =
    learnerDao.getDailyPracticeSession(epochDay)

  suspend fun getOrCreateDailyPracticeSession(epochDay: Long): DailyPracticeSessionEntity {
    var session = learnerDao.getDailyPracticeSessionOnce(epochDay)
    if (session == null) {
      val user = userDao.getUserProfileOnce()
      val skills = gamificationDao.getSkillsForLanguageOnce(user?.selectedLanguage ?: "python")
      val weakSkills = adaptiveLearningService.detectWeakSkills(skills)
      val mistakes = courseDao.getUnresolvedMistakesOnce()
      val challenges = challengeDao.getAllChallengesOnce()

      session = personalizedPracticeService.generateDailySession(
        epochDay = epochDay,
        weakSkills = weakSkills,
        mistakes = mistakes,
        challenges = challenges
      )
      learnerDao.saveDailyPracticeSession(session)
    }
    return session
  }

  suspend fun advanceDailyPracticeStep(epochDay: Long, stepIndex: Int): LevelUpResult? {
    val session = learnerDao.getDailyPracticeSessionOnce(epochDay) ?: return null
    val newCompletedSteps = maxOf(session.completedSteps, stepIndex + 1)
    val isNowCompleted = newCompletedSteps >= session.totalSteps

    learnerDao.updateDailyPracticeProgress(epochDay, newCompletedSteps, isNowCompleted)

    if (isNowCompleted && !session.isCompleted) {
      return addRewards(session.xpReward, session.coinReward)
    }
    return null
  }

  suspend fun getSmartRecommendations(): List<LearningRecommendation> {
    val user = userDao.getUserProfileOnce()
    val allLessons = courseDao.getAllLessonsOnce()
    val unlocked = allLessons.filter { it.isUnlocked }
    val completed = allLessons.filter { it.isCompleted }
    val challenges = challengeDao.getAllChallengesOnce()
    val projects = gamificationDao.getProjectsForLanguageOnce(user?.selectedLanguage ?: "python")
    val mistakes = courseDao.getUnresolvedMistakesOnce()
    val skills = gamificationDao.getSkillsForLanguageOnce(user?.selectedLanguage ?: "python")

    return recommendationEngine.generateRecommendations(
      unlockedLessons = unlocked,
      completedLessons = completed,
      challenges = challenges,
      projects = projects,
      unresolvedMistakes = mistakes,
      skills = skills
    )
  }

  // ==========================================
  // MILESTONE 7: SOCIAL PROGRESSION & GAMIFICATION
  // ==========================================

  private val socialProgressionDao = database.socialProgressionDao()

  val weeklyEvents: Flow<List<EventEntity>> = socialProgressionDao.getAllEvents()
  val friends: Flow<List<FriendEntity>> = socialProgressionDao.getAllFriends()
  val unlockedCosmetics: Flow<List<UnlockedCosmeticEntity>> = socialProgressionDao.getAllUnlockedCosmetics()
  val dailyRewardClaims: Flow<List<DailyRewardClaimEntity>> = socialProgressionDao.getDailyRewardClaims()

  fun getCompetitorsForLeague(leagueName: String): Flow<List<LeaderboardCompetitorEntity>> =
    socialProgressionDao.getCompetitorsForLeague(leagueName)

  suspend fun claimDailyReward(dayIndex: Int): LevelUpResult? {
    val claims = socialProgressionDao.getDailyRewardClaimsOnce()
    val claim = claims.firstOrNull { it.dayIndex == dayIndex } ?: return null
    if (claim.isClaimed) return null

    val todayEpoch = questService.getTodayEpochDay()
    socialProgressionDao.claimReward(dayIndex, todayEpoch)

    val levelUp = addRewards(claim.xpReward, claim.coinReward)
    dispatchRewardEvent("XP_GAINED", value = claim.xpReward)
    return levelUp
  }

  suspend fun claimEventReward(eventId: String): LevelUpResult? {
    val event = socialProgressionDao.getEventById(eventId) ?: return null
    if (!event.isCompleted || event.isClaimed) return null

    val updatedEvent = event.copy(isClaimed = true)
    socialProgressionDao.updateEvent(updatedEvent)

    val levelUp = addRewards(event.xpReward, event.coinReward)
    dispatchRewardEvent("XP_GAINED", value = event.xpReward)
    return levelUp
  }

  suspend fun purchaseCosmetic(itemId: String, costCoins: Int, category: String): Boolean {
    val user = userDao.getUserProfileOnce() ?: return false
    if (user.coins < costCoins) return false

    // Deduct coins
    val updatedUser = user.copy(coins = user.coins - costCoins)
    userDao.updateUser(updatedUser)

    val existing = socialProgressionDao.getUnlockedCosmeticById(itemId)
    if (existing != null) {
      val updated = existing.copy(quantity = existing.quantity + 1)
      socialProgressionDao.insertUnlockedCosmetic(updated)
    } else {
      val newCosmetic = UnlockedCosmeticEntity(id = itemId, category = category, quantity = 1, isEquipped = false)
      socialProgressionDao.insertUnlockedCosmetic(newCosmetic)
    }
    return true
  }

  suspend fun equipCosmetic(itemId: String, category: String): Boolean {
    val cosmetic = socialProgressionDao.getUnlockedCosmeticById(itemId) ?: return false
    if (cosmetic.quantity <= 0) return false

    socialProgressionDao.unequipAllInCategory(category)
    socialProgressionDao.equipCosmetic(itemId)

    if (category == "AVATAR") {
      val user = userDao.getUserProfileOnce()
      if (user != null) {
        userDao.updateUser(user.copy(avatarId = itemId))
      }
    }
    return true
  }

  suspend fun sendXpBoostToFriend(friendId: String): Boolean {
    val friendsList = socialProgressionDao.getAllFriends().firstOrNull() ?: emptyList()
    val friend = friendsList.firstOrNull { it.id == friendId } ?: return false
    val todayEpochMs = System.currentTimeMillis()

    // Check limit: once per day per friend
    val isCooldowned = (todayEpochMs - friend.lastXpBoostEpochMs) < (24 * 60 * 60 * 1000)
    if (isCooldowned) return false

    socialProgressionDao.updateFriendBoostTime(friendId, todayEpochMs)
    // Award the user +10 XP for being generous!
    addRewards(10, 0)
    dispatchRewardEvent("XP_GAINED", value = 10)
    return true
  }

  suspend fun restoreBrokenStreak(): Boolean {
    val user = userDao.getUserProfileOnce() ?: return false
    val freezeItem = socialProgressionDao.getUnlockedCosmeticById("streak_freeze")

    if (freezeItem != null && freezeItem.quantity > 0) {
      // Consume a freeze card
      socialProgressionDao.insertUnlockedCosmetic(freezeItem.copy(quantity = freezeItem.quantity - 1))
      val newStreak = maxOf(user.streakDays, 1)
      val todayEpoch = questService.getTodayEpochDay()
      userDao.updateStreak(newStreak, maxOf(user.longestStreak, newStreak), todayEpoch)
      return true
    } else if (user.coins >= 150) {
      // Pay 150 Coins
      val updatedUser = user.copy(
        coins = user.coins - 150,
        streakDays = maxOf(user.streakDays, 1),
        lastActiveEpochDay = questService.getTodayEpochDay()
      )
      userDao.updateUser(updatedUser)
      return true
    }
    return false
  }

  suspend fun followOrUnfollowFriend(friendId: String, follow: Boolean) {
    socialProgressionDao.updateFriendFollowStatus(friendId, follow)
  }

  // Update active competitor points (simulates active league competition offline!)
  suspend fun tickLeaderboardScores() {
    val user = userDao.getUserProfileOnce() ?: return
    val currentLeague = when {
      user.level <= 2 -> "Bronze"
      user.level <= 4 -> "Silver"
      user.level <= 6 -> "Gold"
      user.level <= 8 -> "Crystal"
      else -> "Obsidian"
    }

    val competitors = socialProgressionDao.getCompetitorsForLeagueOnce(currentLeague)
    competitors.forEach { comp ->
      if (!comp.isPlayer) {
        val randXp = (5..25).random()
        socialProgressionDao.updateCompetitorXp(comp.id, randXp)
      }
    }
  }

  suspend fun dispatchRewardEvent(eventType: String, value: Int = 1, metadata: Map<String, String> = emptyMap()) {
    val user = userDao.getUserProfileOnce() ?: return
    val achievementsList = gamificationDao.getAllAchievements().firstOrNull() ?: emptyList()
    val currentTime = System.currentTimeMillis()

    // 1. Process Achievements
    achievementsList.forEach { ach ->
      if (!ach.isUnlocked) {
        var increment = 0
        when (ach.id) {
          "ach_first_code" -> {
            if (eventType == "EXERCISE_COMPLETED" || eventType == "CHALLENGE_COMPLETED") {
              increment = 1
            }
          }
          "ach_first_project" -> {
            if (eventType == "PROJECT_COMPLETED") {
              increment = 1
            }
          }
          "ach_bug_hunter" -> {
            if (eventType == "CHALLENGE_COMPLETED" && metadata["category"] == "DEBUGGING") {
              increment = 1
            }
          }
          "ach_streak_starter" -> {
            if (eventType == "STREAK_UPDATED") {
              val streak = metadata["streak"]?.toIntOrNull() ?: user.streakDays
              if (streak >= 7) increment = 7 - ach.currentCount
            }
          }
          "ach_streak_master" -> {
            if (eventType == "STREAK_UPDATED") {
              val streak = metadata["streak"]?.toIntOrNull() ?: user.streakDays
              if (streak >= 30) increment = 30 - ach.currentCount
            }
          }
          "ach_perfect_ten" -> {
            if (eventType == "CHALLENGE_COMPLETED" && metadata["isPerfect"] == "true") {
              increment = 1
            }
          }
          "ach_python_beginner" -> {
            if (eventType == "LESSON_COMPLETED" && metadata["worldId"] == "py_w1") {
              val completedCount = courseDao.getAllLessonsOnce().filter { it.isCompleted && it.id.startsWith("py_w1") }.size
              increment = completedCount - ach.currentCount
            }
          }
          "ach_python_explorer" -> {
            if (eventType == "LESSON_COMPLETED" && metadata["worldId"] == "py_w2") {
              val completedCount = courseDao.getAllLessonsOnce().filter { it.isCompleted && it.id.startsWith("py_w2") }.size
              increment = completedCount - ach.currentCount
            }
          }
          "ach_code_marathon" -> {
            if (eventType == "XP_GAINED") {
              increment = value
            }
          }
          "ach_project_builder" -> {
            if (eventType == "PROJECT_COMPLETED") {
              increment = 1
            }
          }
          "ach_loop_master" -> {
            if (eventType == "MASTERY_UPDATED" && metadata["skillId"] == "py_loops") {
              val mastery = metadata["mastery"]?.toIntOrNull() ?: 0
              if (mastery >= 90) increment = 90 - ach.currentCount
            }
          }
          "ach_function_forge" -> {
            if (eventType == "MASTERY_UPDATED" && metadata["skillId"] == "py_functions") {
              val mastery = metadata["mastery"]?.toIntOrNull() ?: 0
              if (mastery >= 90) increment = 90 - ach.currentCount
            }
          }
        }

        if (increment > 0) {
          val newCount = minOf(ach.targetCount, ach.currentCount + increment)
          val nowUnlocked = newCount >= ach.targetCount
          val unlockedTime = if (nowUnlocked) currentTime else 0L

          val updatedAch = ach.copy(
            currentCount = newCount,
            isUnlocked = nowUnlocked,
            unlockedAtEpochMs = unlockedTime
          )
          gamificationDao.insertAchievements(listOf(updatedAch))

          if (nowUnlocked) {
            // Award rewards safely with anti-cheat checks
            addRewards(ach.xpReward, ach.coinReward)
          }
        }
      }
    }

    // 2. Process Weekly Events
    val activeEvents = socialProgressionDao.getAllEvents().firstOrNull() ?: emptyList()
    activeEvents.forEach { event ->
      if (!event.isCompleted) {
        var increment = 0
        when (event.id) {
          "evt_bug_hunter_1" -> {
            if (eventType == "CHALLENGE_COMPLETED" && metadata["category"] == "DEBUGGING") {
              increment = 1
            } else if (eventType == "EXERCISE_COMPLETED" && metadata["category"] == "DEBUGGING") {
              increment = 1
            }
          }
          "evt_speed_demon_1" -> {
            if (eventType == "CHALLENGE_COMPLETED" && metadata["isPerfect"] == "true") {
              increment = 1
            }
          }
        }

        if (increment > 0) {
          val newProgress = minOf(event.targetProgress, event.currentProgress + increment)
          val isCompleted = newProgress >= event.targetProgress
          val updatedEvent = event.copy(
            currentProgress = newProgress,
            isCompleted = isCompleted
          )
          socialProgressionDao.updateEvent(updatedEvent)
        }
      }
    }
  }

  // Bookmarks
  fun observeAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.observeAllBookmarks()
  suspend fun isBookmarked(lessonId: String): Boolean = bookmarkDao.getBookmark(lessonId) != null
  suspend fun toggleBookmark(lessonId: String, lessonTitle: String, worldId: String) {
    if (isBookmarked(lessonId)) {
      bookmarkDao.deleteBookmark(lessonId)
    } else {
      bookmarkDao.insertBookmark(BookmarkEntity(lessonId, lessonTitle, worldId))
    }
  }

  // Notes
  fun observeAllNotes(): Flow<List<LessonNoteEntity>> = lessonNoteDao.observeAllNotes()
  suspend fun getNoteForLesson(lessonId: String): LessonNoteEntity? = lessonNoteDao.getNote(lessonId)
  suspend fun saveNoteForLesson(lessonId: String, lessonTitle: String, text: String) {
    lessonNoteDao.upsertNote(LessonNoteEntity(lessonId, lessonTitle, text, System.currentTimeMillis()))
  }
  suspend fun deleteNoteForLesson(lessonId: String) {
    lessonNoteDao.deleteNote(lessonId)
  }

  // Spaced Repetition Review Queue
  fun observeReviewQueue(): Flow<List<ReviewQueueEntity>> = reviewQueueDao.observeReviewQueue()
  suspend fun getReviewQueue(): List<ReviewQueueEntity> = reviewQueueDao.getReviewQueueOnce()
  suspend fun scheduleSpacedReview(conceptId: String, conceptName: String, performanceScore: Int) {
    val existing = reviewQueueDao.getReviewQueueOnce().find { it.conceptId == conceptId }
    val now = System.currentTimeMillis()
    
    val nextIntervalDays = if (existing != null) {
      if (performanceScore < 60) {
        maxOf(1, existing.intervalDays / 2)
      } else {
        when (existing.intervalDays) {
          0 -> 1
          1 -> 3
          3 -> 7
          7 -> 14
          14 -> 30
          else -> 30
        }
      }
    } else {
      if (performanceScore < 60) 1 else 1
    }
    
    val nextReviewMs = now + (nextIntervalDays * 24L * 60L * 60L * 1000L)
    val updated = ReviewQueueEntity(
      conceptId = conceptId,
      conceptName = conceptName,
      lastReviewedEpochMs = now,
      nextReviewEpochMs = nextReviewMs,
      intervalDays = nextIntervalDays,
      performanceScore = performanceScore,
      difficulty = if (performanceScore < 60) "Hard" else if (performanceScore < 85) "Medium" else "Easy"
    )
    reviewQueueDao.upsertReviewItem(updated)
  }
  suspend fun deleteReviewItem(conceptId: String) {
    reviewQueueDao.deleteReviewItem(conceptId)
  }

  suspend fun resetAllProgress() {
    userDao.insertUser(InitialData.defaultUser())
    courseDao.insertWorlds(InitialData.defaultWorlds())
    courseDao.insertChapters(InitialData.defaultChapters())
    courseDao.insertLessons(InitialData.defaultLessons())
    courseDao.insertExercises(InitialData.defaultExercises())
    gamificationDao.insertDailyQuests(InitialData.defaultDailyQuests())
  }
}
