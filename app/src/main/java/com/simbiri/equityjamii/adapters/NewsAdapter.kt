package com.simbiri.equityjamii.adapters

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsDetailFragment

class NewsAdapter(var context: Context, options: FirebaseRecyclerOptions<NewsText>) : FirebaseRecyclerAdapter<NewsText, NewsAdapter.NewsViewHolder>(options) {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem: Int = 1
        private var currentNewsTextItem: NewsText? = null

         var textNewsHeadlineView: TextView = itemView.findViewById(R.id.textViewHeadline)
         var imageNewsHeadlineView: ImageView = itemView.findViewById(R.id.imageViewHeadNews)
        var cardViewHolder : CardView = itemView.findViewById(R.id.cardItemSample)



        fun setDetails( image: String, title:String, allNews: String){
            textNewsHeadlineView.text = title
            this.currentNewsTextItem = NewsText(image, title, allNews)

            val layoutParams = cardViewHolder.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth
            layoutParams.height = screenWidth * 9 / 16

            cardViewHolder.layoutParams = layoutParams


            Glide.with(itemView.context as AppCompatActivity)
                .load(image)
                .into(imageNewsHeadlineView)

            Log.d("IMAGE URL", image)
        }


        fun setOnclickListeners() {
            itemView.setOnClickListener(this@NewsViewHolder)
        }


        override fun onClick(view: View?) {

            val bottomFragmentDetail = NewsDetailFragment.newInstance(currentNewsTextItem!!)
            val transaction = (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            bottomFragmentDetail.show(transaction, bottomFragmentDetail.tag)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.adapters_news_item, parent, false)


            return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int, model: NewsText) {
        holder.setDetails(model.image, model.title,model.allNews)
        holder.setOnclickListeners()
    }

}









