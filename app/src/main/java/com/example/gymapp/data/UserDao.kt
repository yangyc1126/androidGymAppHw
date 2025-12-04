package com.example.gymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("UPDATE user_table SET password = :newPassword WHERE id = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)

    // 新增：取得特定使用者的即時資料 (Flow)
    @Query("SELECT * FROM user_table WHERE id = :userId")
    fun getUserById(userId: Int): Flow<User?>

    // 新增：更新使用者資料
    @Update
    suspend fun updateUser(user: User)
}