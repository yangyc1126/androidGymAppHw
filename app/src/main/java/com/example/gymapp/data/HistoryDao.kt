package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    // 修改：只撈取「特定使用者」的資料
    @Query("SELECT * FROM history_table WHERE userId = :userId ORDER BY id DESC")
    fun getHistoryByUserId(userId: Int): Flow<List<HistoryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(record: HistoryRecord)

    @Delete
    suspend fun deleteHistory(record: HistoryRecord)
}