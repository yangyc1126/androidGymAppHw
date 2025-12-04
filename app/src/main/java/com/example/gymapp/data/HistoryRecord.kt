package com.example.gymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // 新增：連結到 User 的 ID
    val title: String,
    val duration: String,
    val date: String,
    val calories: String = "120 kcal"
)