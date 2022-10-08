package com.example.gallerysample.data.network.api

import com.example.gallerysample.data.network.response.ShareResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ShareFileApi {

    @POST("/2/files/get_temporary_link")
    suspend fun shareFile(@Body path: RequestBody): ShareResponse

}