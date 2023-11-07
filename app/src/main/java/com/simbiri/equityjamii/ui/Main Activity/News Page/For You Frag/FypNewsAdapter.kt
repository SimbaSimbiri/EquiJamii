package com.simbiri.equityjamii.ui

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
import com.simbiri.equityjamii.R

class FypNewsAdapter(var context: Context, var ForYouNewsList: ArrayList<NewsText>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_BIG_NEWS = 0
        private const val VIEW_TYPE_SMALL_NEWS = 1
    }

    inner class BigNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var positionItem: Int = 0
        private var currentNewsTextItem: NewsText? = null

        private var textViewBigNews: TextView = itemView.findViewById(R.id.textViewBigNewsHeadline)
        private var imageViewBigNews: ImageView = itemView.findViewById(R.id.imageViewBigNews)


        fun setDataToBigItem(forYouNewsInst: NewsText) {

            this.positionItem = position
            this.currentNewsTextItem = forYouNewsInst

            var layoutParams = imageViewBigNews.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth
            layoutParams.height = screenWidth * 9/16
            imageViewBigNews.layoutParams = layoutParams

            textViewBigNews.text = forYouNewsInst.textHeadLine
            imageViewBigNews.setImageResource(forYouNewsInst.imageId)
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

        private var positionItem: Int = 1
        private var currentNewsTextItem: NewsText? = null

        private var textNewsForYou: TextView = itemView.findViewById(R.id.textViewForYou)
        private var imageViewForYou: ImageView = itemView.findViewById(R.id.imageViewForYou)


        fun setDatatoSmallItem(forYouNewsInst: NewsText) {

            this.positionItem = position
            this.currentNewsTextItem = forYouNewsInst
            textNewsForYou.text = forYouNewsInst.textHeadLine
            imageViewForYou.setImageResource(forYouNewsInst.imageId)

            val layoutParams = imageViewForYou.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth/3
            layoutParams.height = screenWidth * 5/16


            imageViewForYou.layoutParams = layoutParams

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
            LayoutInflater.from(context).inflate(R.layout.fyp_first_item_sample, parent, false)
        val view2 =
            LayoutInflater.from(context).inflate(R.layout.for_you_item_sample, parent, false)

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


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val bookMarkedNewsInst: NewsText = ForYouNewsList[position]

        if (viewHolder is BigNewsViewHolder) {
            viewHolder.setDataToBigItem(bookMarkedNewsInst)
            viewHolder.onClickListernersToBig()


        } else if (viewHolder is SmallNewsViewHolder) {
            viewHolder.setDatatoSmallItem(bookMarkedNewsInst)
            viewHolder.setOnClickListernerstosmall()
        }

    }

    override fun getItemCount(): Int = ForYouNewsList.size


}

