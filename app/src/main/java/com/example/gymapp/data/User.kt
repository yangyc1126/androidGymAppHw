package com.example.gymapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_table",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val displayName: String = "",
    val age: Int = 0,
    val weight: Float = 0f,
    val height: Float = 0f,
    val units: String = "Metric",
    // 新增：大頭貼路徑 (存放在 App 內部儲存空間的路徑)
    val profilePicturePath: String? = null
)