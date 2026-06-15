package com.example.randget11


import androidx.room.*

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE month = :month LIMIT 1")
    suspend fun getBudgetForMonth(month: String): Budget?
}