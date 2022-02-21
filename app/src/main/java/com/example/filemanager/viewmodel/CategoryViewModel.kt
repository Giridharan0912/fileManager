package com.example.filemanager.viewmodel

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.model.FileModel
import com.example.filemanager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {
    var videos = MediatorLiveData<List<FileModel>>()
    var images = MediatorLiveData<List<FileModel>>()
    var audios = MediatorLiveData<List<FileModel>>()
    var downloads = MediatorLiveData<List<FileModel>>()
    var recent = MediatorLiveData<List<FileModel>>()
    var documents = MediatorLiveData<List<FileModel>>()

    fun getVideos(context: Context) = viewModelScope.launch {
        videos.addSource(
            repo.getVideoFiles(context)
        ) {
            videos.value = it
        }
    }

    fun getImages(context: Context) = viewModelScope.launch {
        images.addSource(
            repo.getImageFiles(context)
        ) {
            images.value = it
        }
    }

    fun getAudios(context: Context) = viewModelScope.launch {
        audios.addSource(
            repo.getAudioFiles(context)
        ) {
            audios.value = it
        }
    }

    fun getDownloads(context: Context) = viewModelScope.launch {
        downloads.addSource(
            repo.getDownloadFiles(context)
        ) {
            downloads.value = it
        }
    }

    fun getRecent(context: Context) = viewModelScope.launch {
        recent.addSource(
            repo.getVideoFiles(context)
        ) {
            recent.value = it
        }
    }

    fun getDocuments(context: Context) = viewModelScope.launch {
        documents.addSource(
            repo.getDocumentFiles(context)
        ) {
            documents.value = it
        }
    }
}