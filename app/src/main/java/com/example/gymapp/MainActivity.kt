package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gymapp.ui.theme.GymAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymAppTheme(darkTheme = true) {
                MainApp()
            }
        }
    }
}

data class WorkoutItem(
    val title: String,
    val duration: String,
    val level: String? = null,
    val color: Color
)

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val historyRecords = remember { mutableStateListOf<HistoryRecord>() }

    // 判斷是否需要顯示底部導航欄 (登入頁不需要)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route, // 修改起始頁為登入頁
            modifier = Modifier.padding(innerPadding)
        ) {
            // 0. 登入頁 (Login)
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // 登入成功，前往首頁，並清除登入頁的堆疊 (避免按返回鍵回到登入頁)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 1. 首頁
            composable(Screen.Home.route) {
                HomeScreen(
                    onWorkoutClick = { title, duration ->
                        navController.navigate(Screen.Detail.createRoute(title, duration))
                    }
                )
            }

            // 2. 詳細頁
            composable(Screen.Detail.route) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("workoutTitle") ?: "Unknown"
                val duration = backStackEntry.arguments?.getString("workoutDuration") ?: "0 min"

                WorkoutDetailScreen(
                    navController = navController,
                    workoutTitle = title,
                    workoutDuration = duration,
                    onStartWorkout = {
                        navController.navigate(Screen.Execution.createRoute(title, duration))
                    }
                )
            }

            // 3. 執行頁
            composable(Screen.Execution.route) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("workoutTitle") ?: "Unknown"
                val duration = backStackEntry.arguments?.getString("workoutDuration") ?: "0 min"

                WorkoutExecutionScreen(
                    navController = navController,
                    workoutTitle = title,
                    workoutDuration = duration,
                    onWorkoutComplete = {
                        val currentDate = getCurrentDate()
                        historyRecords.add(0, HistoryRecord(title, duration, currentDate))

                        navController.navigate(Screen.Completion.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                )
            }

            // 4. 完賽頁
            composable(Screen.Completion.route) {
                CompletionScreen(
                    onGoHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // 5. 歷史紀錄頁
            composable(Screen.History.route) {
                HistoryScreen(
                    historyRecords = historyRecords,
                    onDeleteRecord = { record ->
                        historyRecords.remove(record)
                    }
                )
            }

            // 6. 個人頁面
            composable(Screen.Profile.route) {
                val stats by remember {
                    derivedStateOf { calculateStats(historyRecords) }
                }

                ProfileScreen(
                    totalWorkouts = stats.totalWorkouts,
                    totalMinutes = stats.totalMinutes,
                    totalCalories = stats.totalCalories,
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            // 7. 設定頁面
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    onSignOut = {
                        // 登出，回到登入頁，並清除所有歷史堆疊
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

data class UserStats(
    val totalWorkouts: Int,
    val totalMinutes: Int,
    val totalCalories: Int
)

fun calculateStats(records: List<HistoryRecord>): UserStats {
    var minutes = 0
    var calories = 0

    records.forEach { record ->
        val m = try {
            record.duration.trim().split(" ")[0].toInt()
        } catch (e: Exception) { 0 }

        val c = try {
            record.calories.trim().split(" ")[0].toInt()
        } catch (e: Exception) { 0 }

        minutes += m
        calories += c
    }

    return UserStats(
        totalWorkouts = records.size,
        totalMinutes = minutes,
        totalCalories = calories
    )
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

// === 以下為 UI 元件 ===

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF1C1C1E)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Screen.items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF333333),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun HomeScreen(onWorkoutClick: (String, String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TopHeader()
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Featured")
        FeaturedSection(onWorkoutClick)
        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle("Quick Workouts")
        QuickWorkoutsSection(onWorkoutClick)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Workouts", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        IconButton(onClick = { }) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2C2C2E)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2C2C2E),
            unfocusedContainerColor = Color(0xFF2C2C2E),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun FeaturedSection(onCardClick: (String, String) -> Unit) {
    val items = listOf(
        WorkoutItem("全身燃脂", "45 min", "Intermediate", Color(0xFF3F51B5)),
        WorkoutItem("核心訓練", "30 min", "Beginner", Color(0xFFFF9800))
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(items) { item ->
            FeaturedCard(item, onClick = { onCardClick(item.title, item.duration) })
        }
    }
}

@Composable
fun FeaturedCard(item: WorkoutItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(280.dp).clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.color)
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center).size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "${item.duration} · ${item.level}", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun QuickWorkoutsSection(onCardClick: (String, String) -> Unit) {
    val items = listOf(
        WorkoutItem("早晨訓練", "15 min", null, Color(0xFF009688)),
        WorkoutItem("午時訓練", "20 min", null, Color(0xFFE91E63)),
        WorkoutItem("晚間放鬆", "25 min", null, Color(0xFF9C27B0))
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(items) { item ->
            QuickWorkoutCard(item, onClick = { onCardClick(item.title, item.duration) })
        }
    }
}

@Composable
fun QuickWorkoutCard(item: WorkoutItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(160.dp).clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.color)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center).size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = item.duration, color = Color.Gray, fontSize = 14.sp)
    }
}