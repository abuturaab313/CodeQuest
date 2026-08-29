package com.example.data.local

import com.example.data.models.EventEntity
import com.example.data.models.FriendEntity
import com.example.data.models.UnlockedCosmeticEntity
import com.example.data.models.LeaderboardCompetitorEntity
import com.example.data.models.DailyRewardClaimEntity

object InitialSocialData {

  fun defaultDailyRewards(): List<DailyRewardClaimEntity> {
    return listOf(
      DailyRewardClaimEntity(1, xpReward = 20, coinReward = 5, isClaimed = false),
      DailyRewardClaimEntity(2, xpReward = 30, coinReward = 10, isClaimed = false),
      DailyRewardClaimEntity(3, xpReward = 40, coinReward = 15, isClaimed = false),
      DailyRewardClaimEntity(4, xpReward = 50, coinReward = 20, isClaimed = false),
      DailyRewardClaimEntity(5, xpReward = 60, coinReward = 25, isClaimed = false),
      DailyRewardClaimEntity(6, xpReward = 80, coinReward = 30, isClaimed = false),
      DailyRewardClaimEntity(7, xpReward = 150, coinReward = 100, isClaimed = false)
    )
  }

  fun defaultEvents(): List<EventEntity> {
    return listOf(
      EventEntity(
        id = "evt_bug_hunter_1",
        title = "Bug Hunter Week",
        description = "Find and fix 15 debugging challenges or exercises in the quest.",
        currentProgress = 0,
        targetProgress = 15,
        xpReward = 500,
        coinReward = 100,
        badgeReward = "Bug Hunter",
        isCompleted = false,
        isClaimed = false,
        endEpochMs = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
      ),
      EventEntity(
        id = "evt_speed_demon_1",
        title = "Speed Coding Derby",
        description = "Submit 5 challenges with perfect solutions (0 hints used) to claim the crown.",
        currentProgress = 0,
        targetProgress = 5,
        xpReward = 400,
        coinReward = 80,
        badgeReward = "Speed Demon",
        isCompleted = false,
        isClaimed = false,
        endEpochMs = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000)
      )
    )
  }

  fun defaultFriends(): List<FriendEntity> {
    return listOf(
      FriendEntity("friend_1", "ByteSlayer", "robot_explorer", 1250, 12, "Gold", false),
      FriendEntity("friend_2", "Ada_Lovelace", "avatar_ninja", 3200, 45, "Obsidian", false),
      FriendEntity("friend_3", "BugSquasher", "avatar_coder", 850, 5, "Silver", false),
      FriendEntity("friend_4", "Pythonista", "avatar_dino", 150, 2, "Bronze", false),
      FriendEntity("friend_5", "CurieCode", "avatar_space", 2100, 24, "Crystal", false)
    )
  }

  fun defaultCompetitors(): List<LeaderboardCompetitorEntity> {
    val list = mutableListOf<LeaderboardCompetitorEntity>()
    
    // Bronze League (XP ranges from 50 to 300)
    val bronzeComp = listOf(
      "NoviceGamer" to 220, "RustyKey" to 180, "CodeNoob" to 140, "LazyLoop" to 95, 
      "VariableVibe" to 70, "SyntaxError" to 210, "TabsVsSpaces" to 150, "CoffeeBean" to 110, 
      "ChillCoder" to 60
    )
    bronzeComp.forEachIndexed { idx, (name, xp) ->
      list.add(LeaderboardCompetitorEntity("c_bronze_$idx", "Bronze", name, "avatar_dino", xp))
    }

    // Silver League (XP ranges from 400 to 1000)
    val silverComp = listOf(
      "BitShift" to 950, "ByteMe" to 820, "ArrayAce" to 710, "SortSensation" to 600, 
      "ScopeDrift" to 530, "FloatFlow" to 480, "SemiColon" to 440, "IndentationError" to 410, 
      "RecursionRider" to 420
    )
    silverComp.forEachIndexed { idx, (name, xp) ->
      list.add(LeaderboardCompetitorEntity("c_silver_$idx", "Silver", name, "avatar_coder", xp))
    }

    // Gold League (XP ranges from 1100 to 2200)
    val goldComp = listOf(
      "PyKing" to 2100, "Algorithmic" to 1950, "StackOverflow" to 1820, "PointerPete" to 1600, 
      "CyberKnight" to 1430, "GarbageCollect" to 1320, "NullPointer" to 1240, "O_of_N" to 1180, 
      "LambdaLover" to 1120
    )
    goldComp.forEachIndexed { idx, (name, xp) ->
      list.add(LeaderboardCompetitorEntity("c_gold_$idx", "Gold", name, "robot_explorer", xp))
    }

    // Crystal League (XP ranges from 2400 to 4500)
    val crystalComp = listOf(
      "ByteSlayer" to 4400, "CurieCode" to 4150, "MatrixNeo" to 3800, "HashMapHarry" to 3400, 
      "DataMiner" to 3100, "KernelCrash" to 2900, "LogicLord" to 2750, "BitWise" to 2600, 
      "BooleanBoss" to 2450
    )
    crystalComp.forEachIndexed { idx, (name, xp) ->
      list.add(LeaderboardCompetitorEntity("c_crystal_$idx", "Crystal", name, "avatar_space", xp))
    }

    // Obsidian League (XP ranges from 5000 to 12000)
    val obsidianComp = listOf(
      "Ada_Lovelace" to 11200, "LinusTorvalds" to 9800, "GuidoVanRossum" to 8500, "TuringTest" to 7400, 
      "BinarySearch" to 6800, "Wozniak" to 6200, "Knuth" to 5800, "StackAce" to 5400, 
      "CryptoKing" to 5100
    )
    obsidianComp.forEachIndexed { idx, (name, xp) ->
      list.add(LeaderboardCompetitorEntity("c_obsidian_$idx", "Obsidian", name, "avatar_ninja", xp))
    }

    return list
  }

  fun defaultCosmetics(): List<UnlockedCosmeticEntity> {
    return listOf(
      UnlockedCosmeticEntity("streak_freeze", "STREAK_FREEZE", quantity = 0, isEquipped = false),
      UnlockedCosmeticEntity("avatar_default", "AVATAR", quantity = 1, isEquipped = true),
      UnlockedCosmeticEntity("theme_default", "THEME", quantity = 1, isEquipped = true),
      UnlockedCosmeticEntity("title_default", "TITLE", quantity = 1, isEquipped = true)
    )
  }
}
