package com.example.gallerysample.presentation.gallery.adapter

import com.example.gallerysample.data.repository.FileType

sealed class GalleryItem {

    abstract val isLoading: Boolean
    abstract val folderName: String

    data class Folder(
        override val isLoading: Boolean,
        override val folderName: String,
        val files: List<File>,
    ) : GalleryItem() {
        val previewFileType: FileType?
            get() = files.firstOrNull()?.fileType
    }

    data class File(
        override val isLoading: Boolean,
        override val folderName: String,
        val filePath: String,
        val fileName: String,
        val fileType: FileType,
        val url: String?,
    ) : GalleryItem()

}