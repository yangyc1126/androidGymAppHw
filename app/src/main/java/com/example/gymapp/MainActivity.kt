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
import androidx.compose.ui.platform.LocalContext
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
import com.example.gymapp.data.AppDatabase
import com.example.gymapp.data.Goal
import com.example.gymapp.data.HistoryRecord
import com.example.gymapp.data.User
import com.example.gymapp.data.WorkoutPlan
import com.example.gymapp.ui.theme.GymAppTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }
            GymAppTheme(darkTheme = isDarkTheme) {
                MainApp(isDarkTheme = isDarkTheme, onThemeChange = { isDarkTheme = it })
            }
        }
    }
}

// UI Data Structures
data class WorkoutItem(val title: String, val duration: String, val level: String? = null, val color: Color)
data class UserStats(val totalWorkouts: Int, val totalMinutes: Int, val totalCalories: Int)

@Composable
fun MainApp(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Database Initialization
    val db = remember { AppDatabase.getDatabase(context) }
    val historyDao = db.historyDao()
    val userDao = db.userDao()
    val workoutPlanDao = db.workoutPlanDao()
    val goalDao = db.goalDao()

    // State Management
    var currentUserId by remember { mutableStateOf<Int?>(null) }
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }

    // Data Observation
    val historyRecords by remember(currentUserId) {
        if (currentUserId != null) historyDao.getHistoryByUserId(currentUserId!!) else flowOf(emptyList<HistoryRecord>())
    }.collectAsState(initial = emptyList<HistoryRecord>())

    val customWorkoutPlans by remember(currentUserId) {
        if (currentUserId != null) workoutPlanDao.getWorkoutsByUserId(currentUserId!!) else flowOf(emptyList<WorkoutPlan>())
    }.collectAsState(initial = emptyList<WorkoutPlan>())

    val userGoal by remember(currentUserId) {
        if (currentUserId != null) goalDao.getGoalByUserId(currentUserId!!) else flowOf(null)
    }.collectAsState(initial = null)

    val currentUser by remember(currentUserId) {
        if (currentUserId != null) userDao.getUserById(currentUserId!!) else flowOf(null)
    }.collectAsState(initial = null)

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Login.route

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavBar(navController, isDarkTheme) },
        containerColor = backgroundColor
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // === Login ===
            composable(Screen.Login.route) {
                LoginScreen(
                    errorMessage = loginErrorMessage,
                    onLoginClick = { username, password ->
                        scope.launch {
                            val user = userDao.login(username, password)
                            if (user != null) {
                                currentUserId = user.id
                                loginErrorMessage = null
                                navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                            } else {
                                loginErrorMessage = "Invalid username or password"
                            }
                        }
                    },
                    onSignUpClick = { username, password ->
                        scope.launch {
                            val existingUser = userDao.getUserByUsername(username)
                            if (existingUser == null) {
                                userDao.insertUser(User(username = username, password = password))
                                val newUser = userDao.login(username, password)
                                currentUserId = newUser?.id
                                loginErrorMessage = null
                                navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                            } else {
                                loginErrorMessage = "Username already exists"
                            }
                        }
                    }
                )
            }

            // === Home ===
            composable(Screen.Home.route) {
                HomeScreen(
                    isDarkTheme = isDarkTheme,
                    customWorkouts = customWorkoutPlans,
                    onAddWorkoutClick = { navController.navigate(Screen.AddWorkout.route) },
                    onWorkoutClick = { title, duration -> navController.navigate(Screen.Detail.createRoute(title, duration)) },
                    onDeleteCustomWorkout = { plan -> scope.launch { workoutPlanDao.deleteWorkout(plan) } }
                )
            }

            // === Add Workout ===
            composable(Screen.AddWorkout.route) {
                AddWorkoutScreen(navController, onSave = { title, duration, level, colorHex ->
                    currentUserId?.let { uid ->
                        scope.launch { workoutPlanDao.insertWorkout(WorkoutPlan(userId = uid, title = title, duration = duration, level = level, colorHex = colorHex)) }
                    }
                    navController.popBackStack()
                })
            }

            composable(Screen.Detail.route) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("workoutTitle") ?: "Unknown"
                val duration = backStackEntry.arguments?.getString("workoutDuration") ?: "0 min"
                WorkoutDetailScreen(navController, title, duration, { navController.navigate(Screen.Execution.createRoute(title, duration)) })
            }

            composable(Screen.Execution.route) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("workoutTitle") ?: "Unknown"
                val duration = backStackEntry.arguments?.getString("workoutDuration") ?: "0 min"
                WorkoutExecutionScreen(navController, title, duration, {
                    val currentDate = getCurrentDate()
                    currentUserId?.let { userId ->
                        scope.launch { historyDao.insertHistory(HistoryRecord(userId = userId, title = title, duration = duration, date = currentDate)) }
                    }
                    navController.navigate(Screen.Completion.route) { popUpTo(Screen.Home.route) { inclusive = false } }
                })
            }

            composable(Screen.Completion.route) {
                CompletionScreen(onGoHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } })
            }

            composable(Screen.History.route) {
                HistoryScreen(historyRecords, { record -> scope.launch { historyDao.deleteHistory(record) } }, isDarkTheme)
            }

            // === Profile ===
            composable(Screen.Profile.route) {
                val stats by remember(historyRecords) { derivedStateOf { calculateStats(historyRecords) } }
                ProfileScreen(
                    currentUser = currentUser,
                    totalWorkouts = stats.totalWorkouts,
                    totalMinutes = stats.totalMinutes,
                    totalCalories = stats.totalCalories,
                    isDarkTheme = isDarkTheme,
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onMyPlanClick = { navController.navigate(Screen.MyPlan.route) },
                    onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                    onToggleUnit = {
                        currentUser?.let { user ->
                            val newUnit = if (user.units == "Metric") "Imperial" else "Metric"
                            scope.launch { userDao.updateUser(user.copy(units = newUnit)) }
                        }
                    }
                )
            }

            // === My Plan ===
            composable(Screen.MyPlan.route) {
                val weeklyStats = calculateWeeklyStats(historyRecords)
                MyPlanScreen(navController, weeklyStats.totalWorkouts, weeklyStats.totalCalories, userGoal?.weeklyWorkoutTarget ?: 0, userGoal?.weeklyCalorieTarget ?: 0, onSaveGoal = { w, c ->
                    currentUserId?.let { uid ->
                        scope.launch {
                            val goal = userGoal?.copy(weeklyWorkoutTarget = w, weeklyCalorieTarget = c) ?: Goal(userId = uid, weeklyWorkoutTarget = w, weeklyCalorieTarget = c)
                            goalDao.insertGoal(goal)
                        }
                    }
                    navController.popBackStack()
                })
            }

            // === Edit Profile (更新：支援圖片路徑) ===
            composable(Screen.EditProfile.route) {
                if (currentUser != null) {
                    EditProfileScreen(
                        navController = navController,
                        currentUser = currentUser!!,
                        onSave = { name, age, weight, height, profilePath ->
                            scope.launch {
                                userDao.updateUser(
                                    currentUser!!.copy(
                                        displayName = name,
                                        age = age,
                                        weight = weight,
                                        height = height,
                                        profilePicturePath = profilePath // 儲存路徑
                                    )
                                )
                            }
                            navController.popBackStack()
                        }
                    )
                }
            }

            // === Settings ===
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onChangePassword = { newPass ->
                        currentUserId?.let { uid -> scope.launch { userDao.updatePassword(uid, newPass) } }
                    },
                    onSignOut = {
                        currentUserId = null
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    }
}

