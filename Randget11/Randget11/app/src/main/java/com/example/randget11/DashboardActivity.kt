package com.example.randget11


import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        db = DatabaseProvider.getDatabase(this)

        val monthInput = findViewById<EditText>(R.id.etMonth)
        val btnLoad = findViewById<Button>(R.id.btnLoad)

        val tvTotal = findViewById<TextView>(R.id.tvTotalSpent)
        val tvStatus = findViewById<TextView>(R.id.tvBudgetStatus)
        val tvRemaining = findViewById<TextView>(R.id.tvRemaining)
        val progressGoals = findViewById<ProgressBar>(R.id.progressGoals)
        val tvProgressDetail = findViewById<TextView>(R.id.tvProgressDetail)
        val tvBadges = findViewById<TextView>(R.id.tvBadges)

        btnLoad.setOnClickListener {

            val month = monthInput.text.toString()

            if (month.isBlank()) {
                Toast.makeText(this, "Enter month", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                val totalSpent = db.expenseDao().getTotalForMonth(month) ?: 0.0
                val budget = db.budgetDao().getBudgetForMonth(month)

                // Check for any newly-earned gamification badges based on
                // this month's data, then load all badges to display.
                BadgeHelper.checkAndAwardBadges(db, month)
                val badges = db.badgeDao().getAllBadges()

                withContext(Dispatchers.Main) {

                    tvTotal.text = "Total Spent: R %.2f".format(totalSpent)

                    // Update the badges list
                    if (badges.isEmpty()) {
                        tvBadges.text = "No badges earned yet"
                    } else {
                        tvBadges.text = badges.joinToString("\n") { badge ->
                            "🏆 ${badge.title} - ${badge.description}"
                        }
                    }

                    if (budget == null) {
                        tvStatus.text = "No budget set for this month"
                        tvRemaining.text = ""
                        progressGoals.progress = 0
                        tvProgressDetail.text = "Set a budget to see your progress towards your goals."
                        return@withContext
                    }

                    val remaining = budget.maxAmount - totalSpent

                    tvRemaining.text = "Remaining: R %.2f".format(remaining)

                    when {
                        totalSpent > budget.maxAmount -> {
                            tvStatus.text = "⚠ OVER BUDGET"
                            tvStatus.setTextColor(Color.RED)
                        }

                        totalSpent >= budget.maxAmount * 0.8 -> {
                            tvStatus.text = "⚠ Near Limit"
                            tvStatus.setTextColor(Color.YELLOW)
                        }

                        else -> {
                            tvStatus.text = "✓ Within Budget"
                            tvStatus.setTextColor(Color.GREEN)
                        }
                    }

                    // Visual progress: how much of the max goal has been spent.
                    // Capped at 100% for the progress bar display.
                    val percentOfMax = if (budget.maxAmount > 0) {
                        (totalSpent / budget.maxAmount) * 100
                    } else 0.0

                    progressGoals.progress = percentOfMax.coerceIn(0.0, 100.0).toInt()

                    val withinGoals = totalSpent in budget.minAmount..budget.maxAmount
                    val goalMessage = if (withinGoals) {
                        "On track! You're between your min (R %.2f) and max (R %.2f) goals."
                            .format(budget.minAmount, budget.maxAmount)
                    } else if (totalSpent < budget.minAmount) {
                        "Below your minimum goal of R %.2f.".format(budget.minAmount)
                    } else {
                        "Over your maximum goal of R %.2f.".format(budget.maxAmount)
                    }

                    tvProgressDetail.text =
                        "Spent %.0f%% of max goal. %s".format(percentOfMax, goalMessage)
                }
            }
        }
    }
}