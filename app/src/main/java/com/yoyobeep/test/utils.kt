package com.yoyobeep.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yoyobeep.test.ui.theme.fontFamily
import java.io.ByteArrayInputStream
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Stable
val isInPreview @Composable get() = LocalInspectionMode.current

fun decodeFromBase64(base64String: String): ByteArray? {
    return Base64.decode(base64String, Base64.DEFAULT)
}

fun encodeToBase64(byteArray: ByteArray): String {
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
    return try {
        val inputStream = ByteArrayInputStream(byteArray)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@RequiresApi(Build.VERSION_CODES.O)
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

// Get current date and time in Asia/Kolkata timezone
@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateTime(): Pair<String, String> {
    val zoneId = ZoneId.of("Asia/Kolkata")
    val now = ZonedDateTime.now(zoneId)

    val date = now.toLocalDate().format(dateFormatter)
    val time = now.toLocalTime().format(timeFormatter)

    return Pair(date, time)
}

@Composable
fun SortDialog(
    onDismiss: () -> Unit,
    onOldSortSelected: () -> Unit,
    onNewSortSelected: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .padding(8.dp)
                .wrapContentWidth()
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(8.dp) // Add padding around the content
            ) {
                TextButton(onClick = {
                    onOldSortSelected()
                    onDismiss()
                }) {
                    Text(
                        text = "Old",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = fontFamily,
                        color = Color(0xFF1C1C1C)
                    )
                }
                // Divider with adjusted width
                Divider(
                    color = Color(0xFFE5E5E5),
                    thickness = 1.dp,
                    modifier = Modifier
                        .width(90.dp)
                        .padding(vertical = 8.dp)
                )
                TextButton(onClick = {
                    onNewSortSelected()
                    onDismiss()
                }) {
                    Text(
                        text = "New",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = fontFamily,
                        color = Color(0xFF1C1C1C)
                    )
                }
            }
        }
    }
}



