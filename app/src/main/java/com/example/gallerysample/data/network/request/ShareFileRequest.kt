package com.example.gallerysample.data.network.request

import com.google.gson.annotations.SerializedName

class ShareFileRequest(
    @SerializedName("path")
    val path: String,
)