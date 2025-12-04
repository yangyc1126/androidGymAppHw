package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutTitle: String,
    workoutDuration: String, // 關鍵修改：這裡必須要新增這個參數！
    onStartWorkout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFF2C2C2E), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = workoutTitle,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(Alignment.Start)) {
                AssistChip(
                    onClick = { },
                    label = { Text(workoutDuration, color = Color.White) }, // 這裡使用參數
                    leadingIcon = { Icon(Icons.Default.Timer, null, tint = Color.White) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(
                    onClick = { },
                    label = { Text("Intermediate", color = Color.White) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "這是一個專為 $workoutTitle 設計的完整訓練課程，時長為 $workoutDuration。包含熱身、主要訓練與緩和運動。",
                color = Color.Gray,
                lineHeight = 24.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Start Workout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}