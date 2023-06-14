package com.example.instagram.other

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.Placeable
import com.example.instagram.models.ChatRowData
import com.example.instagram.models.UserX
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


var currentUser: UserX? = null
var imageBitmapTemp: ImageBitmap? = null
var imageBitmapTempProfile: ImageBitmap? = null

fun convertImageBitmapToFile(imageBitmap: ImageBitmap): File {
    val outputStream = ByteArrayOutputStream()

    val bitmap = Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawBitmap(imageBitmap.asAndroidBitmap(), 0f, 0f, null)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

    val tempFile = File.createTempFile("image_", ".png")
    val fileOutputStream = FileOutputStream(tempFile)
    fileOutputStream.use {
        it.write(outputStream.toByteArray())
    }

    return tempFile
}

fun convertImageBitmapsToFiles(imageBitmaps: List<ImageBitmap>): List<File> {
    val files = mutableListOf<File>()

    for (imageBitmap in imageBitmaps) {
        val file = convertImageBitmapToFile(imageBitmap)
        files.add(file)
    }

    return files
}

fun calculateChatWidthAndHeight(
    text: String,
    chatRowData: ChatRowData,
    message: Placeable,
    status: Placeable?,
) {

    if (status != null) {

        val lineCount = chatRowData.lineCount
        val lastLineWidth = chatRowData.lastLineWidth
        val parentWidth = chatRowData.parentWidth

        val padding = (message.measuredWidth - chatRowData.textWidth) / 2
//        println(
//            "🌽 CHAT INIT calculate() text: $text\n" +
//                    "lineCount: $lineCount, parentWidth: $parentWidth, lastLineWidth: $lastLineWidth\n" +
//                    "MESSAGE width: ${message.width}, measured: ${message.measuredWidth}," +
//                    " textWidth: ${chatRowData.textWidth} padding: $padding\n" +
//                    "STATUS width: ${status.width}, measured: ${status.measuredWidth}, " +
//                    "(stat +last): ${lastLineWidth + status.measuredWidth}\n"
//        )

        // Multiple lines and last line and status is longer than text size and right padding
        if (lineCount > 1 && lastLineWidth + status.measuredWidth >= chatRowData.textWidth + padding) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight + status.measuredHeight
            chatRowData.measuredType = 0
//            println("🤔 CHAT calculate() 0 for ${chatRowData.textWidth + padding}")
        } else if (lineCount > 1 && lastLineWidth + status.measuredWidth < chatRowData.textWidth + padding) {
            // Multiple lines and last line and status is shorter than text size and right padding
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight
            chatRowData.measuredType = 1
//            println("🔥 CHAT calculate() 1 for ${message.measuredWidth - padding}")
        } else if (lineCount == 1 && message.width + status.measuredWidth >= parentWidth) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = message.measuredHeight + status.measuredHeight
            chatRowData.measuredType = 2
//            println("🎃 CHAT calculate() 2")
        } else {
            chatRowData.rowWidth = message.measuredWidth + status.measuredWidth
            chatRowData.rowHeight = message.measuredHeight
            chatRowData.measuredType = 3
//            println("🚀 CHAT calculate() 3")
        }
    } else {
        chatRowData.rowWidth = message.width
        chatRowData.rowHeight = message.height
    }
}

fun extractDateTimeComponents(dateTimeString: String?): Map<String, Any> {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH)
    val date = inputFormat.parse(dateTimeString)

    val calendar = Calendar.getInstance()
    calendar.time = date!!

    val dateTimeComponents = mutableMapOf<String, Any>()
    dateTimeComponents["year"] = calendar.get(Calendar.YEAR)
    dateTimeComponents["month"] = calendar.get(Calendar.MONTH) + 1
    dateTimeComponents["day"] = calendar.get(Calendar.DAY_OF_MONTH)

    // Pad the hour value with leading zero if necessary
    val hour = calendar.get(Calendar.HOUR)
    dateTimeComponents["hour"] = if (hour == 0) 12 else hour
    dateTimeComponents["hour"] = String.format("%02d", hour)

    // Pad the minute value with leading zero if necessary
    val minute = calendar.get(Calendar.MINUTE)
    dateTimeComponents["minute"] = String.format("%02d", minute)

    dateTimeComponents["amPm"] = calendar.get(Calendar.AM_PM)

    return dateTimeComponents
}

