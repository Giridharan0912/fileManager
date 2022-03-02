package com.example.filemanager.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.common.FileViewAdapter
import com.example.filemanager.common.Operations
import com.example.filemanager.common.ResponseState
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
        loadFiles(requireContext(), type)
        fileViewAdapter.setOnFileClickListener {
            Operations.openFile(it, this.requireActivity())
        }
        categoryViewModel.files.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseState.Success -> {

                    hideProgressBar()
                    response.data?.let {
                        fileViewAdapter.submitList(it)
                    }
                    if (response.data == null || response.data.isEmpty()) {
                        rv_category_list.visibility = View.INVISIBLE
                        cat_list_tv.visibility = View.VISIBLE
                    }
                }
                is ResponseState.Failure -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
                is ResponseState.Loading -> {
                    showProgressBar()
                }
            }

        }


        category_list_search.setOnClickListener {
            val bundle: Bundle = Bundle().apply {
                putSerializable("searchData", Search(type, requireContext()))
            }
            findNavController().navigate(
                R.id.action_categoryListFragment_to_searchFragment,
                bundle
            )
        }
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setupRecyclerView() = rv_category_list.apply {
        fileViewAdapter = FileViewAdapter(context)
        adapter = fileViewAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFiles(context: Context, type: String) {
        categoryViewModel.getFiles(context, type)
    }


    private fun hideProgressBar() {
        category_list_progress_bar.visibility = View.INVISIBLE
        cat_list_tv.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        category_list_progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

}