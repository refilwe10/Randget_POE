package com.example.randget11


import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesBetweenDates(startDate: String, endDate: String): List<Expense>

    @Query("""
    SELECT categories.name AS categoryName, SUM(expenses.amount) AS totalAmount
    FROM expenses
    INNER JOIN categories ON expenses.categoryId = categories.id
    WHERE date BETWEEN :startDate AND :endDate
    GROUP BY categoryId
""")
    suspend fun getTotalSpentPerCategory(
        startDate: String,
        endDate: String
    ): List<CategoryTotal>
    @Query("""
    SELECT date AS categoryName, SUM(amount) AS totalAmount
    FROM expenses
    WHERE date BETWEEN :startDate AND :endDate
    GROUP BY date
    ORDER BY date ASC
""")
    suspend fun getDailyTotalsBetweenDates(startDate: String, endDate: String): List<CategoryTotal>

    @Query("""
    SELECT SUM(amount) FROM expenses
    WHERE date LIKE :month || '%'
""")
    suspend fun getTotalForMonth(month: String): Double?
}