package com.example.gallerysample.data.network.response

import com.google.gson.annotations.SerializedName

data class StartUploadResponse(
    @SerializedName("session_id")
    val sessionId: String,
)