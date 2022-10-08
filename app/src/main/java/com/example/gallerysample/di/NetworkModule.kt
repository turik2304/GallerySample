package com.example.gallerysample.di

import com.example.gallerysample.data.network.NetworkBuilder
import com.example.gallerysample.data.network.NetworkUrl
import com.example.gallerysample.data.network.api.ShareFileApi
import com.example.gallerysample.data.network.api.UploadFileApi
import org.koin.dsl.module

val networkModule = module {
    single { NetworkBuilder.buildOkhttp() }
    single {
        NetworkBuilder.buildRetrofit(get(), NetworkUrl.UPLOAD_BASE_URL)
            .create(UploadFileApi::class.java)
    }
    single {
        NetworkBuilder.buildRetrofit(get(), NetworkUrl.SHARE_BASE_URL)
            .create(ShareFileApi::class.java)
    }
}