package com.simbiri.equityjamii.ui

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.simbiri.equityjamii.R
import com.squareup.picasso.Picasso
import kotlinx.serialization.json.JsonArray
import okhttp3.Headers
import org.json.JSONArray


data class Video(
    val title: String,
    val videoUrl: String

)


object YoutubeVideos {

    private val API_KEY = "GIMMICK API KEY"//previous one was immediately revoked
    private val channelD = "GIMMICK CHANNEL ID"

    var videoList: ArrayList<Video>? = null
        get() {

            if (field != null)
                return field

            field = ArrayList()

            val client = AsyncHttpClient()

            val params = RequestParams()
            params["limit"] = "20"
            params["page"] = "1"

            client["https://www.googleapis.com/youtube/v3/search?key=${API_KEY}&channelId=${channelD}&part=snippet,id&order=date&maxResults=20", params, object :
                JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                    // Access the JSON object response to get your list of titles and videoIds which are picasso urls

                    val items = json.jsonObject.getJSONArray("items")

                    for (jsonElementPos in 0 until items.length()) {
                        val snippet = items.getJSONObject(jsonElementPos).getJSONObject("snippet")
                        val title = snippet.getString("title")
                        val thumbnails = snippet.getJSONObject("thumbnails")
                        val defaultThumbnail = thumbnails.getJSONObject("high")
                        val imageUrl = defaultThumbnail.getString("url")
                        val video = Video(title, imageUrl)

                        field!!.add(video)
                    }

                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String,
                    throwable: Throwable?
                ) {
                    Log.d("API FAILURE", response)
                }
            }]

            return field

        }


}


class LiveVideoAdapter(val context: Context, val listVids: ArrayList<Video>) :
    RecyclerView.Adapter<LiveVideoAdapter.LiveVideoViewHolder>() {

    inner class LiveVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var positionOfItem: Int = 1
        private var currentVideoItem: Video? = null
        private val videoThumbnailImage: ImageView = itemView.findViewById(R.id.imageViewNews)
        private val videoTitleTextView: TextView = itemView.findViewById(R.id.textViewHeadline)

        fun setDataToItem(video: Video, position: Int) {

            this.currentVideoItem = video
            this.positionOfItem = position

            var layoutParams = videoThumbnailImage.layoutParams

            val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParams.width = screenWidth
            layoutParams.height = screenWidth * 9/16

            videoThumbnailImage.layoutParams = layoutParams

            Picasso.get()
                .load(video.videoUrl)
                .into(videoThumbnailImage)

            videoTitleTextView.text = video.title

        }

        fun setOnClickListeners() {
            itemView.setOnClickListener(this@LiveVideoViewHolder)
        }

        override fun onClick(view: View?) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveVideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.newsitemsample, parent, false)

        return LiveVideoViewHolder(view)
    }

    override fun onBindViewHolder(videoViewHolder: LiveVideoViewHolder, position: Int) {
        videoViewHolder.setDataToItem(listVids[position], position)
        videoViewHolder.setOnClickListeners()
    }

    override fun getItemCount(): Int = listVids.size
}


