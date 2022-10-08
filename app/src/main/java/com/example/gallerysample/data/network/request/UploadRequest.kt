package com.example.gallerysample.data.network.request

import com.google.gson.annotations.SerializedName

class UploadRequest(
    @SerializedName("autorename")
    val autorename: Boolean = false,
    @SerializedName("mode")
    val mode: String = "add",
    @SerializedName("mute")
    val mute: Boolean = false,
    @SerializedName("path")
    val path: String,
    @SerializedName("strict_conflict")
    val strictConflict: Boolean = false,
)