package com.example.gallerysample.di

import com.example.gallerysample.data.repository.DropBoxRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { DropBoxRepository(get(), get()) }
}