package com.example.gallerysample

import android.app.Application
import android.content.Context
import com.example.gallerysample.di.diModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GalleryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initDi()
    }

    private fun initDi() {
        startKoin {
            androidLogger()
            androidContext(this@GalleryApp)
            modules(diModules)
        }
    }

    companion object {
        lateinit var appContext: Context
    }

}