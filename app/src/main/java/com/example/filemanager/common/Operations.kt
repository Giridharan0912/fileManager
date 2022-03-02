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
import com.example.filemanager.common.Constants.TYPE_AUDIO
import com.example.filemanager.common.Constants.TYPE_FOLDER
import com.example.filemanager.common.Constants.TYPE_IMAGE
import com.example.filemanager.common.Constants.TYPE_VIDEO
import com.example.filemanager.common.Constants.currentPath
import com.example.filemanager.model.FileModel
import java.io.File

object Operations {

    fun openFolder(file: FileModel) {
        if (file.mimeType == TYPE_FOLDER) {
            currentPath = file.path
        }
    }

    fun openSearchFolder(file: FileModel) {
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