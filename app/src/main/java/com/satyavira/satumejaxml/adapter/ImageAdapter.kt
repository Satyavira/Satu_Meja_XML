package com.satyavira.satumejaxml.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.data_model.ImageData

class ImageListAdapter() :
    RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {
    var imageData = emptyList<ImageData>()
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = imageData[position]
        holder.imageView.setImageBitmap(
            BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.size)
        )
    }

    override fun getItemCount(): Int {
        return imageData.size
    }
}