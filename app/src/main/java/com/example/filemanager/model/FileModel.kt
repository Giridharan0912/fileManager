package com.example.filemanager.model

data class FileModel(
    val name: String,
    val info: String,
    val path: String,
    val mimeType: String,
    val lastModified: Long,
    var mediaStoreFile: Boolean = false
) {

}
