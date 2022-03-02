package com.example.filemanager.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.common.ResponseState
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.Search
import com.example.filemanager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {
    private val TAG = SearchViewModel::class.java.simpleName
    var searchFiles = MediatorLiveData<ResponseState<List<FileModel>>>()

//    fun getSearchFiles(search: String, searchData: Search) = viewModelScope.launch {
//        val a = async {
//            repo.getSearchFile(search, searchData)
//        }
//        searchFiles.addSource(a.await()) {
//            searchFiles.value = it
//        }
//    }

    fun getSearchFiles(search: String, searchData: Search) = viewModelScope.launch {
        fetchSearchFiles(search, searchData)
    }

    fun getFiles(currentPath: String?) = viewModelScope.launch {
        fetchFiles(currentPath)
    }

    private fun handleFiles(files: List<FileModel>): ResponseState<List<FileModel>> {
        return try {
            ResponseState.Success(files)
        } catch (e: Exception) {
            ResponseState.Failure(message = e.message)
        }
    }

    private suspend fun fetchSearchFiles(search: String, searchData: Search) =
        viewModelScope.launch {
            searchFiles.postValue(ResponseState.Loading())
            val time = measureTimeMillis {
                val repoSearchFiles =
                    async {
                        repo.getSearchFile(search, searchData)
                    }
                searchFiles.postValue(handleFiles(repoSearchFiles.await()))
            }
            Log.d(TAG, "fetchSearchFiles: $time")

        }

    private suspend fun fetchFiles(currentPath: String?) = viewModelScope.launch {
        searchFiles.postValue(ResponseState.Loading())
        val repoFiles = async { repo.getFiles(currentPath!!) }
        searchFiles.postValue(handleFiles(repoFiles.await()))
    }
}