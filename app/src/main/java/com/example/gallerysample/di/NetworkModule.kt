package com.example.gallerysample.di

import com.example.gallerysample.BuildConfig
import com.example.gallerysample.data.network.ApiBuilder
import com.example.gallerysample.data.network.UFileApi
import org.koin.dsl.module

val networkModule = module {
    single { ApiBuilder.build(BuildConfig.BASE_URL, UFileApi::class.java) }
}