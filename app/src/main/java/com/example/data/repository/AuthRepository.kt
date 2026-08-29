package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface AuthState {
  data object Loading : AuthState
  data class Unauthenticated(val message: String? = null) : AuthState
  data class Authenticated(val user: UserEntity, val isGuest: Boolean) : AuthState
  data class Error(val errorMessage: String) : AuthState
}

class AuthRepository(private val userDao: UserDao) {
  val authState: Flow<AuthState> = userDao.getUserProfile().map { user ->
    if (user == null) {
      AuthState.Unauthenticated()
    } else {
      AuthState.Authenticated(user = user, isGuest = user.isGuest)
    }
  }

  suspend fun signInAsGuest(experience: String, language: String, goal: Int): UserEntity {
    val existing = userDao.getUserProfileOnce()
    val guestUser = existing?.copy(
      experienceLevel = experience,
      selectedLanguage = language,
      dailyGoalMinutes = goal,
      hasCompletedOnboarding = true,
      isGuest = true
    ) ?: UserEntity(
      experienceLevel = experience,
      selectedLanguage = language,
      dailyGoalMinutes = goal,
      hasCompletedOnboarding = true,
      isGuest = true
    )
    userDao.insertUser(guestUser)
    return guestUser
  }

  suspend fun upgradeAccount(email: String, username: String): Boolean {
    userDao.upgradeAccount(email, username)
    return true
  }

  suspend fun switchLanguage(language: String) {
    val user = userDao.getUserProfileOnce() ?: return
    userDao.updateUser(user.copy(selectedLanguage = language))
  }
}
