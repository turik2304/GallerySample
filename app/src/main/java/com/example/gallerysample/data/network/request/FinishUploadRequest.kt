package com.example.gallerysample.data.network.request

import com.google.gson.annotations.SerializedName

class FinishUploadRequest(
    @SerializedName("commit")
    val commit: UploadRequest,
    @SerializedName("cursor")
    val cursor: Cursor,
)