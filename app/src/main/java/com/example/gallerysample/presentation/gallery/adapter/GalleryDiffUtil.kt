package com.example.gallerysample.presentation.gallery.adapter

import androidx.recyclerview.widget.DiffUtil

class GalleryDiffUtil(
    private val oldList: List<GalleryItem>,
    private val newList: List<GalleryItem>,
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areItemsTheSame(newList[newItemPosition])
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areContentsTheSame(newList[newItemPosition])
    }
}