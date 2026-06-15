package com.example.randget11

/**
 * Simple gamification helper.
 * Checks expense/budget data for the given month and awards
 * badges (rewards) for things like staying within budget
 * and logging expenses consistently.
 */
object BadgeHelper {

    /**
     * Checks the given month's data and awards any badges the
     * user has newly earned. Safe to call repeatedly - badges
     * are only inserted once per key thanks to OnConflictStrategy.IGNORE
     * and the getBadgeByKey check.
     */
    suspend fun checkAndAwardBadges(db: AppDatabase, month: String) {

        val totalSpent = db.expenseDao().getTotalForMonth(month) ?: 0.0
        val budget = db.budgetDao().getBudgetForMonth(month)
        val expenseCount = db.expenseDao().getExpensesBetweenDates(
            "$month-01",
            "$month-31"
        ).size

        // Badge 1: Stayed within min/max budget goals for the month
        if (budget != null &&
            totalSpent in budget.minAmount..budget.maxAmount
        ) {
            awardBadge(
                db = db,
                key = "WITHIN_BUDGET_$month",
                title = "Budget Champion",
                icon = "🏆",
                description = "Stayed within your budget goals for $month",
                month = month
            )
        }

        // Badge 2: Consistent expense logging (5 or more expenses logged in the month)
        if (expenseCount >= 5) {
            awardBadge(
                db = db,
                key = "CONSISTENT_LOGGER_$month",
                title = "Consistent Logger",
                icon = "📝",
                description = "Logged $expenseCount expenses in $month",
                month = month
            )
        }
    }

    private suspend fun awardBadge(
        db: AppDatabase,
        key: String,
        title: String,
        icon: String,
        description: String,
        month: String
    ) {
        val existing = db.badgeDao().getBadgeByKey(key)
        if (existing == null) {
            db.badgeDao().insertBadge(
                Badge(
                    key = key,
                    title = title,
                    icon = icon,
                    description = description,
                    dateEarned = month
                )
            )
        }
    }
}