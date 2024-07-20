package com.simbiri.equityjamii.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.FileTitle

class LinksAdapter(private var context: Context, val links: MutableList<FileTitle>, val canDelete : Boolean = false) : RecyclerView.Adapter<LinksAdapter.LinkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapters_item_links, parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = links[position]
        holder.linkTextView.text = link.fileTitle

        if (canDelete) holder.deleteLink.visibility  =  View.VISIBLE

        holder.linkTextView.setOnClickListener {

            if (link.fileUri.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.fileUri))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No valid link found!", Toast.LENGTH_SHORT).show()
            }
        }

        holder.deleteLink.setOnClickListener {
            links.remove(link)
            notifyItemRemoved(position)
        }

    }

    override fun getItemCount() = links.size

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
        val deleteLink : ImageView = itemView.findViewById(R.id.deleteLink)
    }
}
