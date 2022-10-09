package com.example.gallerysample.presentation.gallery.adapter

import com.example.gallerysample.data.repository.FileType

sealed class GalleryItem {

    abstract val isLoading: Boolean
    abstract val folderName: String

    abstract fun areItemsTheSame(other: GalleryItem): Boolean

    abstract fun areContentsTheSame(other: GalleryItem): Boolean

    data class Folder(
        override val isLoading: Boolean,
        override val folderName: String,
        val files: List<File>,
    ) : GalleryItem() {
        val previewFileType: FileType?
            get() = files.firstOrNull()?.fileType

        override fun areItemsTheSame(other: GalleryItem): Boolean {
            return folderName == (other as? Folder)?.folderName
        }

        override fun areContentsTheSame(other: GalleryItem): Boolean {
            return if (other is Folder) {
                val currentFile = files.firstOrNull()
                val otherFile = other.files.firstOrNull()
                if (currentFile != null && otherFile != null) {
                    isLoading == other.isLoading && currentFile.areContentsTheSame(otherFile)
                } else {
                    true
                }
            } else {
                false
            }
        }
    }

    data class File(
        override val isLoading: Boolean,
        override val folderName: String,
        val filePath: String,
        val fileName: String,
        val fileDate: Long,
        val fileType: FileType,
        val url: String?,
    ) : GalleryItem() {
        override fun areItemsTheSame(other: GalleryItem): Boolean {
            return filePath == (other as? File)?.filePath
        }

        override fun areContentsTheSame(other: GalleryItem): Boolean {
            return if (other is File) {
                isLoading == other.isLoading && url == other.url
                        && filePath == other.filePath
            } else {
                false
            }
        }
    }
}