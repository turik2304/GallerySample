package com.example.gallerysample.presentation.gallery

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.gallerysample.data.repository.DropBoxRepository
import com.example.gallerysample.data.repository.FileType
import com.example.gallerysample.data.repository.MediaStoreRepository
import com.example.gallerysample.presentation.base.BaseViewModel
import com.example.gallerysample.presentation.base.Reducer
import com.example.gallerysample.presentation.gallery.adapter.GalleryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class GalleryViewModel(
    private val dropBoxRepository: DropBoxRepository,
    private val mediaStoreRepository: MediaStoreRepository,
) : BaseViewModel<Action, State, BackPressSideEffect>() {

    override val initialState: State
        get() = State.Loading

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.LoadFilesStarted -> {
                State.Loading
            }
            is Change.FilesLoaded -> {
                val folders = change.folders.map {
                    val files = it.files.map { file ->
                        GalleryItem.File(
                            isLoading = false,
                            fileName = file.fileName,
                            folderName = file.folderName,
                            filePath = file.filePath,
                            isVideo = file.fileType == FileType.VIDEO,
                            previewBitmap = file.previewBitmap,
                            url = null
                        )
                    }
                    GalleryItem.Folder(
                        isLoading = false,
                        folderName = it.folderName,
                        files = files
                    )
                }
                State.Content(folders = folders, openedFolder = null)
            }
            is Change.Error -> {
                State.Error
            }
            is Change.FolderOpened -> {
                if (state is State.Content) {
                    val openFolder = state.folders.find { it.folderName == change.folderName }!!
                    state.copy(openedFolder = openFolder)
                } else {
                    state
                }
            }
            is Change.FolderClosed -> {
                when {
                    state is State.Content && state.openedFolder == null -> {
                        viewModelScope.launch { sideEffects.emit(BackPressSideEffect) }
                        state
                    }
                    state is State.Content && state.openedFolder != null -> {
                        state.copy(openedFolder = null)
                    }
                    else -> {
                        state
                    }
                }
            }
            is Change.UploadFileStarted -> {
                if (state is State.Content) {
                    val updatedFolders = state.folders.updateFileState(
                        folderName = change.folderName,
                        filePath = change.filePath,
                        updatedIsLoading = true,
                        updatedUrl = null
                    )
                    state.copy(updatedFolders)
                } else {
                    state
                }
            }
            is Change.UploadFileSuccess -> {
                if (state is State.Content) {
                    val updatedFolders = state.folders.updateFileState(
                        folderName = change.folderName,
                        filePath = change.filePath,
                        updatedIsLoading = false,
                        updatedUrl = change.url
                    )
                    state.copy(updatedFolders)
                } else {
                    state
                }
            }
            is Change.UploadFileError -> {
                if (state is State.Content) {
                    val updatedFolders = state.folders.updateFileState(
                        folderName = change.folderName,
                        filePath = change.filePath,
                        updatedIsLoading = false,
                        updatedUrl = null
                    )
                    state.copy(updatedFolders)
                } else {
                    state
                }
            }
        }
    }

    init {
        listOf(
            bindActionLoadData(),
            bindActionUploadFile(),
            bindActionOpenFolder(),
            bindActionBack()
        ).merge()
            .scan(initialState, reducer)
            .distinctUntilChanged()
            .catch { Log.d(TAG, it.stackTraceToString()) }
            .onEach(states::emit)
            .launchIn(viewModelScope)
    }

    private fun bindActionLoadData(): Flow<Change> {
        return actions.filterIsInstance<Action.LoadMediaFiles>()
            .map {
                try {
                    val files = mediaStoreRepository.loadFolders()
                    Change.FilesLoaded(files)
                } catch (e: Exception) {
                    Log.d(TAG, e.stackTraceToString())
                    Change.Error
                }
            }
            .onStart { emit(Change.LoadFilesStarted) }
    }

    private fun bindActionOpenFolder(): Flow<Change> {
        return actions.filterIsInstance<Action.OpenFolder>()
            .map { action ->
                Change.FolderOpened(action.folderName)
            }
    }

    private fun bindActionBack(): Flow<Change> {
        return actions.filterIsInstance<Action.CloseFolder>()
            .map { Change.FolderClosed }
    }

    private fun bindActionUploadFile(): Flow<Change> {
        return actions.filterIsInstance<Action.UploadFile>()
            .flatMapMerge<Action.UploadFile, Change> { action ->
                try {
                    flow {
                        emit(Change.UploadFileStarted(action.folderName, action.filePath))
                        val url = dropBoxRepository.uploadFile(File(action.filePath))
                        if (url != null) {
                            emit(Change.UploadFileSuccess(action.folderName, action.filePath, url))
                        } else {
                            emit(Change.UploadFileError(action.folderName, action.filePath))
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, e.stackTraceToString())
                    flowOf(Change.UploadFileError(action.folderName, action.filePath))
                }
            }
    }

    private fun List<GalleryItem.Folder>.updateFileState(
        folderName: String,
        filePath: String,
        updatedIsLoading: Boolean,
        updatedUrl: String?,
    ): List<GalleryItem.Folder> {
        return this.map { folder ->
            if (folder.folderName == folderName) {
                val updatedFiles = folder.files.map { file ->
                    if (file.filePath == filePath) file.copy(isLoading = updatedIsLoading, url = updatedUrl) else file
                }
                folder.copy(files = updatedFiles)
            } else {
                folder
            }
        }
    }

    companion object {
        private const val TAG: String = "GalleryViewModel"
    }

}