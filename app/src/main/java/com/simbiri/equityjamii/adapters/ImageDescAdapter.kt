package com.simbiri.equityjamii.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.ImageDesc

class ImageDescAdapter(
    private val context: Context,
    private val imageDescList: MutableList<ImageDesc>
) : RecyclerView.Adapter<ImageDescAdapter.ImageDescViewHolder>() {

    inner class ImageDescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val editTextDescription: EditText = itemView.findViewById(R.id.editTextDescription)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleteImageDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDescViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapter_item_image_desc, parent, false)
        return ImageDescViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageDescViewHolder, position: Int) {
        val imageDesc = imageDescList[position]
        Glide.with(context).load(Uri.parse(imageDesc.image)).fitCenter().into(holder.imageView)

        holder.editTextDescription.setText(imageDesc.description)
        holder.editTextDescription.addTextChangedListener {
            imageDesc.description = it.toString()
        }
        holder.deleteImage.setOnClickListener {
            deleteImageDesc(imageDesc, position)
        }
    }

    override fun getItemCount(): Int {
        return imageDescList.size
    }

    private fun deleteImageDesc(imageDesc: ImageDesc, position: Int) {
        imageDescList.remove(imageDesc)
        notifyItemRemoved(position)
    }

    fun addImageDesc(imageDesc: ImageDesc) {
        if (imageDescList.size < 5) {
            imageDescList.add(imageDesc)
            notifyItemInserted(imageDescList.size - 1)
        }
    }
}