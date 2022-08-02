package com.example.filemanager.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filemanager.MainActivity
import com.example.filemanager.R
import com.example.filemanager.common.Constants.TYPE_AUDIO
import com.example.filemanager.common.Constants.TYPE_DOCUMENT
import com.example.filemanager.common.Constants.TYPE_DOWNLOAD
import com.example.filemanager.common.Constants.TYPE_IMAGE
import com.example.filemanager.common.Constants.TYPE_VIDEO
import kotlinx.android.synthetic.main.fragment_categories.*

class CategoryFragment : Fragment(R.layout.fragment_categories) {
    companion object {
        private val TAG = "Sample"
    }

    private lateinit var mActivity: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = (activity as MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: categoryFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: categoryFragment")
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: categoryFragment")
        cv_video.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment(TYPE_VIDEO)
            findNavController().navigate(
                action
            )
        }
        cv_image.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment(TYPE_IMAGE)
            findNavController().navigate(
                action
            )
        }
        cv_downloads.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment(
                    TYPE_DOWNLOAD
                )
            findNavController().navigate(
                action
            )


        }
        cv_documents.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment(
                    TYPE_DOCUMENT
                )
            findNavController().navigate(
                action
            )
        }
        cv_audio.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment(TYPE_AUDIO)
            findNavController().navigate(
                action
            )
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: categoryFragment")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: categoryFragment")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: categoryFragment")
    }


}