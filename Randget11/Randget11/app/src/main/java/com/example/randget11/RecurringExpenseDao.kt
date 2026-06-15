package com.example.randget11

import androidx.room.*

@Dao
interface RecurringExpenseDao {

    @Insert
    suspend fun insertRecurringExpense(recurringExpense: RecurringExpense): Long

    @Query("SELECT * FROM recurring_expenses WHERE active = 1")
    suspend fun getActiveRecurringExpenses(): List<RecurringExpense>

    @Query("SELECT * FROM recurring_expenses ORDER BY id DESC")
    suspend fun getAllRecurringExpenses(): List<RecurringExpense>

    @Delete
    suspend fun deleteRecurringExpense(recurringExpense: RecurringExpense)

    @Update
    suspend fun updateRecurringExpense(recurringExpense: RecurringExpense)
}
