package com.example.filemanager.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.common.FileViewAdapter
import com.example.filemanager.common.Operations
import com.example.filemanager.viewmodel.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_category_list.*

@AndroidEntryPoint
class CategoryListFragment : Fragment(R.layout.fragment_category_list) {

    private val categoryViewModel: CategoryViewModel by viewModels()
    private val args: CategoryListFragmentArgs by navArgs()
    private lateinit var fileViewAdapter: FileViewAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        val type = args.type
        fileViewAdapter.setOnFileClickListener {
            Operations.openFile(it, this.requireActivity())
        }
        when (type) {
            "video" -> loadVideos()
            "image" -> loadImages()
            "download" -> loadDownloads()
            "document" -> loadDocuments()
            "audio" -> loadAudios()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadDocuments() {
        categoryViewModel.getDocuments(requireContext())
        categoryViewModel.documents.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
        }
    }

    private fun setupRecyclerView() = rv_category_list.apply {
        fileViewAdapter = FileViewAdapter(context)
        adapter = fileViewAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadVideos() {
        categoryViewModel.getVideos(requireContext())
        categoryViewModel.videos.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
        }
    }

    private fun loadImages() {
        categoryViewModel.getImages(requireContext())
        categoryViewModel.images.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
        }
    }

    private fun loadDownloads() {
        categoryViewModel.getDownloads(requireContext())
        categoryViewModel.downloads.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
        }
    }

    private fun loadAudios() {
        categoryViewModel.getAudios(requireContext())
        categoryViewModel.audios.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
        }
    }
}