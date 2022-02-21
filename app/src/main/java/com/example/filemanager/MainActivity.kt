package com.example.filemanager


import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_R = MANAGE_EXTERNAL_STORAGE
        val PERMISSION_Q = arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            ACCESS_MEDIA_LOCATION
        )
        val PERMISSION_OTHER = arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
    }

    val reqCode = 20
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission()
        }
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())


    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission() {
        if (checkPermission()) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + this.packageName)
                    )
                    this.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    this.startActivity(intent)
                }
            }

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            requestPermissions(PERMISSION_Q, reqCode)
        } else {
            requestPermissions(PERMISSION_OTHER, reqCode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(): Boolean {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                return checkSelfPermission(PERMISSION_R) == PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                for (perm in PERMISSION_Q) {
                    if (checkSelfPermission(perm) != PERMISSION_GRANTED)
                        return false
                }
            }
            else -> {
                for (perm in PERMISSION_OTHER) {
                    if (checkSelfPermission(perm) != PERMISSION_GRANTED)
                        return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}