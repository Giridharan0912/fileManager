package com.example.filemanager.viewmodel

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.R
import com.example.filemanager.common.ResponseState
import com.example.filemanager.model.FileModel
import com.example.filemanager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {
    var presentFolder = MediatorLiveData<ResponseState<String>>()
    var files = MediatorLiveData<ResponseState<List<FileModel>>>()

//    fun getFiles(currentPath: String) = viewModelScope.launch {
//        val a = async { repo.getFiles(currentPath) }
//        files.addSource(
//            a.await(), files::setValue
//        )
//    }

    fun getFolderName(currentPath: String, context: Context) = viewModelScope.launch {
        presentFolder.postValue(ResponseState.Loading())
        try {
            var name = repo.getFolderName(currentPath)
            if (name == "0") {
                name = context.getString(R.string.app_name)
            }
            presentFolder.postValue(ResponseState.Success(name))
        } catch (e: Exception) {
            presentFolder.postValue(ResponseState.Failure(message = e.message))

        }


    }

    fun getFiles(currentPath: String) = viewModelScope.launch {
        files.postValue(ResponseState.Loading())
        fetchFiles(currentPath)
    }


    private fun handleFiles(files: List<FileModel>): ResponseState<List<FileModel>> {
        return try {
            ResponseState.Success(files)
        } catch (e: Exception) {
            ResponseState.Failure(message = e.message)
        }
    }

    private suspend fun fetchFiles(currentPath: String) = viewModelScope.launch {

        val repoFiles = async { repo.getFiles(currentPath) }
        files.postValue(handleFiles(repoFiles.await()))
    }
}





