package com.example.gallerysample.data.network.request

import com.google.gson.annotations.SerializedName

class AppendUploadRequest(
    @SerializedName("close")
    val close: Boolean,
    @SerializedName("cursor")
    val cursor: Cursor
)

class Cursor(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("session_id")
    val sessionId: String
)