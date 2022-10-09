package com.example.gallerysample.di

import com.example.gallerysample.data.network.NetworkBuilder
import com.example.gallerysample.data.network.NetworkConstants
import com.example.gallerysample.data.network.api.ShareFileApi
import com.example.gallerysample.data.network.api.UploadFileApi
import org.koin.dsl.module

val networkModule = module {
    single { NetworkBuilder.buildOkhttp() }
    single {
        NetworkBuilder.buildRetrofit(get(), NetworkConstants.UPLOAD_BASE_URL)
            .create(UploadFileApi::class.java)
    }
    single {
        NetworkBuilder.buildRetrofit(get(), NetworkConstants.SHARE_BASE_URL)
            .create(ShareFileApi::class.java)
    }
}