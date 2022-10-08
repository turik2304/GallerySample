package com.example.gallerysample.data.network.response

import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("fuid")
    val fileUid: String,
)