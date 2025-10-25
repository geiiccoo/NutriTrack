package com.fit2081.nutritrack.data.genai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
// Referenced from W5 Lab
interface MotivationalTipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: MotivationalTip)

    @Query("SELECT * FROM motivational_tips WHERE userId = :userId ORDER BY id DESC")
    fun getAllTipsForUser(userId: String): Flow<List<MotivationalTip>>
}