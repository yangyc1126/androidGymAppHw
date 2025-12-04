package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymapp.ui.theme.GymAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 強制使用深色主題以符合截圖風格
            GymAppTheme(darkTheme = true) {
                MainScreen()
            }
        }
    }
}

// 定義卡片資料結構
data class WorkoutItem(
    val title: String,
    val duration: String,
    val level: String? = null,
    val color: Color // 用顏色暫代圖片
)

@Composable
fun MainScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() },
        containerColor = Color(0xFF121212) // 深色背景
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // 讓整個頁面可以垂直滑動
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 頂部標題列
            TopHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // 搜尋欄
            SearchBar()

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Featured
            SectionTitle("Featured")
            FeaturedSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Quick Workouts
            SectionTitle("Quick Workouts")
            QuickWorkoutsSection()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Workouts",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        IconButton(onClick = { /* TODO: Add action */ }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
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
            .background(Color(0xFF2C2C2E)), // 深灰色背景
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
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun FeaturedSection() {
    val items = listOf(
        WorkoutItem("全身燃脂", "45 min", "Intermediate", Color(0xFF3F51B5)), // 藍色
        WorkoutItem("核心訓練", "30 min", "Beginner", Color(0xFFFF9800))   // 橘色
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            FeaturedCard(item)
        }
    }
}

@Composable
fun FeaturedCard(item: WorkoutItem) {
    Column(modifier = Modifier.width(280.dp)) {
        // 圖片區域 (暫時用 Box + 顏色代替)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.color)
        ) {
            // 這裡之後可以用 Image(painter = painterResource(...)) 替換
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Center).size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "${item.duration} · ${item.level}",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun QuickWorkoutsSection() {
    val items = listOf(
        WorkoutItem("早晨訓練", "15 min", null, Color(0xFF009688)), // 綠色
        WorkoutItem("午時訓練", "20 min", null, Color(0xFFE91E63)), // 粉色
        WorkoutItem("晚間放鬆", "25 min", null, Color(0xFF9C27B0))  // 紫色
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            QuickWorkoutCard(item)
        }
    }
}

@Composable
fun QuickWorkoutCard(item: WorkoutItem) {
    Column(modifier = Modifier.width(160.dp)) {
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
        Text(
            text = item.duration,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = Color(0xFF1C1C1E) // 底部導航列背景色
    ) {
        // 定義導航項目
        val items = listOf(
            "Workout" to Icons.Filled.FitnessCenter,
            "History" to Icons.Outlined.History,
            "Profile" to Icons.Outlined.Person,
            "Settings" to Icons.Outlined.Settings
        )

        items.forEachIndexed { index, (label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = index == 0, // 假設目前選中第一個
                onClick = { },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF333333), // 選中時的背景圓圈
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GymAppTheme(darkTheme = true) {
        MainScreen()
    }
}