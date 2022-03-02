package com.example.filemanager.common

import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Constants {
    private val fileStorage: File = getDirectory("ANDROID_STORAGE", "/storage")
    val rootPath = "${fileStorage.path}/emulated/0"
    val tempPath = "${fileStorage.path}/emulated/0/"
    var currentPath = rootPath
    const val TYPE_FOLDER = "folder"
    const val TYPE_UNKNOWN = "unknown"
    const val TYPE_VIDEO = "video"
    const val TYPE_AUDIO = "audio"
    const val TYPE_IMAGE = "image"
    const val TYPE_DOWNLOAD = "download"
    const val TYPE_DOCUMENT = "document"
    const val TYPE_APPLICATION = "application"
    const val TYPE_PDF = "pdf"
    const val TYPE_SHEET = "sheet"
    const val TYPE_ZIP = "zip"


    private fun getDirectory(variableName: String?, defaultPath: String?): File {
        val path = System.getenv(variableName)
        return if (path == null) File(defaultPath) else File(path)
    }

    fun convertToDate(time: Long): String =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd MMMM yyyy").format(
                Instant.ofEpochMilli(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            )
        } else {
            SimpleDateFormat("dd MMMM yyyy").format(
                Date(time)
            )
        }

}