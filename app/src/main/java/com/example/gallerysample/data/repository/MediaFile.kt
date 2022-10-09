package com.example.gallerysample.data.repository

import android.graphics.Bitmap

data class Folder(
    val folderName: String,
    val files: List<MediaFile>,
)

data class MediaFile(
    val fileName: String,
    val folderName: String,
    val filePath: String,
    val fileDate: Long,
    val fileType: FileType,
    val previewBitmap: Bitmap?,
)

enum class FileType {
    IMAGE,
    VIDEO,
    UNKNOWN
}