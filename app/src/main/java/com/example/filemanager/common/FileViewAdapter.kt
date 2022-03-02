package com.example.filemanager.common

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.filemanager.R
import com.example.filemanager.common.Constants.TYPE_APPLICATION
import com.example.filemanager.common.Constants.TYPE_AUDIO
import com.example.filemanager.common.Constants.TYPE_DOCUMENT
import com.example.filemanager.common.Constants.TYPE_FOLDER
import com.example.filemanager.common.Constants.TYPE_IMAGE
import com.example.filemanager.common.Constants.TYPE_PDF
import com.example.filemanager.common.Constants.TYPE_SHEET
import com.example.filemanager.common.Constants.TYPE_VIDEO
import com.example.filemanager.common.Constants.TYPE_ZIP
import com.example.filemanager.model.FileModel
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File


class FileViewAdapter(context: Context) :
    RecyclerView.Adapter<FileViewAdapter.FileViewHolder>() {
    private val TAG = FileViewAdapter::class.java.simpleName

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


    private fun decideFormatThumbnail(fileType: FileModel): Bitmap? = try {
        if (!fileType.mediaStoreFile) {
            storageThumbnail(fileType)
        } else {
            mediaStoreThumbnail(fileType)
        }
    } catch (e: Exception) {
        Log.d(TAG, "decideFormatThumbnail: $e")
        BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_icon_doc
        )

    }

    private fun mediaStoreThumbnail(fileType: FileModel): Bitmap? =
        if (fileType.mimeType == TYPE_FOLDER) {
            getBitmap(applicationContext, R.drawable.ic_icon_folder)
        } else if (fileType.mimeType.startsWith(TYPE_IMAGE)) {
            val uri: Uri = fileType.path.toUri()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                applicationContext.contentResolver.loadThumbnail(uri, Size(480, 480), null)
            } else {
                TODO()
            }
        } else if (fileType.mimeType.startsWith(TYPE_VIDEO)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uri: Uri = fileType.path.toUri()
                applicationContext.contentResolver.loadThumbnail(uri, Size(480, 480), null)

            } else {
                TODO()
            }
        } else if (fileType.mimeType.startsWith(TYPE_AUDIO)) {
            getBitmap(applicationContext, R.drawable.ic_icon_audio)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_PDF
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_pdf)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_SHEET
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_doc)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_DOCUMENT
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_doc)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith("ppt")) {
            getBitmap(applicationContext, R.drawable.ic_icon_ppt)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_ZIP
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_zip)
        } else {
            getBitmap(applicationContext, R.drawable.ic_icon_doc)
        }


    private fun storageThumbnail(fileType: FileModel): Bitmap? =
        if (fileType.mimeType == TYPE_FOLDER) {
            getBitmap(applicationContext, R.drawable.ic_icon_folder)
        } else if (fileType.mimeType.startsWith(TYPE_IMAGE)) {
            BitmapFactory.decodeFile(fileType.path)
        } else if (fileType.mimeType.startsWith(TYPE_VIDEO)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(File(fileType.path), Size(480, 480), null)
            } else {
                ThumbnailUtils.createVideoThumbnail(
                    fileType.path,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
            }
        } else if (fileType.mimeType.startsWith(TYPE_AUDIO)) {
            getBitmap(applicationContext, R.drawable.ic_icon_audio)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_PDF
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_pdf)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_SHEET
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_doc)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_DOCUMENT
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_doc)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith(
                TYPE_ZIP
            )
        ) {
            getBitmap(applicationContext, R.drawable.ic_icon_zip)
        } else if (fileType.mimeType.startsWith(TYPE_APPLICATION) && fileType.mimeType.endsWith("ppt")) {
            getBitmap(applicationContext, R.drawable.ic_icon_ppt)
        } else {
            getBitmap(applicationContext, R.drawable.ic_icon_doc)
        }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        vectorDrawable.draw(canvas)
        Log.e(TAG, "getBitmap: 1")
        return bitmap
    }

    private fun getBitmap(context: Context, drawableId: Int): Bitmap? {
        Log.e(TAG, "getBitmap: 2")
        val drawable = ContextCompat.getDrawable(context, drawableId)
        return if (drawable is BitmapDrawable) {
            BitmapFactory.decodeResource(context.resources, drawableId)
        } else if (drawable is VectorDrawable) {
            (drawable as VectorDrawable?)?.let { getBitmap(it) }
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }
}
