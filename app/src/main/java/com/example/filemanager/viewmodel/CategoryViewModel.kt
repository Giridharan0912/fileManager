package com.example.filemanager.viewmodel

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.common.ResponseState
import com.example.filemanager.model.FileModel
import com.example.filemanager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {
    var presentFolder=MediatorLiveData<ResponseState<String>>()
    var files = MediatorLiveData<ResponseState<List<FileModel>>>()

    fun getFiles(context: Context, type: String) = viewModelScope.launch {
        fetchFile(context, type)
    }

    private suspend fun getVideos(context: Context) = viewModelScope.launch {
        val repoFiles = async { repo.getVideoFiles(context) }
        files.postValue(handleFiles(repoFiles.await()))
    }

    private suspend fun getImages(context: Context) = viewModelScope.launch {
        val repoFiles = async { repo.getImageFiles(context) }
        files.postValue(handleFiles(repoFiles.await()))
    }

    private suspend fun getAudios(context: Context) = viewModelScope.launch {
        val repoFiles = async { repo.getAudioFiles(context) }
        files.postValue(handleFiles(repoFiles.await()))
    }

    private suspend fun getDownloads(context: Context) = viewModelScope.launch {
        val repoFiles = async { repo.getDownloadFiles(context) }
        files.postValue(handleFiles(repoFiles.await()))
    }

    fun getDocuments(context: Context) = viewModelScope.launch {
        val repoFiles = async { repo.getDocumentFiles(context) }
        files.postValue(handleFiles(repoFiles.await()))
    }


    private fun handleFiles(files: List<FileModel>): ResponseState<List<FileModel>> {
        return try {
            ResponseState.Success(files)
        } catch (e: Exception) {
            ResponseState.Failure(message = e.message)
        }
    }

    private fun fetchFile(context: Context, type: String) = viewModelScope.launch {
        files.postValue(ResponseState.Loading())
        when (type) {
            "video" -> getVideos(context)
            "image" -> getImages(context)
            "audio" -> getAudios(context)
            "download" -> getDownloads(context)
            "document" -> getDocuments(context)
        }

    }
}