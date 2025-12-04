package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    // 取得特定使用者的目標 (如果沒有設過目標，可能會回傳 null)
    @Query("SELECT * FROM goal_table WHERE userId = :userId LIMIT 1")
    fun getGoalByUserId(userId: Int): Flow<Goal?>

    // 新增或更新目標 (若 userId 相同則覆蓋)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)
}