package com.example.filemanager.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.common.FileViewAdapter
import com.example.filemanager.common.Operations
import com.example.filemanager.model.Search
import com.example.filemanager.viewmodel.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_category_list.*

@AndroidEntryPoint
class CategoryListFragment : Fragment(R.layout.fragment_category_list) {
    private var isLoading = false
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
        category_list_search.setOnClickListener {
            val bundle: Bundle = Bundle().apply {
                putSerializable("searchData", Search(type,requireContext()))
            }
            findNavController().navigate(
                R.id.action_categoryListFragment_to_searchFragment,
                bundle
            )
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
        showProgressBar()
        categoryViewModel.getVideos(requireContext())
        categoryViewModel.videos.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
            hideProgressBar()
        }
    }

    private fun loadImages() {
        showProgressBar()
        categoryViewModel.getImages(requireContext())
        categoryViewModel.images.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
            hideProgressBar()
        }
    }

    private fun loadDownloads() {
        showProgressBar()
        categoryViewModel.getDownloads(requireContext())
        categoryViewModel.downloads.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
            hideProgressBar()
        }
    }

    private fun loadAudios() {
        showProgressBar()
        categoryViewModel.getAudios(requireContext())
        categoryViewModel.audios.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
            hideProgressBar()
        }
    }

    private fun hideProgressBar() {
        category_list_progress_bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        category_list_progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

}