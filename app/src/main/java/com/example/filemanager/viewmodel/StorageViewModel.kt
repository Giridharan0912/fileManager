package com.example.filemanager.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.model.FileModel
import com.example.filemanager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {
    var files = MediatorLiveData<List<FileModel>>()

    fun getFiles(currentPath: String) = viewModelScope.launch {
        files.addSource(
            repo.getFiles(currentPath)
        ) {
            files.value = it
        }
    }
}

//    fun getFiles(currentPath: String) = viewModelScope.launch {
//        handleFiles(currentPath)
//    }
//
//    private suspend fun handleFiles(currentPath: String) {
//        files.postValue(Resource.Loading())
//        val op=repo.getFiles(currentPath)
//
//
//    }





