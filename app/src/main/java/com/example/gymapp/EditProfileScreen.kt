package com.example.gymapp

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt // 新增相機圖示
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gymapp.data.User
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    currentUser: User,
    onSave: (String, Int, Float, Float, String?) -> Unit // 修改：增加 profilePath 參數
) {
    var displayName by remember { mutableStateOf(currentUser.displayName) }
    var age by remember { mutableStateOf(if (currentUser.age > 0) currentUser.age.toString() else "") }
    var weight by remember { mutableStateOf(if (currentUser.weight > 0f) currentUser.weight.toString() else "") }
    var height by remember { mutableStateOf(if (currentUser.height > 0f) currentUser.height.toString() else "") }

    // 圖片處理狀態
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentProfilePath by remember { mutableStateOf(currentUser.profilePicturePath) }
    val context = LocalContext.current

    // 圖片選擇器 Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                // 當使用者選好圖片，我們先暫存 URI，按下 Save 時再複製檔案
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = Color.White) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // 讓頭像置中
        ) {
            // === 頭像編輯區 ===
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2C2E))
                    .clickable {
                        // 開啟相簿選擇器 (只選圖片)
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                // 優先顯示新選的圖片，其次是舊的存檔，最後是預設圖示
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (currentProfilePath != null) {
                    AsyncImage(
                        model = File(currentProfilePath!!),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(50.dp)
                    )
                }

                // 顯示一個小相機圖示提示可以更換
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White.copy(alpha = 0.7f))
                }
            }
            Text("Tap to change photo", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Display Name
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Age
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                label = { Text("Age") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Height
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // 如果有選新圖片，將其複製到內部儲存空間
                    var finalPath = currentProfilePath
                    if (selectedImageUri != null) {
                        finalPath = copyUriToInternalStorage(context, selectedImageUri!!, "profile_${currentUser.id}.jpg")
                    }

                    onSave(
                        displayName,
                        age.toIntOrNull() ?: 0,
                        weight.toFloatOrNull() ?: 0f,
                        height.toFloatOrNull() ?: 0f,
                        finalPath
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 輔助函式：複製圖片到 App 內部空間
fun copyUriToInternalStorage(context: Context, uri: Uri, filename: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, filename)
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}