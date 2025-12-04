package com.example.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymapp.data.HistoryRecord // 確保引用正確

@Composable
fun HistoryScreen(
    historyRecords: List<HistoryRecord>,
    onDeleteRecord: (HistoryRecord) -> Unit,
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF5F5F5)
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2E) else Color.White
    val secondaryTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "History",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (historyRecords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = secondaryTextColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "No workouts yet", color = secondaryTextColor, fontSize = 18.sp)
                    Text(text = "Start your first workout today!", color = secondaryTextColor, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items = historyRecords, key = { it.id }) { record ->
                    HistoryItemCard(
                        record = record,
                        onDelete = { onDeleteRecord(record) },
                        cardColor = cardColor,
                        contentColor = contentColor,
                        secondaryColor = secondaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    record: HistoryRecord,
    onDelete: () -> Unit,
    cardColor: Color,
    contentColor: Color,
    secondaryColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = record.title, color = contentColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = record.date, color = secondaryColor, fontSize = 14.sp)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF3F51B5), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = record.duration, color = contentColor, fontWeight = FontWeight.Medium)
                    }
                    Text(text = record.calories, color = secondaryColor, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE57373))
                }
            }
        }
    }
}