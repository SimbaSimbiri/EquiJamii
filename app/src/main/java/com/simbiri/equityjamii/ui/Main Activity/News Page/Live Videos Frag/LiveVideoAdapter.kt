package com.simbiri.equityjamii.ui

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.simbiri.equityjamii.R
import com.squareup.picasso.Picasso
import okhttp3.Headers

val VIDEO_CONSTANT = "video"

data class Video(
    val title: String,
    val videoUrl: String,
    val videoId : String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: ""
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(videoId)
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}


object YoutubeKeyProvider {

    fun keyProvider(context: Context, intKey: Int): String {
        return (if (intKey == 0) {
            context.resources.getString(R.string.apikey)
        } else {
            context.resources.getString(R.string.channelID)
        }).toString()

    }
}

object YouTubeVids {

    var listVids =  ArrayList<Video>()


    fun YoutubeVideos(context: Context): ArrayList<Video> {

        val API_KEY = YoutubeKeyProvider.keyProvider(context,0)
        val channelD = YoutubeKeyProvider.keyProvider(context,1)

        var videoList: ArrayList<Video>  =  ArrayList()

        val client = AsyncHttpClient()

        val params = RequestParams()
        params["limit"] = "20"
        params["eventType"] = "completed"
        params["type"] = "video"
        params["page"] = "1"


        client["https://www.googleapis.com/youtube/v3/search?key=${API_KEY}&channelId=${channelD}&part=snippet,id&order=date&maxResults=20", params, object :
            JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                // Access the JSON object response to get your list of titles and videoIds which are picasso urls

                val items = json.jsonObject.getJSONArray("items")

                for (jsonElementPos in 0 until items.length()) {
                    val snippet = items.getJSONObject(jsonElementPos).getJSONObject("snippet")
                    val title = snippet.getString("title")
                    val videoId = items.getJSONObject(jsonElementPos).optJSONObject("id")?.optString("videoId").toString()
                    val thumbnails = snippet.getJSONObject("thumbnails")
                    val defaultThumbnail = thumbnails.getJSONObject("high")
                    val imageUrl = defaultThumbnail.getString("url")
                    val video = Video(title, imageUrl, videoId)

                    Log.d("videoId", videoId)

                    videoList.add(video)
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

        listVids = videoList

        return videoList

    }

}

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
                .load(video.videoUrl)
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

          /*  val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra(VIDEO_CONSTANT, currentVideoItem?.videoId)
            context.startActivity(intent)
            Log.d("Our video id", currentVideoItem!!.videoId)*/

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveVideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item_sample, parent, false)

        return LiveVideoViewHolder(view)
    }

    override fun onBindViewHolder(videoViewHolder: LiveVideoViewHolder, position: Int) {
        videoViewHolder.setDataToItem(listVids[position], position)
        videoViewHolder.setOnClickListeners()
    }

    override fun getItemCount(): Int = listVids.size
}


