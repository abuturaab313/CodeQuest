package com.example.domain.services

data class CurrencyTransaction(
  val amount: Int,
  val reason: String,
  val timestamp: Long = System.currentTimeMillis()
)

class CurrencyService {

  fun addCoins(currentBalance: Int, amount: Int): Int {
    return (currentBalance + amount).coerceAtLeast(0)
  }

  fun canAfford(currentBalance: Int, cost: Int): Boolean {
    return currentBalance >= cost
  }

  fun spendCoins(currentBalance: Int, cost: Int): Pair<Boolean, Int> {
    return if (canAfford(currentBalance, cost)) {
      Pair(true, currentBalance - cost)
    } else {
      Pair(false, currentBalance)
    }
  }
}
