package com.simbiri.equityjamii.adapters

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.ui.main_activity.news_page.AddNewsFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsDetailFragment

class NewsAdapter(var context: Context, var newsList: List<NewsText>, var editable: Boolean) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var positionItem: Int = 0
        private var currentNewsTextItem: NewsText? = null

        var textNewsHeadlineView: TextView = itemView.findViewById(R.id.textViewHeadline)
        var imageNewsHeadlineView: ImageView = itemView.findViewById(R.id.imageViewHeadNews)
        var editNewsImageView: ImageView = itemView.findViewById(R.id.editNews)

        fun editNews(editable: Boolean) {
            if (editable) {
                editNewsImageView.visibility = View.VISIBLE
                editNewsImageView.setOnClickListener {
                    val editNewsFrag = AddNewsFragment.newInstance(this.currentNewsTextItem)
                    val transaction =
                        (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    editNewsFrag.show(transaction, editNewsFrag.tag)
                }
            }
        }

        fun setOnclickListeners() {
            imageNewsHeadlineView.setOnClickListener {

                val bottomFragmentDetail = NewsDetailFragment.newInstance(currentNewsTextItem!!)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                bottomFragmentDetail.show(transaction, bottomFragmentDetail.tag)
            }


        }


        fun setData(newsInstance: NewsText, position: Int) {
            this.positionItem = position
            textNewsHeadlineView.text = newsInstance.title
            this.currentNewsTextItem = newsInstance

            val layoutParams = imageNewsHeadlineView.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth - 20
            layoutParams.height = screenWidth * 9 / 16

            imageNewsHeadlineView.layoutParams = layoutParams

            Glide.with(itemView.context as AppCompatActivity)
                .load(newsInstance.imageDescList[0].image)
                .into(imageNewsHeadlineView)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_news_item, parent, false)


        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsInstance = newsList[position]
        holder.setData(newsInstance, position)
        holder.setOnclickListeners()
        holder.editNews(editable)
    }

}
