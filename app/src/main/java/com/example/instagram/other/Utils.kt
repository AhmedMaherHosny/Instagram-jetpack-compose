package com.example.instagram.other

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.instagram.models.UserX
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}

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
