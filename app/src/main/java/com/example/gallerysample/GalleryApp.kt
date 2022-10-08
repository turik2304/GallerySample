package com.example.gallerysample

import android.app.Application
import com.example.gallerysample.di.diModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GalleryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initDi()
    }

    private fun initDi() {
        startKoin {
            androidLogger()
            androidContext(this@GalleryApp)
            modules(diModules)
        }
    }
}