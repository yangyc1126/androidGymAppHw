package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // 匯入 Coil
import com.example.gymapp.data.User
import java.io.File

@Composable
fun ProfileScreen(
    currentUser: User?,
    totalWorkouts: Int,
    totalMinutes: Int,
    totalCalories: Int,
    isDarkTheme: Boolean,
    onSettingsClick: () -> Unit,
    onMyPlanClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onToggleUnit: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color.White
    val secondaryTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray

    val isMetric = currentUser?.units != "Imperial"
    val weightDisplay = if (currentUser == null || currentUser.weight == 0f) "--" else {
        if (isMetric) "${currentUser.weight} kg" else "${(currentUser.weight * 2.20462f).toInt()} lb"
    }
    val heightDisplay = if (currentUser == null || currentUser.height == 0f) "--" else {
        if (isMetric) "${currentUser.height} cm" else String.format("%.1f ft", currentUser.height / 30.48f)
    }
    val nameDisplay = if (currentUser?.displayName.isNullOrBlank()) currentUser?.username ?: "Guest" else currentUser!!.displayName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = contentColor)
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = contentColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            // === 修改：顯示大頭貼 ===
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(cardColor),
                contentAlignment = Alignment.Center
            ) {
                if (currentUser?.profilePicturePath != null) {
                    AsyncImage(
                        model = File(currentUser.profilePicturePath),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, tint = secondaryTextColor, modifier = Modifier.size(40.dp))
                }
            }
            // ======================

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(nameDisplay, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = contentColor)
                Text("Age: ${if(currentUser?.age == 0) "--" else currentUser?.age}", fontSize = 14.sp, color = secondaryTextColor)
                Text("$weightDisplay  •  $heightDisplay", fontSize = 14.sp, color = secondaryTextColor)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onEditProfileClick) {
                Icon(Icons.Default.Edit, "Edit", tint = secondaryTextColor)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Dashboard
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatCard("Workouts", totalWorkouts.toString(), cardColor, contentColor, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            StatCard("Minutes", totalMinutes.toString(), cardColor, contentColor, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            StatCard("Kcal", totalCalories.toString(), cardColor, contentColor, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Menu
        Text("General", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = contentColor, modifier = Modifier.padding(bottom = 16.dp))

        ProfileMenuItem("My Weekly Goals", "View", cardColor, contentColor, secondaryTextColor, onMyPlanClick)
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem(title = "Units", value = if (isMetric) "Metric" else "Imperial", bgColor = cardColor, textColor = contentColor, subTextColor = secondaryTextColor, onClick = onToggleUnit)
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem("Notifications", "On", cardColor, contentColor, secondaryTextColor, {})
    }
}

// ... StatCard 和 ProfileMenuItem 保持不變 ...
@Composable
fun StatCard(label: String, value: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(100.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
            Text(label, fontSize = 12.sp, color = if(textColor == Color.White) Color.Gray else Color.DarkGray)
        }
    }
}

@Composable
fun ProfileMenuItem(title: String, value: String, bgColor: Color, textColor: Color, subTextColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).background(bgColor, RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = textColor, fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = subTextColor, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Outlined.ChevronRight, null, tint = subTextColor, modifier = Modifier.size(20.dp))
        }
    }
}