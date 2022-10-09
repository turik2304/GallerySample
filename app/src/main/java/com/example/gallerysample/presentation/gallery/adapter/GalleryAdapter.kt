package com.example.gallerysample.presentation.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.gallerysample.R
import com.example.gallerysample.data.repository.FileType
import com.example.gallerysample.databinding.ItemFileBinding

class GalleryAdapter(
    private val onItemClick: (filePath: String?, folderName: String, url: String?) -> Unit,
) : RecyclerView.Adapter<GalleryViewHolder>() {

    private var items: List<GalleryItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return GalleryViewHolder(view).apply {
            binding.root.setOnClickListener {
                currentItem?.let {
                    when (it) {
                        is GalleryItem.File -> onItemClick(it.filePath, it.folderName, it.url)
                        is GalleryItem.Folder -> onItemClick(null, it.folderName, null)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(items: List<GalleryItem>) {
        this.items = items
        notifyDataSetChanged()
    }

}

class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding: ItemFileBinding = ItemFileBinding.bind(view)

    var currentItem: GalleryItem? = null

    fun bind(item: GalleryItem) {
        currentItem = item
        with(binding) {
            when (item) {
                is GalleryItem.File -> {
//                    renderPreview(item.fileType)
                    ivFolder.isGone = true
                    ivFileType.isVisible = item.fileType is FileType.Video
                    tvName.text = item.fileName
                    if (item.isLoading) {
                        ivProgress.isVisible = item.isLoading
                        ivStatus.isGone = true
                    } else {
                        ivProgress.isGone = true
                        ivStatus.isVisible = item.url != null
                    }
                }
                is GalleryItem.Folder -> {
//                    renderPreview(item.previewFileType)
                    ivFolder.isVisible = true
                    ivFileType.isGone = true
                    ivProgress.isGone = true
                    ivStatus.isGone = true
                    tvName.text = item.folderName
                }
            }
        }
    }

    private fun renderPreview(fileType: FileType?) {
        when (fileType) {
            is FileType.Image -> {
                binding.ivBackground.setImageURI(fileType.previewUri)
            }
            is FileType.Video -> {
                binding.ivBackground.setImageBitmap(fileType.previewBitmap)
            }
            null, FileType.Unknown -> {
                binding.ivBackground.setImageResource(0)
            }
        }
    }

}