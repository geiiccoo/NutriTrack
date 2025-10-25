package com.fit2081.nutritrack.data.genai

import androidx.room.Entity
import androidx.room.PrimaryKey

// Referenced from W5 Lab
@Entity(tableName = "motivational_tips")
data class MotivationalTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val message: String
)
