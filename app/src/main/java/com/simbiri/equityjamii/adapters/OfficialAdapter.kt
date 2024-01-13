package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.OfficialNews


class OfficialAdapter (var context: Context, var officialNewsList : ArrayList<OfficialNews>) : RecyclerView.Adapter<OfficialAdapter.OfficialAdapterViewHolder>(){

    inner class OfficialAdapterViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        private var positionItem = 1
        private var currentOfficialItem : OfficialNews? = null

        private var textHeadlineOfficial : TextView = itemView.findViewById(R.id.titleOfficialItem)
        private var textPreviewOfficial : TextView = itemView.findViewById(R.id.previewOfficialItem)

        fun setDatatoItem( officialNewsInstance: OfficialNews,  position: Int ) {
            this.positionItem = positionItem
            this.currentOfficialItem = officialNewsInstance

            textHeadlineOfficial.text = officialNewsInstance.headline
            textPreviewOfficial.text = officialNewsInstance.officialPreviewText
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfficialAdapterViewHolder {
        val view =  LayoutInflater.from(context).inflate(R.layout.adapters_official_item, parent,false)

        return  OfficialAdapterViewHolder(view)    }

    override fun onBindViewHolder(officialNewsholder: OfficialAdapterViewHolder, position: Int) {

        val officialNewsInstance : OfficialNews = officialNewsList[position]

        officialNewsholder.setDatatoItem(officialNewsInstance, position)
    }

    override fun getItemCount(): Int  = officialNewsList.size

}