// ... 以下元件 (HomeScreen, TopHeader 等) 保持不變，請保留您原本的程式碼 ...
// 為了避免篇幅過長，這裡只列出需要保留的部分，請不要刪除您專案裡原本的這些函式。
@Composable
fun HomeScreen(isDarkTheme: Boolean, customWorkouts: List<WorkoutPlan>, onAddWorkoutClick: () -> Unit, onWorkoutClick: (String, String) -> Unit, onDeleteCustomWorkout: (WorkoutPlan) -> Unit) {
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val inputColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color.White
    var searchQuery by remember { mutableStateOf("") }

    val defaultFeatured = listOf(WorkoutItem("全身燃脂", "45 min", "Intermediate", Color(0xFF3F51B5)), WorkoutItem("核心訓練", "30 min", "Beginner", Color(0xFFFF9800)))
    val defaultQuick = listOf(WorkoutItem("早晨訓練", "15 min", null, Color(0xFF009688)), WorkoutItem("午時訓練", "20 min", null, Color(0xFFE91E63)), WorkoutItem("晚間放鬆", "25 min", null, Color(0xFF9C27B0)))

    val filteredCustom = remember(customWorkouts, searchQuery) { customWorkouts.filter { it.title.contains(searchQuery, true) || it.duration.contains(searchQuery, true) } }
    val filteredFeatured = remember(searchQuery) { defaultFeatured.filter { it.title.contains(searchQuery, true) || it.duration.contains(searchQuery, true) } }
    val filteredQuick = remember(searchQuery) { defaultQuick.filter { it.title.contains(searchQuery, true) || it.duration.contains(searchQuery, true) } }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(16.dp))
        TopHeader(contentColor, onAddWorkoutClick)
        Spacer(modifier = Modifier.height(16.dp))
        SearchBar(searchQuery, { searchQuery = it }, inputColor, contentColor)

        if (filteredCustom.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("My Workouts", contentColor)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredCustom) { plan -> FeaturedCard(WorkoutItem(plan.title, plan.duration, plan.level, Color(plan.colorHex)), contentColor, { onWorkoutClick(plan.title, plan.duration) }, { onDeleteCustomWorkout(plan) }) }
            }
        }
        if (filteredFeatured.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Featured", contentColor)
            FeaturedSection(filteredFeatured, onWorkoutClick, contentColor)
        }
        if (filteredQuick.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Quick Workouts", contentColor)
            QuickWorkoutsSection(filteredQuick, onWorkoutClick, contentColor)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ... 保持 SearchBar, FeaturedSection, QuickWorkoutsSection, TopHeader, SectionTitle, BottomNavBar, FeaturedCard, QuickWorkoutCard 不變 ...
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, bgColor: Color, textColor: Color) {
    TextField(value = query, onValueChange = onQueryChange, placeholder = { Text("Search (e.g. Yoga, 30 min)", color = Color.Gray) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) }, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bgColor), colors = TextFieldDefaults.colors(focusedContainerColor = bgColor, unfocusedContainerColor = bgColor, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, focusedTextColor = textColor, unfocusedTextColor = textColor), singleLine = true)
}
@Composable
fun FeaturedSection(items: List<WorkoutItem>, onCardClick: (String, String) -> Unit, contentColor: Color) { LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) { items(items) { item -> FeaturedCard(item, contentColor, onClick = { onCardClick(item.title, item.duration) }) } } }
@Composable
fun QuickWorkoutsSection(items: List<WorkoutItem>, onCardClick: (String, String) -> Unit, contentColor: Color) { LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) { items(items) { item -> QuickWorkoutCard(item, contentColor, onClick = { onCardClick(item.title, item.duration) }) } } }
@Composable
fun TopHeader(textColor: Color, onAddClick: () -> Unit) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Workouts", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor); IconButton(onClick = onAddClick) { Icon(Icons.Default.Add, null, tint = textColor, modifier = Modifier.size(32.dp)) } } }
@Composable
fun SectionTitle(title: String, textColor: Color) { Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(bottom = 12.dp)) }
@Composable
fun BottomNavBar(navController: NavController, isDarkTheme: Boolean) { val containerColor = if (isDarkTheme) Color(0xFF1C1C1E) else Color.White; val contentColor = if (isDarkTheme) Color.White else Color.Black; NavigationBar(containerColor = containerColor) { val navBackStackEntry by navController.currentBackStackEntryAsState(); val currentDestination = navBackStackEntry?.destination; Screen.items.forEach { screen -> NavigationBarItem(icon = { Icon(screen.icon, null) }, label = { Text(screen.title) }, selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true, onClick = { navController.navigate(screen.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } }, colors = NavigationBarItemDefaults.colors(selectedIconColor = if (isDarkTheme) Color.White else Color.White, selectedTextColor = contentColor, indicatorColor = if (isDarkTheme) Color(0xFF333333) else Color(0xFF3F51B5), unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)) } } }
@Composable
fun FeaturedCard(item: WorkoutItem, contentColor: Color, onClick: () -> Unit, onDelete: (() -> Unit)? = null) { Column(modifier = Modifier.width(280.dp).clickable(onClick = onClick)) { Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(item.color)) { Icon(Icons.Default.FitnessCenter, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.Center).size(48.dp)); if (onDelete != null) { IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) { Icon(Icons.Default.Delete, "Delete", tint = Color.White) } } }; Spacer(modifier = Modifier.height(8.dp)); Text(item.title, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("${item.duration} · ${item.level}", color = Color.Gray, fontSize = 14.sp) } }
@Composable
fun QuickWorkoutCard(item: WorkoutItem, contentColor: Color, onClick: () -> Unit) { Column(modifier = Modifier.width(160.dp).clickable(onClick = onClick)) { Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(12.dp)).background(item.color)) { Icon(Icons.Default.DirectionsRun, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.Center).size(32.dp)) }; Spacer(modifier = Modifier.height(8.dp)); Text(item.title, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(item.duration, color = Color.Gray, fontSize = 14.sp) } }

// 統計函式
fun calculateStats(records: List<HistoryRecord>): UserStats {
    var minutes = 0; var calories = 0
    records.forEach {
        minutes += try { it.duration.trim().split(" ")[0].toInt() } catch (e: Exception) { 0 }
        calories += try { it.calories.trim().split(" ")[0].toInt() } catch (e: Exception) { 0 }
    }
    return UserStats(records.size, minutes, calories)
}

fun calculateWeeklyStats(records: List<HistoryRecord>): UserStats {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    val startOfWeek = cal.time

    var minutes = 0; var calories = 0
    var count = 0
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    records.forEach { record ->
        try {
            val recordDate = sdf.parse(record.date)
            if (recordDate != null && recordDate.after(startOfWeek)) {
                count++
                minutes += record.duration.trim().split(" ")[0].toInt()
                calories += record.calories.trim().split(" ")[0].toInt()
            }
        } catch (e: Exception) { }
    }
    return UserStats(count, minutes, calories)
}

fun getCurrentDate(): String { val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()); return sdf.format(Date()) }