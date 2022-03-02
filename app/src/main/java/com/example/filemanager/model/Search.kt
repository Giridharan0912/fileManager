package com.example.filemanager.model

import android.content.Context
import java.io.Serializable

data class Search(
    val searchArea: String,
    val context: Context? = null
) : Serializable
