package com.example.gallerysample.presentation.gallery.adapter

import android.graphics.Bitmap

sealed class GalleryItem {

    abstract val isLoading: Boolean
    abstract val folderName: String
    abstract val previewBitmap: Bitmap?

    data class Folder(
        override val isLoading: Boolean,
        override val folderName: String,
        val files: List<File>,
    ) : GalleryItem() {
        override val previewBitmap: Bitmap? = files.firstOrNull()?.previewBitmap
    }

    data class File(
        override val isLoading: Boolean,
        override val folderName: String,
        override val previewBitmap: Bitmap?,
        val filePath: String,
        val fileName: String,
        val isVideo: Boolean,
        val url: String?,
    ) : GalleryItem()

}