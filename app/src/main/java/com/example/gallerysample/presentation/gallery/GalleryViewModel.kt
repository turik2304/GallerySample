package com.example.gallerysample.presentation.gallery

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.gallerysample.data.network.NetworkBuilder
import com.example.gallerysample.data.repository.DropBoxRepository
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
) : BaseViewModel<Action, State, SideEffect>() {

    override val initialState: State
        get() = State.Loading

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.LoadFilesStarted -> {
                State.Loading
            }
            is Change.FileLoaded -> {
                val file = GalleryItem.File(
                    isLoading = false,
                    fileName = change.mediaFile.fileName,
                    folderName = change.mediaFile.folderName,
                    filePath = change.mediaFile.filePath,
                    fileDate = change.mediaFile.fileDate,
                    fileType = change.mediaFile.fileType,
                    url = null
                )
                if (state is State.Content) {
                    state.copy(state.folders.appendFile(file))
                } else {
                    val folder = GalleryItem.Folder(
                        isLoading = false,
                        folderName = file.folderName,
                        files = listOf(file)
                    )
                    State.Content(folders = listOf(folder), openedFolderName = null)
                }
            }
            is Change.Error -> {
                State.Error
            }
            is Change.FolderOpened -> {
                if (state is State.Content) {
                    state.copy(openedFolderName = change.folderName)
                } else {
                    state
                }
            }
            is Change.FolderClosed -> {
                when {
                    state is State.Content && state.openedFolder == null -> {
                        viewModelScope.launch { sideEffects.emit(SideEffect.BackPress) }
                        state
                    }
                    state is State.Content && state.openedFolder != null -> {
                        state.copy(openedFolderName = null)
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
            .onEach {
                viewModelScope.launch { mediaStoreRepository.loadFolders() }
            }
            .flatMapMerge { mediaStoreRepository.filesFlow }
            .map { Change.FileLoaded(it) }
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
                        emit(Change.UploadFileSuccess(action.folderName, action.filePath, url))
                    }
                } catch (e: Exception) {
                    Log.d(TAG, e.stackTraceToString())
                    if (e is NetworkBuilder.OAuthError) {
                        sideEffects.emit(SideEffect.AuthError)
                    }
                    flowOf(Change.UploadFileError(action.folderName, action.filePath))
                }
            }
    }

    private fun List<GalleryItem.Folder>.appendFile(file: GalleryItem.File): List<GalleryItem.Folder> {
        var isFileAdded = false
        val folders = this.map { folder ->
            if (folder.folderName == file.folderName && !folder.files.contains(file)) {
                val updatedFiles = (folder.files + file).sortedByDescending { it.fileDate }
                isFileAdded = true
                folder.copy(files = updatedFiles)
            } else {
                folder
            }
        }
        return if (isFileAdded) {
            folders
        } else {
            (this + GalleryItem.Folder(isLoading = false, folderName = file.folderName, listOf(file))).sortedBy { it.folderName }
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