package com.example.gallerysample.data.network.api

import com.example.gallerysample.data.network.response.StartUploadResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UploadFileApi {

    @POST("/2/files/upload")
    suspend fun uploadFile(
        @Header("Dropbox-API-Arg") arg: String,
        @Body request: RequestBody,
    )

    @POST("/2/files/upload_session/start")
    suspend fun startUploadSession(
        @Header("Dropbox-API-Arg") arg: String,
        @Header("Content-Type") contentType: MediaType?,
    ): StartUploadResponse

    @POST("/2/files/upload_session/append_v2")
    suspend fun appendUpload(
        @Header("Dropbox-API-Arg") arg: String,
        @Body request: RequestBody,
    )

    @POST("/2/files/upload_session/finish")
    suspend fun finishUploadSession(
        @Header("Dropbox-API-Arg") arg: String,
        @Header("Content-Type") contentType: MediaType?,
    )

}