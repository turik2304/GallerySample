package com.example.gallerysample.data.network.request

import com.google.gson.annotations.SerializedName

class StartUploadRequest(
    @SerializedName("close")
    val close: Boolean,
    @SerializedName("session_type")
    val sessionType: SessionType,
)

enum class SessionType {
    @SerializedName("sequential")
    SEQUENTIAL,

    @SerializedName("concurrent")
    CONCURRENT
}
