package com.example.randget11
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Category::class, Expense::class, Budget::class, Badge::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun badgeDao(): BadgeDao
}