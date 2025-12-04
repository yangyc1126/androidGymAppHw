package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // 匯入鍵盤類型
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    navController: NavController,
    onSave: (String, String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") } // 這裡只存數字字串
    var level by remember { mutableStateOf("Beginner") }

    val colors = listOf(
        0xFF3F51B5, 0xFF009688, 0xFFFF9800,
        0xFFE91E63, 0xFF9C27B0, 0xFF4CAF50
    )
    var selectedColor by remember { mutableLongStateOf(colors[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Workout", fontWeight = FontWeight.Bold, color = Color.White) },
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
            // Title
            Text("Workout Title", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Morning Yoga") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Duration (Modified: Number Only)
            Text("Duration (minutes)", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = duration,
                onValueChange = {
                    // 只允許輸入數字
                    if (it.all { char -> char.isDigit() }) {
                        duration = it
                    }
                },
                placeholder = { Text("e.g. 30") },
                singleLine = true,
                // 設定鍵盤為數字模式
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                trailingIcon = { Text("min", color = Color.Gray, modifier = Modifier.padding(end = 16.dp)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Level
            Text("Level", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                listOf("Beginner", "Intermediate", "Advanced").forEach { lvl ->
                    FilterChip(
                        selected = level == lvl,
                        onClick = { level = lvl },
                        label = { Text(lvl) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3F51B5),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF2C2C2E),
                            labelColor = Color.Gray
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Color
            Text("Card Color", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                colors.forEach { colorHex ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(colorHex))
                            .clickable { selectedColor = colorHex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColor == colorHex) {
                            Icon(Icons.Default.Check, null, tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank() && duration.isNotBlank()) {
                        // 關鍵修正：儲存時自動加上 " min"
                        // 這樣 "30" 就會變成 "30 min"，符合 parser 的格式
                        onSave(title, "$duration min", level, selectedColor)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Workout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}