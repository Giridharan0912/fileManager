package com.example.filemanager.ui.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.filemanager.R

class DetailsFragment : Fragment(R.layout.fragment_details) {
    companion object{
       private val TAG=DetailsFragment::class.java.simpleName
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }




}
