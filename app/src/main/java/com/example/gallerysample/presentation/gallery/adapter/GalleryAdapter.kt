package com.example.gallerysample.presentation.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.gallerysample.R
import com.example.gallerysample.databinding.ItemFileBinding

class GalleryAdapter(
    private val onItemClick: (filePath: String?, folderName: String) -> Unit,
) : RecyclerView.Adapter<GalleryViewHolder>() {

    private var items: List<GalleryItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return GalleryViewHolder(view).apply {
            binding.root.setOnClickListener {
                currentItem?.let {
                    when (it) {
                        is GalleryItem.File -> onItemClick(it.filePath, it.folderName)
                        is GalleryItem.Folder -> onItemClick(null, it.folderName)
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

    @Suppress("DEPRECATION")
    fun bind(item: GalleryItem) {
        currentItem = item
        if (item.previewBitmap != null) {
            binding.ivBackground.setImageBitmap(item.previewBitmap)
        } else {
            binding.ivBackground.setImageDrawable(null)
        }

        when (item) {
            is GalleryItem.File -> {
                binding.ivFolder.isGone = true
                binding.ivFileType.isVisible = item.isVideo
                binding.tvName.text = item.fileName
                if (item.isLoading) {
                    binding.ivProgress.isVisible = item.isLoading
                    binding.ivStatus.isGone = true
                } else {
                    binding.ivProgress.isGone = true
                    binding.ivStatus.isVisible = item.url != null
                }
            }
            is GalleryItem.Folder -> {
                item.previewBitmap?.let {
                    binding.ivBackground.setImageBitmap(it)
                }
                binding.ivFolder.isVisible = true
                binding.ivFileType.isGone = true
                binding.ivProgress.isGone = true
                binding.ivStatus.isGone = true
                binding.tvName.text = item.folderName
            }
        }
    }

}