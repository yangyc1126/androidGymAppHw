package com.example.gymapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Lock)
    object Home : Screen("home", "Workout", Icons.Filled.FitnessCenter)
    object History : Screen("history", "History", Icons.Outlined.History)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings)
    object AddWorkout : Screen("add_workout", "Add Workout", Icons.Filled.AddCircle)
    object MyPlan : Screen("my_plan", "My Plan", Icons.Filled.DateRange)

    // 新增：編輯個人資料頁面
    object EditProfile : Screen("edit_profile", "Edit Profile", Icons.Filled.Edit)

    object Detail : Screen("detail/{workoutTitle}/{workoutDuration}", "Detail", Icons.Filled.Info) {
        fun createRoute(workoutTitle: String, workoutDuration: String) = "detail/$workoutTitle/$workoutDuration"
    }
    object Execution : Screen("execution/{workoutTitle}/{workoutDuration}", "Execution", Icons.Filled.PlayArrow) {
        fun createRoute(workoutTitle: String, workoutDuration: String) = "execution/$workoutTitle/$workoutDuration"
    }
    object Completion : Screen("completion", "Completion", Icons.Filled.CheckCircle)

    companion object {
        val items = listOf(Home, History, Profile, Settings)
    }
}