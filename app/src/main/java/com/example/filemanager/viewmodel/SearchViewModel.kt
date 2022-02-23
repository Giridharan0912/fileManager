package com.example.filemanager.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.Search
import com.example.filemanager.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: MainRepository
) : ViewModel() {
    var searchFiles = MediatorLiveData<List<FileModel>>()
    fun getSearchFiles(search: String, searchData: Search) = viewModelScope.launch {
        searchFiles.addSource(repo.getSearchFile(search, searchData)) {
            searchFiles.value = it
        }
    }
}