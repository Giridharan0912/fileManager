package com.example.filemanager.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.filemanager.R
import kotlinx.android.synthetic.main.fragment_categories.*

class CategoryFragment : Fragment(R.layout.fragment_categories) {
    companion object {
        private val TAG = CategoryFragment::class.java.simpleName
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cv_video.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment("video")
            findNavController().navigate(
                action
            )
        }
        cv_image.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment("image")
            findNavController().navigate(
                action
            )
        }
        cv_downloads.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment("download")
            findNavController().navigate(
                action
            )



        }
        cv_documents.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment("document")
            findNavController().navigate(
                action
            )
        }
        cv_audio.setOnClickListener {
            val action =
                CategoryFragmentDirections.actionCategoryFragmentToCategoryListFragment("audio")
            findNavController().navigate(
                action
            )
        }
        super.onViewCreated(view, savedInstanceState)
    }
}