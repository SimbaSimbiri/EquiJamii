package com.simbiri.equityjamii

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class NewsText(val imageId: Int, val textHeadLine: String, val isBookmarked: Boolean)

object NewsToday {

    private val imageList = arrayOf(
        R.drawable.europeanu_talks_image,
        R.drawable.equity_assurance,
        R.drawable.wings_to_fly_girls,
        R.drawable.jamesshakinghands,
        R.drawable.elimu_talk,
        R.drawable.stanfordmba,
        R.drawable.good_profile
    )

    private val textLineList =  arrayOf(
        "\tEU officials grace Equity Group Holdings",
        "\tEquity maintains outstanding financial perfomance",
        "\tWings to Fly Scholars fly abroad",
        "\tEquity Group  registers  21% growth in total assets",
        "\tElimu Scholarship Program Day",
        "\tStanford MBA influential visit to the bank",
        "\tBehind the  scenes of EquityJamii"

    )

    var newsTextList : ArrayList<NewsText>? =  null
    get() {
        if (field!= null)

            return  field
        field =  ArrayList()

        for (imagePosition in imageList.indices){
            val imageId = imageList[imagePosition]
            val textLine =  textLineList[imagePosition]
            val newsText =  NewsText(imageId, textLine, false)
            field!!.add(newsText)
        }

        return field

    }

    var isBookMarkedBool : MutableList<NewsText> = mutableListOf()


}

class NewsAdapter(var context: Context, var newsTextList: List<NewsText>): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class  NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun setDatatoItem(newsText: NewsText, position: Int) {
            this.positionItem = position
            this.currentNewsTextItem = newsText

            textNewsHeadline.text = newsText.textHeadLine
            imageNewsHeadline.setImageResource(newsText.imageId)

        }

        private var positionItem:Int = 1
        private var currentNewsTextItem : NewsText ? = null

        private  var textNewsHeadline : TextView =  itemView.findViewById(R.id.textViewHeadline)
        private  var imageNewsHeadline : ImageView = itemView.findViewById(R.id.imageViewNews)
        private  var bookMark: ImageView = itemView.findViewById(R.id.imageViewbkMark)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.newsitemsample, parent, false)

        return NewsViewHolder( view)
    }

    override fun onBindViewHolder(newsViewholder: NewsViewHolder, position: Int) {

        val newsTextInstance =  newsTextList[position]

        newsViewholder.setDatatoItem(newsTextInstance, position)
    }

    override fun getItemCount(): Int {
        
    return  newsTextList.size
    }
}
