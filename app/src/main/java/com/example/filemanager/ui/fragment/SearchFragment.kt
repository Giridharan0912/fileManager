package com.example.filemanager.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.common.Constants.TIME_DELAY
import com.example.filemanager.common.FileViewAdapter
import com.example.filemanager.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var isLoading = false
    private val args: SearchFragmentArgs by navArgs()
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var fileViewAdapter: FileViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        val searchData = args.searchData
        val type = searchData.searchArea
        var job: Job? = null
        et_search.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                showProgressBar()
                delay(TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        searchViewModel.getSearchFiles(editable.toString(), searchData)
                        loadFiles()
                    } else if (editable.toString().isEmpty()) {
                        searchViewModel.searchFiles.value = emptyList()
                        loadFiles()
                    }
                }

            }
        }
        search_back.setOnClickListener {

            if (type == "category") {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToCategoryFragment()
                )
            } else if (type == "video" || type == "audio" || type == "image" || type == "download") {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToCategoryListFragment(type)
                )
            } else if (type == "storage") {
                findNavController().navigate(
                    SearchFragmentDirections.actionSearchFragmentToStorageFragment()
                )
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadFiles() {
        searchViewModel.searchFiles.observe(viewLifecycleOwner) {
            fileViewAdapter.submitList(it)
            hideProgressBar()
        }
    }

    private fun setupRecyclerView() = rv_search.apply {
        fileViewAdapter = FileViewAdapter(context)
        adapter = fileViewAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun hideProgressBar() {
        search_progress_bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        search_progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

}