package com.example.gymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plan_table")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // 綁定使用者
    val title: String,
    val duration: String,
    val level: String,
    val colorHex: Long // 儲存顏色的數值
)