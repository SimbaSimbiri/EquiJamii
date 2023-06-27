package com.simbiri.equityjamii

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WatchLaterAdapter(var context: Context, var watchTextList: ArrayList<NewsText>): RecyclerView.Adapter<WatchLaterAdapter.WatchLaterViewHolder>(){

    inner class WatchLaterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var positionItem:Int = 1
        private var currentLaterItem : NewsText ? = null

        private  var textNewsForYou : TextView =  itemView.findViewById(R.id.textViewForYou)
        private  var imageViewForYou : ImageView = itemView.findViewById(R.id.imageViewForYou)
        private var removeSaved: ImageView = itemView.findViewById(R.id.removeSaved)


        fun setDatatoItem(bookMarkedNewsInst: NewsText, position: Int) {

            this.positionItem = position
            this.currentLaterItem = bookMarkedNewsInst

            textNewsForYou.text = bookMarkedNewsInst.textHeadLine
            imageViewForYou.setImageResource(bookMarkedNewsInst.imageId)
        }

        fun setOnClicks() {
            removeSaved.setOnClickListener(this@WatchLaterViewHolder)
        }

        override fun onClick(v: View?) {

            watchTextList.remove(currentLaterItem)
            newsAddedList.remove(currentLaterItem?.imageId)
            notifyItemRemoved(positionItem)
            notifyItemRangeChanged(positionItem, watchTextList.size)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchLaterViewHolder {
        val view =  LayoutInflater.from(context).inflate(R.layout.for_you_item_sample, parent,false)

        return  WatchLaterViewHolder(view)
    }

    override fun onBindViewHolder(watchLaterViewHolder: WatchLaterViewHolder, position: Int) {
        val bookMarkedNewsInst: NewsText = watchTextList[position]

        watchLaterViewHolder.setDatatoItem(bookMarkedNewsInst, position)
        watchLaterViewHolder.setOnClicks()

    }

    override fun getItemCount(): Int = watchTextList.size


}
