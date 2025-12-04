package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit, // 傳遞帳號密碼給 MainActivity 處理
    onSignUpClick: (String, String) -> Unit,
    errorMessage: String? // 顯示錯誤訊息 (如：帳號不存在、密碼錯誤)
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) } // 切換登入/註冊模式

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF2C2C2E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = Color(0xFF3F51B5),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (isSignUpMode) "Create Account" else "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = if (isSignUpMode) "Sign up to get started" else "Sign in to continue",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 錯誤訊息顯示
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3F51B5),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF3F51B5),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF3F51B5),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3F51B5),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF3F51B5),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF3F51B5),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    if (isSignUpMode) {
                        onSignUpClick(username, password)
                    } else {
                        onLoginClick(username, password)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isSignUpMode) "Sign Up" else "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 切換模式按鈕
        TextButton(onClick = {
            isSignUpMode = !isSignUpMode
            // 切換時清除欄位可能比較好，這裡暫時保留
        }) {
            Text(
                text = if (isSignUpMode) "Already have an account? Login" else "Don't have an account? Sign Up",
                color = Color.Gray
            )
        }
    }
}