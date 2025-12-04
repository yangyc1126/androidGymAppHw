package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlanScreen(
    navController: NavController,
    currentWeeklyWorkouts: Int,
    currentWeeklyCalories: Int,
    targetWorkouts: Int, // 從 DB 讀取的目標
    targetCalories: Int, // 從 DB 讀取的目標
    onSaveGoal: (Int, Int) -> Unit
) {
    // 本地狀態，用於 UI 調整
    var sliderWorkouts by remember { mutableFloatStateOf(targetWorkouts.toFloat()) }
    var sliderCalories by remember { mutableFloatStateOf(targetCalories.toFloat()) }

    // 如果沒有設定過目標，給預設值
    LaunchedEffect(targetWorkouts, targetCalories) {
        if (targetWorkouts == 0) sliderWorkouts = 3f
        if (targetCalories == 0) sliderCalories = 500f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Goals", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // === 1. 進度區塊 ===
            Text("This Week's Progress", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // 運動次數進度
            GoalProgressCard(
                title = "Workouts",
                current = currentWeeklyWorkouts,
                target = sliderWorkouts.roundToInt(),
                unit = "times"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 卡路里進度
            GoalProgressCard(
                title = "Calories",
                current = currentWeeklyCalories,
                target = sliderCalories.roundToInt(),
                unit = "kcal"
            )

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(32.dp))

            // === 2. 設定目標區塊 ===
            Text("Set Your Goals", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // 設定次數
            Text("Target Workouts: ${sliderWorkouts.roundToInt()} / week", color = Color.Gray)
            Slider(
                value = sliderWorkouts,
                onValueChange = { sliderWorkouts = it },
                valueRange = 1f..14f, // 每週 1 到 14 次
                steps = 12,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF3F51B5),
                    activeTrackColor = Color(0xFF3F51B5)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 設定卡路里
            Text("Target Calories: ${sliderCalories.roundToInt()} kcal / week", color = Color.Gray)
            Slider(
                value = sliderCalories,
                onValueChange = { sliderCalories = it },
                valueRange = 100f..5000f,
                steps = 48, // (5000-100)/100 約等於 49 格
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFE91E63),
                    activeTrackColor = Color(0xFFE91E63)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 儲存按鈕
            Button(
                onClick = { onSaveGoal(sliderWorkouts.roundToInt(), sliderCalories.roundToInt()) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Goals", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GoalProgressCard(title: String, current: Int, target: Int, unit: String) {
    val progress = if (target > 0) (current.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    val color = if (title == "Workouts") Color(0xFF3F51B5) else Color(0xFFE91E63)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text("$current / $target $unit", color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.DarkGray,
            )
        }
    }
}