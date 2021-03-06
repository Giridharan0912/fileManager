package com.example.filemanager.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alphaverse.grocerymart.common.DispatchCoroutineProviders
import com.example.filemanager.common.Constants.TYPE_FOLDER
import com.example.filemanager.common.Constants.TYPE_UNKNOWN
import com.example.filemanager.model.FileModel
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject


class MainRepository @Inject constructor(
    val dispatcher: DispatchCoroutineProviders
) {


    suspend fun getFiles(currentPath: String): LiveData<List<FileModel>> {
        return withContext(dispatcher.io)
        {
            var files = listOf<File>()
            try {
                files = File(currentPath).listFiles().asList()
            } catch (e: Exception) {

            }
            makeFile(files)
        }
    }

    private fun makeFile(fileList: List<File>): LiveData<List<FileModel>> {
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
        return MutableLiveData(
            mutableFileList
        )
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

    suspend fun getVideoFiles(context: Context): LiveData<List<FileModel>> {
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
            MutableLiveData(videoList)
        }
    }

    suspend fun getImageFiles(context: Context): LiveData<List<FileModel>> {
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
                        imgList += FileModel(
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

            MutableLiveData(imgList)
        }
    }

    suspend fun getAudioFiles(context: Context): LiveData<List<FileModel>> {
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

            MutableLiveData(audioList)
        }
    }

    suspend fun getDownloadFiles(context: Context): LiveData<List<FileModel>> {
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
                            val mimeType =
                                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
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

                MutableLiveData(downloadList)
            } else {
                getFiles(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path)
            }
        }
    }


    suspend fun getDocumentFiles(context: Context): LiveData<List<FileModel>> {
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
