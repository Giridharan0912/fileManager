package com.example.filemanager.common

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.example.filemanager.common.Constants.TYPE_FOLDER
import com.example.filemanager.common.Constants.currentPath
import com.example.filemanager.model.FileModel
import java.io.File

object Operations {

    fun openFolder(file: FileModel) {
        if (file.mimeType == TYPE_FOLDER) {
            currentPath = file.path
        }
    }

    fun openUnknown(file: FileModel, activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_VIEW)

        intent.setDataAndType(
            FileProvider.getUriForFile(
                activity.applicationContext, activity.applicationContext.packageName + ".provider",
                File(file.path)
            ), "*/*"
        )

        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        activity.startActivity(intent)
    }

    fun openFile(file: FileModel, activity: FragmentActivity) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (!file.mediaStoreFile) {
            try {
                intent.setDataAndType(
                    FileProvider.getUriForFile(
                        activity.applicationContext,
                        "${activity.applicationContext.packageName}.provider",
                        File(file.path)
                    ), file.mimeType
                )
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    activity.applicationContext,
                    "Supported viewer not available",
                    Toast.LENGTH_SHORT
                )
            }


        } else {
            try {
                intent.setDataAndType(
                    file.path.toUri(), file.mimeType
                )
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    activity.applicationContext,
                    "Supported viewer not available",
                    Toast.LENGTH_SHORT
                )
            }
        }


    }

    @JvmStatic
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                return if ("primary".equals(type, ignoreCase = true)) {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else { // non-primary volumes e.g sd card
                    var filePath = "non"
                    //getExternalMediaDirs() added in API 21
                    val external = context.externalMediaDirs
                    for (f in external) {
                        filePath = f.absolutePath
                        if (filePath.contains(type)) {
                            val endIndex = filePath.indexOf("Android")
                            filePath = filePath.substring(0, endIndex) + split[1]
                        }
                    }
                    filePath
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: java.lang.Exception) {
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


}