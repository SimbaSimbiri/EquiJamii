package com.simbiri.equityjamii.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R

data class OfficialNews (val headline : String, val officialPreviewText: String)

object OfficialNewsTexts {

    private val headlineList = arrayOf(

        "Employees to receive increase in salaries",
        "New customer interaction guidelines",
        "Different retirement  plans to sign up for",
        "Introducing new official communication platform",
        "The Pamoja entrepreneurship loan  is now available for employees"

    )

    private val officialPreviewTextList = arrayOf(

        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. ",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla.  Etiam gravida, turpis nec pellentesque congue",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu.",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu. Mauris cursus, velit ac dapibus porttitor, velit elit sodales nunc, vitae euismod justo nisi at mauris. Nunc sit amet odio nec metus consectetur hendrerit.\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet euismod purus. Suspendisse potenti. Integer nec aliquet ipsum. Proin eu nisl vitae dui rutrum interdum. Suspendisse potenti. Ut scelerisque, odio ac facilisis feugiat, purus tellus placerat mauris, eget facilisis urna mauris id purus. Phasellus scelerisque odio eu ligula dapibus, vitae feugiat tellus facilisis. Aenean a venenatis lorem. Etiam vehicula, nisi a blandit fringilla, turpis nulla auctor odio, sit amet accumsan ex mi sit amet nulla. Aenean at eros luctus, feugiat lectus eu, tincidunt odio. Mauris semper elit at odio eleifend, at convallis lacus posuere. Suspendisse eu diam dui.\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec lacinia metus nec elit vulputate, vel ultrices dolor iaculis. Sed et ex in purus dignissim consequat. Nam id arcu eget erat elementum pulvinar. Maecenas id tortor et dui posuere mollis non eu justo. Donec rutrum nunc vel quam suscipit pellentesque. Cras ullamcorper feugiat fringilla. Suspendisse maximus justo ligula, id bibendum mauris ultrices vel. Sed malesuada auctor dui, id aliquet tellus pellentesque quis",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu."
    )

    var officialNewsList : ArrayList<OfficialNews>? = null
    get() {

        if (field!=null)
            return field
        field = ArrayList()

        for (headlinePos in headlineList.indices){
            val headline = headlineList[headlinePos]
            val previewText = officialPreviewTextList[headlinePos]
            val officialNews = OfficialNews(headline,previewText)

            field!!.add(officialNews)
        }
        return field
    }
}

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
        val view =  LayoutInflater.from(context).inflate(R.layout.official_news_item, parent,false)

        return  OfficialAdapterViewHolder(view)    }

    override fun onBindViewHolder(officialNewsholder: OfficialAdapterViewHolder, position: Int) {

        val officialNewsInstance : OfficialNews = officialNewsList[position]

        officialNewsholder.setDatatoItem(officialNewsInstance, position)
    }

    override fun getItemCount(): Int  = officialNewsList.size

}