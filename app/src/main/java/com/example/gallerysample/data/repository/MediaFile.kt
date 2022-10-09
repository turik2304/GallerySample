package com.example.gallerysample.data.repository

import android.graphics.Bitmap
import android.net.Uri

data class MediaFile(
    val fileName: String,
    val folderName: String,
    val filePath: String,
    val fileDate: Long,
    val fileType: FileType,
)

sealed class FileType {
    data class Image(val previewUri: Uri) : FileType()
    data class Video(val previewBitmap: Bitmap?) : FileType()
    object Unknown : FileType()
}