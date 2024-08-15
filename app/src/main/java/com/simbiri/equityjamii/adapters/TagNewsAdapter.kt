package com.simbiri.equityjamii.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Tag

class TagNewsAdapter(
    private val tags: List<Tag>,
    private val onTagClick: (Tag) -> Unit
) : RecyclerView.Adapter<TagNewsAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)
        val cardView: CardView = itemView as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapters_tag_news, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.tagTextView.text = tag.name
        updateBackgroundColor(holder, tag.isSelected)
        onTagClick(tag)

        holder.itemView.setOnClickListener {
            tag.isSelected = !tag.isSelected
            updateBackgroundColor(holder, tag.isSelected)
            onTagClick(tag)
        }
    }

    override fun getItemCount(): Int = tags.size

    private fun updateBackgroundColor(holder: TagViewHolder, isSelected: Boolean) {
        if (isSelected) {
            holder.tagTextView.background = ContextCompat.getDrawable(holder.itemView.context, R.color.logoColour)

        } else {
            holder.tagTextView.background = ContextCompat.getDrawable(holder.itemView.context, R.color.grey_font)

        }
    }
}
