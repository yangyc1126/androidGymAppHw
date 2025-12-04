package com.example.gymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_table")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // 綁定使用者
    val weeklyWorkoutTarget: Int, // 每週目標運動次數
    val weeklyCalorieTarget: Int  // 每週目標消耗卡路里
)