package com.example.gallerysample.data.repository

import android.net.Uri
import android.provider.MediaStore
import com.example.gallerysample.GalleryApp
import com.example.gallerysample.data.network.api.ShareFileApi
import com.example.gallerysample.data.network.api.UploadFileApi
import com.example.gallerysample.data.network.request.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DropBoxRepository(
    private val shareFileApi: ShareFileApi,
    private val uploadFileApi: UploadFileApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val octetStreamMediaType: MediaType? = "application/octet-stream".toMediaTypeOrNull()

    private val jsonMediaType: MediaType? = "application/json".toMediaTypeOrNull()

    private val gson: Gson = Gson()

    suspend fun uploadFile(file: File): String? {
        return withContext(dispatcher) {
            if (file.length() > MAX_FILE_BYTE_SIZE) {
                uploadLargeFile(file)
            } else {
                uploadSmallFile(file)
            }
            shareFile(file.dropBoxPath())
        }
    }

    private suspend fun uploadLargeFile(file: File) {
        withContext(dispatcher) {
            val startUploadRequest = StartUploadRequest(close = false, sessionType = SessionType.CONCURRENT)
            val startUploadArg = gson.toJson(startUploadRequest)
            val sessionId = uploadFileApi.startUploadSession(startUploadArg, octetStreamMediaType).sessionId
            var offset = 0
            val bytes = splitFile(file)
            bytes.forEachIndexed { index, byteArray ->
                val uploadRequest = byteArray.toRequestBody(octetStreamMediaType)
                val uploadArg = gson.toJson(
                    AppendUploadRequest(
                        close = index == bytes.size - 1,
                        cursor = Cursor(offset = offset, sessionId = sessionId)
                    )
                )
                uploadFileApi.appendUpload(arg = uploadArg, request = uploadRequest)
                offset += byteArray.size
            }
            val finishArg = gson.toJson(
                FinishUploadRequest(
                    commit = UploadRequest(path = file.dropBoxPath()),
                    cursor = Cursor(0, sessionId)
                )
            )
            uploadFileApi.finishUploadSession(arg = finishArg, contentType = octetStreamMediaType)
        }
    }

    private fun splitFile(file: File): List<ByteArray> {
        return buildList {
            file.forEachBlock(CHUNK_BYTE_SIZE) { buffer, bytesRead ->
                if (bytesRead < buffer.size) {
                    val byteArray = ByteArray(bytesRead)
                    buffer.copyInto(byteArray, endIndex = bytesRead)
                    add(byteArray)
                } else {
                    add(buffer)
                }
            }
        }
    }

    private suspend fun uploadSmallFile(file: File) {
        val uploadRequest = file.asRequestBody(octetStreamMediaType)
        val uploadArg = gson.toJson(UploadRequest(path = file.dropBoxPath()))
        uploadFileApi.uploadFile(uploadArg, uploadRequest)
    }

    private suspend fun shareFile(filePath: String): String {
        val shareArg = ShareFileRequest(filePath)
        val shareRequest = gson.toJson(shareArg).toRequestBody(jsonMediaType)
        return shareFileApi.shareFile(path = shareRequest).url
    }

    private fun File.dropBoxPath() = "/${name}"

    companion object {
        private const val MAX_FILE_BYTE_SIZE: Long = 150 * 1024 * 1024
        private const val CHUNK_BYTE_SIZE: Int = 4194304 * 5
    }

}