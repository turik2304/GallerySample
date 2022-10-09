package com.example.gallerysample.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreRepository(
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun loadFolders(): List<Folder> {
        return withContext(dispatcher) {
            val photo = loadFiles(
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                pathColumn = MediaStore.Images.ImageColumns.DATA,
                fileNameColumn = MediaStore.Images.Media.DISPLAY_NAME,
                fileDateColumn = MediaStore.Images.Media.DATE_MODIFIED,
                folderColumn = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val video = loadFiles(
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                pathColumn = MediaStore.Video.VideoColumns.DATA,
                fileNameColumn = MediaStore.Video.Media.DISPLAY_NAME,
                fileDateColumn = MediaStore.Video.Media.DATE_MODIFIED,
                folderColumn = MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            )
            (photo + video).groupBy { it.folderName }
                .map { (folderName, fileState) ->
                    Folder(
                        folderName = folderName,
                        files = fileState.sortedByDescending { it.fileDate }
                    )
                }
                .sortedBy { it.folderName }
        }
    }

    private fun loadFiles(
        uri: Uri,
        pathColumn: String,
        fileNameColumn: String,
        fileDateColumn: String,
        folderColumn: String,
    ): List<MediaFile> {
        val projection = arrayOf(pathColumn, fileNameColumn, folderColumn, fileDateColumn)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return buildList {
            try {
                cursor!!.moveToFirst()
                do {
                    val fileName = cursor.getString(cursor.getColumnIndexOrThrow(fileNameColumn))
                    val folderName = cursor.getString(cursor.getColumnIndexOrThrow(folderColumn))
                    val filePath = cursor.getString(cursor.getColumnIndexOrThrow(pathColumn))
                    val fileDate = cursor.getLong(cursor.getColumnIndexOrThrow(fileDateColumn))
                    val fileType = when (uri) {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> FileType.IMAGE
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> FileType.VIDEO
                        else -> FileType.UNKNOWN
                    }
                    val fileState = MediaFile(
                        fileName = fileName,
                        folderName = folderName,
                        filePath = filePath,
                        fileDate = fileDate,
                        fileType = fileType,
                        previewBitmap = getPreviewBitmap(filePath, fileType)
                    )
                    add(fileState)
                } while (cursor.moveToNext())
                cursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getPreviewBitmap(filePath: String, fileType: FileType): Bitmap? {
        val thumbnail = when (fileType) {
            FileType.IMAGE -> MediaStore.Images.Thumbnails.MINI_KIND
            FileType.VIDEO -> MediaStore.Video.Thumbnails.MINI_KIND
            FileType.UNKNOWN -> return null
        }
        return ThumbnailUtils.createVideoThumbnail(filePath, thumbnail)
    }

}