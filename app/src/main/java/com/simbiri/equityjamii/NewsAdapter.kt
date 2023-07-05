package com.simbiri.equityjamii

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.simbiri.equityjamii.NewsToday.listImageIds

data class NewsText(val imageId: Int, val textHeadLine: String)

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

    private val textLineList = arrayOf(
        "\tEU officials grace Equity Group Holdings",
        "\tEquity maintains outstanding financial perfomance",
        "\tWings to Fly Scholars fly abroad",
        "\tEquity Group  registers  21% growth in total assets",
        "\tElimu Scholarship Program Day",
        "\tStanford MBA influential visit to the bank",
        "\tBehind the  scenes of EquityJamii"

    )

    var newsTextList: ArrayList<NewsText>? = null
        get() {
            if (field != null)

                return field
            field = ArrayList()

            for (imagePosition in imageList.indices) {
                val imageId = imageList[imagePosition]
                val textLine = textLineList[imagePosition]
                val newsText = NewsText(imageId, textLine)
                field!!.add(newsText)
            }

            return field

        }

    var isBookMarkedNews: ArrayList<NewsText> = arrayListOf()

    var listImageIds: ArrayList<Int> = arrayListOf()



}


class NewsAdapter(var context: Context, var newsTextList: List<NewsText>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem: Int = -1
        private var currentNewsTextItem: NewsText? = null

        private var textNewsHeadlineView: TextView = itemView.findViewById(R.id.textViewHeadline)
        private var imageNewsHeadlineView: ImageView = itemView.findViewById(R.id.imageViewNews)
        private var bookMark: ImageView = itemView.findViewById(R.id.imageViewbkMark)

        fun setDatatoItem(newsText: NewsText, position: Int) {
            this.positionItem = position
            this.currentNewsTextItem = newsText

            textNewsHeadlineView.text = newsText.textHeadLine
            imageNewsHeadlineView.setImageResource(newsText.imageId)
        }

        fun setOnclickListeners() {

            bookMark.setOnClickListener(this@NewsViewHolder)
        }


        override fun onClick(view: View?) {
            val newsImageId = currentNewsTextItem?.imageId

            if (!listImageIds.contains(newsImageId)) {

                listImageIds.add(newsImageId!!)

                NewsToday.isBookMarkedNews.add(currentNewsTextItem!!)
                Toast.makeText(
                    context,
                    "Added to  For You page",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.newsitemsample, parent, false)

        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(newsViewholder: NewsViewHolder, position: Int) {

        val newsTextInstance = newsTextList[position]

        newsViewholder.setDatatoItem(newsTextInstance, position)
        newsViewholder.setOnclickListeners()

    }

    override fun getItemCount(): Int {

        return newsTextList.size
    }
}









