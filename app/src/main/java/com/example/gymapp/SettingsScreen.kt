package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onChangePassword: (String) -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color.White

    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change Password") },
            text = {
                Column {
                    Text("Enter your new password:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPassword.isNotBlank()) {
                        onChangePassword(newPassword)
                        showPasswordDialog = false
                        newPassword = ""
                    }
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = contentColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = contentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Preferences
            SettingsSectionTitle("Preferences", if (isDarkTheme) Color(0xFF3F51B5) else Color(0xFF3F51B5))
            SettingsSwitchItem("Notifications", true, {}, contentColor, cardColor)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsSwitchItem("Dark Mode", isDarkTheme, onThemeChange, contentColor, cardColor)

            Spacer(modifier = Modifier.height(24.dp))

            // Account
            SettingsSectionTitle("Account", if (isDarkTheme) Color(0xFF3F51B5) else Color(0xFF3F51B5))

            SettingsNavItem(
                title = "Change Password",
                textColor = contentColor,
                bgColor = cardColor,
                onClick = { showPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // About
            SettingsSectionTitle("About", if (isDarkTheme) Color(0xFF3F51B5) else Color(0xFF3F51B5))

            // 修改處：更新版本號
            Text("Version 6.1.0", color = if (isDarkTheme) Color.Gray else Color.DarkGray, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign Out", color = Color(0xFFE57373), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 輔助元件
@Composable
fun SettingsSectionTitle(title: String, color: Color) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(bottom = 12.dp, start = 8.dp))
}

@Composable
fun SettingsSwitchItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, textColor: Color, bgColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bgColor).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = textColor, fontSize = 16.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsNavItem(title: String, textColor: Color, bgColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bgColor).clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = textColor, fontSize = 16.sp)
        Icon(Icons.Outlined.ChevronRight, null, tint = if (textColor == Color.White) Color.Gray else Color.DarkGray, modifier = Modifier.size(20.dp))
    }
}