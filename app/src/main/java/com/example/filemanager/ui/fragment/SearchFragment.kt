package com.example.filemanager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.common.Constants
import com.example.filemanager.common.FileViewAdapter
import com.example.filemanager.common.Operations
import com.example.filemanager.common.ResponseState
import com.example.filemanager.model.Search
import com.example.filemanager.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var isLoading = false
    private val args: SearchFragmentArgs by navArgs()
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var fileViewAdapter: FileViewAdapter
    private var searchPath: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val searchData = args.searchData
        val type = searchData.searchArea
        initSearchFragment(type, searchData)
        searchViewModel.searchFiles.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is ResponseState.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        fileViewAdapter.submitList(it)
                    }
                    if (response.data == null || response.data.isEmpty()) {
                        rv_search.visibility = View.INVISIBLE
                        search_tv.visibility = View.VISIBLE
                    }
                }
                is ResponseState.Failure -> {
                    hideProgressBar()
                    rv_search.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()

                }
                is ResponseState.Loading -> showProgressBar()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }


    private fun hideProgressBar() {
        search_progress_bar.visibility = View.INVISIBLE
        search_tv.visibility = View.INVISIBLE
        rv_search.visibility = View.VISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        search_progress_bar.visibility = View.VISIBLE
        rv_search.visibility = View.INVISIBLE

        isLoading = true
    }

    private fun Fragment.hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun initSearchFragment(type: String, searchData: Search) {
        setupRecyclerView()
        setBackButtonListener(type)
        setSearchBtnListener(searchData)
        setOnFileClickListener()
    }

    private fun setupRecyclerView() = rv_search.apply {
        fileViewAdapter = FileViewAdapter(context)
        adapter = fileViewAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setBackButtonListener(type: String) {
        search_back.setOnClickListener {
            hideKeyboard()
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
    }

    private fun setSearchBtnListener(searchData: Search) {
        var job: Job? = null
        et_search.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        searchViewModel.getSearchFiles(editable.toString(), searchData)
                    } else if (editable.toString().isEmpty()) {
                        searchViewModel.searchFiles.value = ResponseState.Success(emptyList())
                        search_tv.visibility = View.INVISIBLE
                    }
                }
            }
        }

    }

    private fun setOnFileClickListener() {
        hideKeyboard()
        fileViewAdapter.setOnFileClickListener {
            when (it.mimeType) {
                Constants.TYPE_FOLDER -> Operations.openFolder(it).apply {
                    findNavController().navigate(R.id.action_searchFragment_to_storageFragment)
                }
                Constants.TYPE_UNKNOWN -> Operations.openUnknown(it, this.requireActivity())
                else -> Operations.openFile(it, this.requireActivity())
            }
        }
    }
}