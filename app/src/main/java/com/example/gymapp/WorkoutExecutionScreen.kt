package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun WorkoutExecutionScreen(
    navController: NavController,
    workoutTitle: String,
    workoutDuration: String, // 接收 "30 min"
    onWorkoutComplete: () -> Unit
) {
    // 使用下方的輔助函式解析時間
    val totalSeconds = remember(workoutDuration) {
        parseDurationToSeconds(workoutDuration)
    }

    var timeLeft by remember { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = isRunning, key2 = timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            isRunning = false
            onWorkoutComplete()
        }
    }

    val progress = if (totalSeconds > 0) timeLeft.toFloat() / totalSeconds.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(
                text = workoutTitle,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Timer Display
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(280.dp),
                color = Color.DarkGray,
                strokeWidth = 12.dp,
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(280.dp),
                color = if (timeLeft < 10) Color(0xFFFF5252) else Color(0xFF3F51B5),
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(timeLeft),
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Remaining",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }

        // Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRunning) "Keep going!" else "Paused",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2E)),
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Pause/Resume",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Button(
                    onClick = onWorkoutComplete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(80.dp).width(160.dp)
                ) {
                    Text("Finish", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 關鍵：這個函式負責把 "30 min" 轉成 1800 秒
fun parseDurationToSeconds(duration: String): Int {
    return try {
        // 1. 去除前後空白
        // 2. 用空格切割 ("30 min" -> ["30", "min"])
        // 3. 取第一個元素 ("30")
        // 4. 轉成整數
        val minutes = duration.trim().split(" ")[0].toInt()
        minutes * 60
    } catch (e: Exception) {
        60 // 解析失敗時的預設值
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}