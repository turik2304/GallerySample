package com.example.gallerysample.data.network.response

import com.google.gson.annotations.SerializedName

data class ShareResponse(
    @SerializedName("link")
    val url: String
)