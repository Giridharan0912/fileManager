package com.example.filemanager.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.filemanager.R
import com.example.filemanager.model.FileModel
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File

class FileViewAdapter(context: Context) :
    RecyclerView.Adapter<FileViewAdapter.FileViewHolder>() {
    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val applicationContext = context
    private val differCallback = object : DiffUtil.ItemCallback<FileModel>() {
        override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    fun submitList(list: List<FileModel>) = differ.submitList(list)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_file,
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((FileModel) -> Unit)? = null
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileModel = differ.currentList[position]
        holder.itemView.apply {
            tv_file_name.text = fileModel.name
            tv_file_info.text = fileModel.info
            tv_file_last_modified.text = Constants.convertToDate(fileModel.lastModified)
            setThumbnail(holder, fileModel)
            setOnClickListener {

                onItemClickListener?.let { it(fileModel) }
            }
        }


    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    fun setOnFileClickListener(listener: (FileModel) -> Unit) {
        onItemClickListener = listener
    }


    private fun setThumbnail(
        holder: FileViewHolder,
        fileType: FileModel
    ) {

        Glide.with(holder.itemView).load(decideFormatThumbnail(fileType))
            .centerCrop()
            .diskCacheStrategy(
                DiskCacheStrategy.NONE
            ).into(holder.itemView.file_img_view)
    }


    private fun decideFormatThumbnail(fileType: FileModel): Bitmap? =
        if (!fileType.mediaStoreFile) {
            if (fileType.mimeType == "folder") {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_folder)
            } else if (fileType.mimeType.startsWith("image")) {
                BitmapFactory.decodeFile(fileType.path)
            } else if (fileType.mimeType.startsWith("video")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ThumbnailUtils.createVideoThumbnail(File(fileType.path), Size(480, 480), null)
                } else {
                    ThumbnailUtils.createVideoThumbnail(
                        fileType.path,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )
                }
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("pdf")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_pdf)
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("sheet")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_xls)
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("document")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_docx)
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("zip")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_zip)
            } else {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_unknown)
            }
        } else {
            if (fileType.mimeType == "folder") {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_folder)
            } else if (fileType.mimeType.startsWith("image")) {
                val uri: Uri = fileType.path.toUri()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    applicationContext.contentResolver.loadThumbnail(uri, Size(480, 480), null)
                } else {
                    TODO()
                }
            } else if (fileType.mimeType.startsWith("video")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val uri: Uri = fileType.path.toUri()
                    applicationContext.contentResolver.loadThumbnail(uri, Size(480, 480), null)

                } else {
                    TODO()
                }
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("pdf")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_pdf)
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("sheet")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_xls)
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("document")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_docx)
            } else if (fileType.mimeType.startsWith("application") && fileType.mimeType.endsWith("zip")) {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_zip)
            } else {
                BitmapFactory.decodeResource(applicationContext.resources, R.drawable.icon_unknown)
            }
        }


}
