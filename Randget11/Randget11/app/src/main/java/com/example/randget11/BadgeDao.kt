package com.example.randget11

import androidx.room.*

@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: Badge)

    @Query("SELECT * FROM badges ORDER BY id DESC")
    suspend fun getAllBadges(): List<Badge>

    @Query("SELECT * FROM badges WHERE `key` = :key LIMIT 1")
    suspend fun getBadgeByKey(key: String): Badge?
}