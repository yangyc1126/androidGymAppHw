package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plan_table WHERE userId = :userId ORDER BY id DESC")
    fun getWorkoutsByUserId(userId: Int): Flow<List<WorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutPlan)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutPlan)
}