package com.example.filemanager.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.common.Constants
import com.example.filemanager.common.Constants.TYPE_FOLDER
import com.example.filemanager.common.Constants.TYPE_UNKNOWN
import com.example.filemanager.common.Constants.currentPath
import com.example.filemanager.common.Constants.rootPath
import com.example.filemanager.common.Constants.tempPath
import com.example.filemanager.common.FileViewAdapter
import com.example.filemanager.common.Operations
import com.example.filemanager.common.ResponseState
import com.example.filemanager.model.Search
import com.example.filemanager.viewmodel.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_storage.*
import kotlinx.coroutines.Job

@AndroidEntryPoint
class StorageFragment : Fragment(R.layout.fragment_storage) {

    private var isLoading = false
    private val storageViewModel: StorageViewModel by viewModels()
    private lateinit var fileViewAdapter: FileViewAdapter
    var job: Job? = null


    private val TAG = StorageFragment::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            Log.i(TAG, "onCreate: Back Pressed")
            onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        activity?.application?.onTerminate()
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "onStop: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ")
        setupRecyclerView()

        storageViewModel.files.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseState.Success -> {
                    hideProgressBar()
                    response.data?.let { fileViewAdapter.submitList(it) }
                    if (response.data == null || response.data.isEmpty()) {
                        rv_storage.visibility = View.INVISIBLE
                        storage_tv.visibility = View.VISIBLE
                    }

                    if (currentPath == rootPath || currentPath == tempPath) {
                        storage_search.visibility = View.INVISIBLE
                    } else {
                        storage_search.visibility = View.VISIBLE
                    }
                }
                is ResponseState.Failure -> {
                    hideProgressBar()
                    rv_storage.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
                is ResponseState.Loading -> {
                    showProgressBar()
                }
            }
        }
        storageViewModel.presentFolder.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ResponseState.Success -> {
                    storage_head_tv.text = response.data
                }
                is ResponseState.Failure -> {
                    storage_head_tv.text = getString(R.string.error)
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        loadFiles()
        fileViewAdapter.setOnFileClickListener {
            showProgressBar()
            when (it.mimeType) {
                TYPE_FOLDER -> Operations.openFolder(it).apply {
                    loadFiles()
                }
                TYPE_UNKNOWN -> Operations.openUnknown(it, this.requireActivity())
                else -> {
                    Operations.openFile(it, this.requireActivity())
                    onPause()
                }
            }
        }
        storage_search.setOnClickListener {
            val bundle: Bundle = Bundle().apply {
                putSerializable(getString(R.string.search_data), Search("storage"))
            }
            findNavController().navigate(
                R.id.action_storageFragment_to_searchFragment,
                bundle
            )
        }
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setupRecyclerView() = rv_storage.apply {
        fileViewAdapter = FileViewAdapter(context)
        adapter = fileViewAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadFiles(path: String = Constants.currentPath) {
        storageViewModel.getFiles(path)
        storageViewModel.getFolderName(path, requireActivity().applicationContext)
    }

    private fun onBackPressed() {
        var path = currentPath
        if (currentPath.lastOrNull() == '/') {
            path = path.removeSuffix("/")
        }
        if (path != rootPath) {
            currentPath = path.substring(0, path.lastIndexOf('/') + 1)
            loadFiles(currentPath)
        } else if (path == rootPath) {
            activity?.finish()
        }
    }

    private fun hideProgressBar() {
        storage_progress_bar.visibility = View.INVISIBLE
        storage_tv.visibility = View.INVISIBLE
        rv_storage.visibility = View.VISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        rv_storage.visibility = View.INVISIBLE
        storage_progress_bar.visibility = View.VISIBLE
        isLoading = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        loadFiles(currentPath)
        super.onResume()
    }
}