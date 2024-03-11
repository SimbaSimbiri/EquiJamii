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
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube.YouTubeDialogFrag


class LiveVideoAdapter(val context: Context, val listVids: ArrayList<Video>) :
    RecyclerView.Adapter<LiveVideoAdapter.LiveVideoViewHolder>() {

    inner class LiveVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionOfItem: Int = 1
        private var currentVideoItem: Video? = null
        private val videoThumbnailImage: ImageView = itemView.findViewById(R.id.imageViewThumbnail)
        private val videoTitleTextView: TextView = itemView.findViewById(R.id.YouTubeViewHeadline)

        fun setDataToItem(video: Video, position: Int) {

            this.currentVideoItem = video
            this.positionOfItem = position

            var layoutParams = videoThumbnailImage.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth
            layoutParams.height = screenWidth * 9 / 16

            videoThumbnailImage.layoutParams = layoutParams

            Glide.with(context)
                .load(video.thumbnailUrl)
                .into(videoThumbnailImage)

            videoTitleTextView.text = video.title

        }

        fun setOnClickListeners() {
            itemView.setOnClickListener(this@LiveVideoViewHolder)
        }

        override fun onClick(view: View?) {

            val youTubeDialogFrag = YouTubeDialogFrag.newInstance(currentVideoItem!!)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            youTubeDialogFrag.show(transaction, youTubeDialogFrag.tag)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveVideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_live_videos_item, parent, false)

        return LiveVideoViewHolder(view)
    }

    override fun onBindViewHolder(videoViewHolder: LiveVideoViewHolder, position: Int) {
        videoViewHolder.setDataToItem(listVids[position], position)
        videoViewHolder.setOnClickListeners()
    }

    override fun getItemCount(): Int = listVids.size
}


