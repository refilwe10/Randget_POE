package com.example.randget11

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CUSTOM FEATURE 1: Recurring Expenses.
 *
 * Allows the user to define an expense that repeats every month
 * (e.g. rent, subscriptions). The app can then quickly generate
 * this month's entry for all active recurring expenses with a
 * single tap, instead of the user typing it out manually each time.
 */
@Entity(tableName = "recurring_expenses")
data class RecurringExpense(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val description: String,
    val categoryId: Int,
    val amount: Double,

    // Day of month this expense usually occurs on, e.g. 1 for the 1st
    val dayOfMonth: Int,

    // Whether this recurring expense is still active
    val active: Boolean = true
)
