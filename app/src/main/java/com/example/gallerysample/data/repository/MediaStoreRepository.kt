package com.example.gallerysample.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach

class MediaStoreRepository(
    private val contentResolver: ContentResolver,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val _filesFlow: MutableSharedFlow<MediaFile> = MutableSharedFlow(extraBufferCapacity = 100)

    val filesFlow: Flow<MediaFile>
        get() = _filesFlow.onEach { Log.d("MediaStoreRepository", "loaded $it") }
            .onEach { delay(100) }

    private var loadImagesJob: Job? = null

    private var loadVideosJob: Job? = null

    suspend fun loadFolders() {
        try {
            loadImagesJob?.cancel()
            loadVideosJob?.cancel()
            withContext(dispatcher) {
                loadImagesJob = launch {
                    loadFiles(
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        pathColumn = MediaStore.Images.ImageColumns.DATA,
                        fileNameColumn = MediaStore.Images.Media.DISPLAY_NAME,
                        fileDateColumn = MediaStore.Images.Media.DATE_MODIFIED,
                        folderColumn = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                    )
                }
                loadVideosJob = launch {
                    loadFiles(
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        pathColumn = MediaStore.Video.VideoColumns.DATA,
                        fileNameColumn = MediaStore.Video.Media.DISPLAY_NAME,
                        fileDateColumn = MediaStore.Video.Media.DATE_MODIFIED,
                        folderColumn = MediaStore.Video.Media.BUCKET_DISPLAY_NAME
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadFiles(
        uri: Uri,
        pathColumn: String,
        fileNameColumn: String,
        fileDateColumn: String,
        folderColumn: String,
    ) {
        val projection = arrayOf(pathColumn, fileNameColumn, folderColumn, fileDateColumn)
        val sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC"
        val cursor = contentResolver.query(uri, projection, null, null, sortOrder)
        try {
            cursor!!.moveToFirst()
            do {
                val fileName = cursor.getString(cursor.getColumnIndexOrThrow(fileNameColumn))
                val folderName = cursor.getString(cursor.getColumnIndexOrThrow(folderColumn))
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(pathColumn))
                val fileDate = cursor.getLong(cursor.getColumnIndexOrThrow(fileDateColumn))
                val fileType = when (uri) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> FileType.Image(previewUri = Uri.parse(filePath))
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> FileType.Video(previewBitmap = getVideoPreview(filePath))
                    else -> FileType.Unknown
                }
                val mediaFile = MediaFile(
                    fileName = fileName,
                    folderName = folderName,
                    filePath = filePath,
                    fileDate = fileDate,
                    fileType = fileType,
                )
                _filesFlow.emit(mediaFile)
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION")
    private fun getVideoPreview(filePath: String): Bitmap? {
        return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND)
    }

}