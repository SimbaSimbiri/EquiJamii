package com.simbiri.equityjamii.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R

class LinksAdapter(private var context: Context, private val links: List<String>) : RecyclerView.Adapter<LinksAdapter.LinkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapters_item_links, parent, false)
        return LinkViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val link = links[position]
        holder.linkTextView.text = link
        holder.itemView.setOnClickListener {

            if (link.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No valid link found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = links.size

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkTextView: TextView = itemView.findViewById(R.id.linkTextView)
    }
}
