package com.example.gallerysample.di

import com.example.gallerysample.presentation.gallery.GalleryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { GalleryViewModel(get(), get()) }
}