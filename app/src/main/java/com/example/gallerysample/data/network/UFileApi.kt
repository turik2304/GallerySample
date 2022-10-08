package com.example.gallerysample.data.network

import com.example.gallerysample.data.network.response.SessionResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UFileApi {

    @FormUrlEncoded
    @POST("/v1/upload/create_session")
    suspend fun createSession(
        @Field("file_size") fileSize: Int,
    ): SessionResponse

}