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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsDetailFragment

class FypNewsAdapter(var context: Context,options : FirebaseRecyclerOptions<NewsText>) :
    FirebaseRecyclerAdapter<NewsText, RecyclerView.ViewHolder>(options) {

    companion object {
        private const val VIEW_TYPE_BIG_NEWS = 0
        private const val VIEW_TYPE_SMALL_NEWS = 1
    }

    inner class BigNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var currentNewsTextItem: NewsText? = null
        private var  positionItem :  Int= 0

        private var textViewBigNews: TextView = itemView.findViewById(R.id.textViewBigNewsHeadline)
        private var imageViewBigNews: ImageView = itemView.findViewById(R.id.imageViewBigNews)
        private var cardViewHolderBig: CardView = itemView.findViewById(R.id.cardViewFirstFyp)


        fun setDetailsToBigItem(image: String, title: String, allNews: String,position: Int) {
            this.positionItem =  position
            textViewBigNews.text = title
            this.currentNewsTextItem = NewsText(image, title, allNews)

            var layoutParams = cardViewHolderBig.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth
            layoutParams.height = screenWidth * 9 / 16

            cardViewHolderBig.layoutParams = layoutParams


            Glide.with(itemView.context as AppCompatActivity)
                .load(image)
                .into(imageViewBigNews)

        }

        fun onClickListernersToBig() {

            itemView.setOnClickListener(this@BigNewsViewHolder)
        }

        override fun onClick(v: View?) {
            val bottomFragmentDetail = NewsDetailFragment.newInstance(currentNewsTextItem!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            bottomFragmentDetail.show(transaction, bottomFragmentDetail.tag)
        }
    }


    inner class SmallNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionItem: Int = 0
        private var currentNewsTextItem: NewsText? = null

        private var textNewsForYou: TextView = itemView.findViewById(R.id.textViewForYou)
        private var imageViewForYou: ImageView = itemView.findViewById(R.id.imageViewForYou)
        private var cardViewHolder: CardView = itemView.findViewById(R.id.cardViewSmallFyp)
        private var cardViewImage: CardView = itemView.findViewById(R.id.cardViewImage)


        fun setDetailsToSmall(image: String, title: String, allNews: String,position: Int) {
            this.positionItem =  position

            textNewsForYou.text = title
            this.currentNewsTextItem = NewsText(image, title, allNews)

            var layoutParamsCardView = cardViewHolder.layoutParams
            var layoutParamsCardViewImage = cardViewImage.layoutParams
            var layoutParamsTextView = textNewsForYou.layoutParams


            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsCardView.width = screenWidth
            layoutParamsCardView.height = screenWidth * 1 / 3
            layoutParamsCardViewImage.width = screenWidth * 1 / 3 - 10
            layoutParamsCardViewImage.height = screenWidth * 1 / 3 - 10
            layoutParamsTextView.width = screenWidth - layoutParamsCardViewImage.width - 100
            layoutParamsTextView.height = layoutParamsCardView.height

            cardViewHolder.layoutParams = layoutParamsCardView
            cardViewImage.layoutParams = layoutParamsCardViewImage
            textNewsForYou.layoutParams = layoutParamsTextView




            Glide.with(itemView.context as AppCompatActivity)
                .load(image)
                .into(imageViewForYou)


        }


        fun setOnClickListernerstosmall() {

            itemView.setOnClickListener(this@SmallNewsViewHolder)
        }

        override fun onClick(v: View?) {
            val bottomFragmentDetail = NewsDetailFragment.newInstance(currentNewsTextItem!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            bottomFragmentDetail.show(transaction, bottomFragmentDetail.tag)
        }


    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_BIG_NEWS
        } else {
            VIEW_TYPE_SMALL_NEWS
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view1 =
            LayoutInflater.from(context).inflate(R.layout.adapters_fyp_big_item, parent, false)
        val view2 =
            LayoutInflater.from(context).inflate(R.layout.adapters_fyp_small_item, parent, false)

        return when (viewType) {
            VIEW_TYPE_BIG_NEWS -> {
                BigNewsViewHolder(view1)
            }

            VIEW_TYPE_SMALL_NEWS -> {
                SmallNewsViewHolder(view2)
            }

            else -> {
                throw IllegalArgumentException("Invalid view Type")
            }
        }
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        model: NewsText
    ) {
        if (viewHolder is BigNewsViewHolder) {
            viewHolder.setDetailsToBigItem(model.image, model.title, model.allNews,position)
            viewHolder.onClickListernersToBig()

        } else if (viewHolder is SmallNewsViewHolder) {
            viewHolder.setDetailsToSmall(model.image, model.title, model.allNews,position)
            viewHolder.setOnClickListernerstosmall()
        }
    }

}

