package com.example.filemanager.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import com.alphaverse.grocerymart.common.DispatchCoroutineProviders
import com.example.filemanager.common.Constants.TYPE_AUDIO
import com.example.filemanager.common.Constants.TYPE_DOWNLOAD
import com.example.filemanager.common.Constants.TYPE_FOLDER
import com.example.filemanager.common.Constants.TYPE_IMAGE
import com.example.filemanager.common.Constants.TYPE_UNKNOWN
import com.example.filemanager.common.Constants.TYPE_VIDEO
import com.example.filemanager.common.Constants.currentPath
import com.example.filemanager.common.Constants.rootPath
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.Search
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val dispatcher: DispatchCoroutineProviders
) {
    private val TAG = MainRepository::class.java.simpleName

    suspend fun getSearchFile(search: String, searchData: Search): List<FileModel> {
        return withContext(dispatcher.io) {
            val result = mutableListOf<File>()
            when (searchData.searchArea) {
                "category" -> {
                    File(rootPath).walk().takeWhile { this.isActive }.forEach {

                        if (it.name.lowercase(Locale.ROOT)
                                .contains(search.lowercase(Locale.ROOT)) && it.path != rootPath
                        )
                            result += it
                    }
                }
                "storage" -> {
                    File(currentPath).walk().takeWhile { this.isActive }.forEach {

                        if (it.name.lowercase(Locale.ROOT)
                                .contains(search.lowercase(Locale.ROOT)) && it.path != currentPath
                        )
                            result += it
                    }
                }
                TYPE_VIDEO -> {
                    val mutableFileList = mutableListOf<FileModel>()
                    getVideoFiles(searchData.context!!).forEach {
                        if (it.name.lowercase(Locale.ROOT)
                                .contains(search.lowercase(Locale.ROOT))
                        ) {
                            mutableFileList += it
                        }
                    }
                    return@withContext mutableFileList
                }
                TYPE_IMAGE -> {
                    val mutableFileList = mutableListOf<FileModel>()
                    getImageFiles(searchData.context!!).forEach {
                        if (it.name.lowercase(Locale.ROOT)
                                .contains(search.lowercase(Locale.ROOT))
                        ) {
                            mutableFileList += it
                        }
                    }
                    return@withContext mutableFileList
                }
                TYPE_AUDIO -> {
                    val mutableFileList = mutableListOf<FileModel>()
                    getAudioFiles(searchData.context!!).forEach {
                        if (it.name.lowercase(Locale.ROOT)
                                .contains(search.lowercase(Locale.ROOT))
                        ) {
                            mutableFileList += it
                        }
                    }
                    return@withContext mutableFileList
                }
                TYPE_DOWNLOAD -> {
                    val mutableFileList = mutableListOf<FileModel>()
                    getDownloadFiles(searchData.context!!).forEach {
                        if (it.name.lowercase(Locale.ROOT)
                                .contains(search.lowercase(Locale.ROOT))
                        ) {
                            mutableFileList += it
                        }
                    }
                    return@withContext mutableFileList
                }
            }
            makeFile(result)

        }
    }

    suspend fun getFolderName(currentPath: String): String {
        return withContext(dispatcher.io) {
            File(currentPath).name

        }
    }

    suspend fun getFiles(currentPath: String): List<FileModel> {
        return withContext(dispatcher.io)
        {
            var files = listOf<File>()
            try {
                files = File(currentPath).listFiles().asList()
            } catch (e: Exception) {
                Log.d(TAG, "getFiles: $e")
            }
            makeFile(files)
        }
    }

    private fun makeFile(fileList: List<File>): List<FileModel> {
        val mutableFileList = mutableListOf<FileModel>()

        if (fileList.isNotEmpty()) {
            for (curr in fileList) {
                val uri = curr.path
                val extension = uri.substring(uri.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT)
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                val name = curr.name
                val lastModified = curr.lastModified()
                when {
                    curr.isDirectory -> mutableFileList += FileModel(
                        name,
                        "${curr.list()?.size} items",
                        uri,
                        TYPE_FOLDER,
                        lastModified
                    )
                    mimeType.isNullOrEmpty() -> mutableFileList += FileModel(
                        name,
                        getSize(curr.length()),
                        uri,
                        TYPE_UNKNOWN,
                        lastModified
                    )
                    else -> mutableFileList += FileModel(
                        name,
                        getSize(curr.length()),
                        uri,
                        mimeType.toString(),
                        lastModified
                    )
                }
            }
        }
        return mutableFileList
    }

    private fun getSize(fileSize: Long): String {
        val GB: Long = (1024 * 1024 * 1024).toLong()
        val MB: Long = (1024 * 1024).toLong()
        val kB: Long = 1024
        val size_in_bytes = fileSize.toDouble()

        if (size_in_bytes > GB)
            return String.format("%.1f", size_in_bytes / GB) + "\u00A0GB"
        else if (size_in_bytes > MB)
            return String.format("%.1f", size_in_bytes / MB) + "\u00A0MB"
        else if (size_in_bytes > kB)
            return String.format("%.1f", size_in_bytes / kB) + "\u00A0kB"
        else
            return String.format("%.1f", size_in_bytes) + "\u00A0B"
    }

    suspend fun getVideoFiles(context: Context): List<FileModel> {
        return withContext(dispatcher.io) {
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE
            )
            val query = context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                null
            )

            val videoList = mutableListOf<FileModel>()
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(idColumn)
                        val videoName = cursor.getString(nameColumn)
                        val dateModified = cursor.getLong(dateModifiedColumn)
                        val size = cursor.getLong(sizeColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val extension = videoName.substring(videoName.lastIndexOf(".") + 1)
                            ?.toLowerCase(Locale.ROOT)
                        val mimeType =
                            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        videoList += FileModel(
                            videoName,
                            getSize(size),
                            contentUri.toString(),
                            mimeType!!,
                            dateModified * 1000,
                            true
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            videoList
        }
    }

    suspend fun getImageFiles(context: Context): List<FileModel> {
        return withContext(dispatcher.io) {

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE
            )


            val query = context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                null
            )

            val imgList = mutableListOf<FileModel>()
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                if (cursor.moveToFirst()) {

                    do {
                        val id = cursor.getLong(idColumn)
                        val imgName = cursor.getString(nameColumn)
                        val dateModified = cursor.getLong(dateModifiedColumn)
                        val size = cursor.getLong(sizeColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val extension = imgName.substring(imgName.lastIndexOf(".") + 1)
                            ?.toLowerCase(Locale.ROOT)
                        val mimeType =
                            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        imgList.add(
                            FileModel(
                                imgName,
                                getSize(size),
                                contentUri.toString(),
                                mimeType!!,
                                dateModified * 1000,
                                true
                            )
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()

            }

            imgList
        }
    }

    suspend fun getAudioFiles(context: Context): List<FileModel> {
        return withContext(dispatcher.io) {

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
            )


            val query = context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                null
            )

            val audioList = mutableListOf<FileModel>()
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                if (cursor.moveToFirst()) {

                    do {
                        val id = cursor.getLong(idColumn)
                        val imgName = cursor.getString(nameColumn)
                        val dateModified = cursor.getLong(dateModifiedColumn)
                        val size = cursor.getLong(sizeColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val extension = imgName.substring(imgName.lastIndexOf(".") + 1)
                            ?.toLowerCase(Locale.ROOT)
                        val mimeType =
                            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        audioList += FileModel(
                            imgName,
                            getSize(size),
                            contentUri.toString(),
                            mimeType!!,
                            dateModified * 1000,
                            true
                        )
                    } while (cursor.moveToNext())
                }
                cursor.close()

            }

            audioList
        }
    }

    suspend fun getDownloadFiles(context: Context): List<FileModel> {
        return withContext(dispatcher.io) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
                val projection = arrayOf(
                    MediaStore.Downloads.DISPLAY_NAME,
                    MediaStore.Downloads._ID,
                    MediaStore.Downloads.DATE_ADDED,
                    MediaStore.Downloads.SIZE
                )

                val query = context.contentResolver.query(
                    collection,
                    projection,
                    null,
                    null,
                    null
                )

                val downloadList = mutableListOf<FileModel>()
                query?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_ADDED)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)

                    if (cursor.moveToFirst()) {
                        do {
                            val id = cursor.getLong(idColumn)
                            val imgName = cursor.getString(nameColumn)
                            val dateModified = cursor.getLong(dateModifiedColumn)
                            val size = cursor.getLong(sizeColumn)
                            val contentUri: Uri = ContentUris.withAppendedId(
                                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                                id
                            )
                            val extension = imgName.substring(imgName.lastIndexOf(".") + 1)
                                ?.toLowerCase(Locale.ROOT)
                            var mimeType =
                                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                            if (mimeType == null) {
                                mimeType = TYPE_UNKNOWN
                            }
                            downloadList += FileModel(
                                imgName,
                                getSize(size),
                                contentUri.toString(),
                                mimeType!!,
                                dateModified * 1000,
                                true
                            )
                        } while (cursor.moveToNext())
                    }
                    cursor.close()

                }

                downloadList
            } else {
                getFiles(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path)
            }
        }
    }


    suspend fun getDocumentFiles(context: Context): List<FileModel> {
        return withContext(dispatcher.io) {
            var files = listOf<File>()
            try {
                files = context.getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS).toList()
            } catch (e: Exception) {

            }
            makeFile(files)
        }
    }


}
