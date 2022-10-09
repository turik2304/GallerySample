package com.example.gallerysample.presentation.gallery

import com.example.gallerysample.presentation.base.BaseState
import com.example.gallerysample.presentation.gallery.adapter.GalleryItem

sealed class State : BaseState {

    object Loading : State()

    data class Content(
        val folders: List<GalleryItem.Folder>,
        val openedFolderName: String?,
    ) : State() {
        val openedFolder: GalleryItem.Folder?
            get() = folders.find { it.folderName == openedFolderName }
    }

    object Error : State()
}