package com.example.randget11

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.*

class GamificationActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification)

        db = DatabaseProvider.getDatabase(this)

        val tvBadges = findViewById<TextView>(R.id.tvBadges)

        CoroutineScope(Dispatchers.IO).launch {
            val expenseCount = db.expenseDao().getAllExpenses().size
            val totalSpent = db.expenseDao().getTotalForMonth("2026-06") ?: 0.0 // Example month

            val badges = mutableListOf<String>()

            if (expenseCount >= 5) badges.add("✅ Consistent Logger (5+ expenses)")
            if (expenseCount >= 10) badges.add("🏆 Expense Master (10+ expenses)")
            if (totalSpent > 0) badges.add("💰 First Expense Logged")
            // Add more based on budget etc.

            withContext(Dispatchers.Main) {
                tvBadges.text = if (badges.isEmpty()) "No badges yet. Log some expenses!" else badges.joinToString("\n")
            }
        }
    }
}
