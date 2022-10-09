package com.example.gallerysample.di

import com.example.gallerysample.data.repository.DropBoxRepository
import com.example.gallerysample.data.repository.MediaStoreRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModule = module {
    single { DropBoxRepository(get(), get()) }
    single { MediaStoreRepository(androidApplication().contentResolver) }
}