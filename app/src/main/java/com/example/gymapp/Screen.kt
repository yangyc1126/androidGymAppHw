package com.example.gymapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock // 新增 Lock 圖示
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    // 新增：登入頁面
    object Login : Screen("login", "Login", Icons.Default.Lock)

    object Home : Screen("home", "Workout", Icons.Filled.FitnessCenter)
    object History : Screen("history", "History", Icons.Outlined.History)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings)

    // 詳細頁面
    object Detail : Screen("detail/{workoutTitle}/{workoutDuration}", "Detail", Icons.Filled.Info) {
        fun createRoute(workoutTitle: String, workoutDuration: String) = "detail/$workoutTitle/$workoutDuration"
    }

    // 執行頁面
    object Execution : Screen("execution/{workoutTitle}/{workoutDuration}", "Execution", Icons.Filled.PlayArrow) {
        fun createRoute(workoutTitle: String, workoutDuration: String) = "execution/$workoutTitle/$workoutDuration"
    }

    // 完賽頁面
    object Completion : Screen("completion", "Completion", Icons.Filled.CheckCircle)

    companion object {
        val items = listOf(Home, History, Profile, Settings)
    }
}