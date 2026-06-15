package com.example.randget11


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a gamification badge/reward earned by the user.
 * "key" is a unique code for the badge type (e.g. "WITHIN_BUDGET_2026-04")
 * so the same badge is not awarded twice.
 */
@Entity(tableName = "badges")
data class Badge(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val key: String,
    val title: String,
    val icon: String,
    val description: String,
    val dateEarned: String
)