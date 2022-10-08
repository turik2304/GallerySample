package com.example.gallerysample.data.repository

import com.example.gallerysample.data.network.api.ShareFileApi
import com.example.gallerysample.data.network.api.UploadFileApi
import com.example.gallerysample.data.network.request.ShareFileRequest
import com.example.gallerysample.data.network.request.UploadRequest
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

    suspend fun uploadFile(file: File): String {
        return withContext(dispatcher) {
            val uploadRequest = file.asRequestBody(octetStreamMediaType)
            val path = "/${file.name}"
            val uploadArg = gson.toJson(UploadRequest(path = path))
            uploadFileApi.uploadFile(uploadArg, uploadRequest)
            shareFile(path)
        }
    }

    private suspend fun shareFile(filePath: String): String {
        val shareArg = ShareFileRequest(filePath)
        val shareRequest = gson.toJson(shareArg).toRequestBody(jsonMediaType)
        return shareFileApi.shareFile(path = shareRequest).url
    }

}