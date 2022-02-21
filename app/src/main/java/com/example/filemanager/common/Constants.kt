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
    var currentPath = rootPath
    const val TYPE_FOLDER = "folder"
    const val TYPE_UNKNOWN = "unknown"
    const val VIDEOS = "videos"


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