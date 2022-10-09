package com.example.gallerysample.presentation.gallery

import com.example.gallerysample.data.repository.Folder
import com.example.gallerysample.presentation.base.BaseAction
import com.example.gallerysample.presentation.base.BaseSideEffect

sealed class Action : BaseAction {
    object LoadMediaFiles : Action()
    data class OpenFolder(val folderName: String) : Action()
    object CloseFolder : Action()
    data class UploadFile(val folderName: String, val filePath: String) : Action()
}

sealed class Change {
    object LoadFilesStarted : Change()
    data class FilesLoaded(val folders: List<Folder>) : Change()
    object Error : Change()

    data class FolderOpened(val folderName: String) : Change()
    object FolderClosed : Change()

    data class UploadFileStarted(val folderName: String, val filePath: String) : Change()
    data class UploadFileSuccess(val folderName: String, val filePath: String, val url: String) : Change()
    data class UploadFileError(val folderName: String, val filePath: String) : Change()
}

object BackPressSideEffect : BaseSideEffect