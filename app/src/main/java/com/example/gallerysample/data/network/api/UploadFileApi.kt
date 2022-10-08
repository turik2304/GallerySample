package com.example.gallerysample.data.network.api

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

}