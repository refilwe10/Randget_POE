package com.example.randget11

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

/**
 * Required Feature: Gamification.
 *
 * Displays all badges/rewards the user has earned for meeting budget
 * goals or consistently logging expenses (see BadgeManager).
 */
class BadgesActivity : AppCompatActivity() {

    private val TAG = "BadgesActivity"

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        db = DatabaseProvider.getDatabase(this)

        val recycler = findViewById<RecyclerView>(R.id.recyclerBadges)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        recycler.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val badges = db.badgeDao().getAllBadges()
            Log.d(TAG, "Loaded ${badges.size} badge(s)")

            withContext(Dispatchers.Main) {
                if (badges.isEmpty()) {
                    tvEmpty.visibility = android.view.View.VISIBLE
                } else {
                    tvEmpty.visibility = android.view.View.GONE
                    recycler.adapter = BadgeAdapter(badges)
                }
            }
        }
    }
}
