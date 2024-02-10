package com.simbiri.equityjamii.data.model

import android.content.Context
import android.util.Log
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.simbiri.equityjamii.R
import okhttp3.Headers

object AvailableSlots{

    private val availableTimeList = arrayOf(
        "9:00am", "9:30am", "10:00am", "10:30am", "11:00am", "11:30am", "12:00pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm", "4:30am"

    )
    var timeSlotToday : ArrayList<TimeSlot>? =null
        get() {
            if (field != null)
                return field
            field =  ArrayList()

            for (timeslot in availableTimeList){
                val timeAvailable = TimeSlot(timeslot)

                field!!.add(timeAvailable)
            }

            return field

        }
}

object SocialMedia {

    val instaGPic = R.drawable.insta

    val faceBPic = R.drawable.facebook

    val linkedIPic = R.drawable.linkedin

    val webSPic =  R.drawable.web_iconsvg

}

object OfficialNewsTexts {

    private val headlineList = arrayOf(

        "Employees to receive increase in salaries",
        "New customer interaction guidelines",
        "Different retirement  plans to sign up for",
        "Introducing new official communication platform",
        "The Pamoja entrepreneurship loan  is now available for employees"

    )

    private val officialPreviewTextList = arrayOf(

        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. ",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla.  Etiam gravida, turpis nec pellentesque congue",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu.",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu. Mauris cursus, velit ac dapibus porttitor, velit elit sodales nunc, vitae euismod justo nisi at mauris. Nunc sit amet odio nec metus consectetur hendrerit.\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet euismod purus. Suspendisse potenti. Integer nec aliquet ipsum. Proin eu nisl vitae dui rutrum interdum. Suspendisse potenti. Ut scelerisque, odio ac facilisis feugiat, purus tellus placerat mauris, eget facilisis urna mauris id purus. Phasellus scelerisque odio eu ligula dapibus, vitae feugiat tellus facilisis. Aenean a venenatis lorem. Etiam vehicula, nisi a blandit fringilla, turpis nulla auctor odio, sit amet accumsan ex mi sit amet nulla. Aenean at eros luctus, feugiat lectus eu, tincidunt odio. Mauris semper elit at odio eleifend, at convallis lacus posuere. Suspendisse eu diam dui.\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec lacinia metus nec elit vulputate, vel ultrices dolor iaculis. Sed et ex in purus dignissim consequat. Nam id arcu eget erat elementum pulvinar. Maecenas id tortor et dui posuere mollis non eu justo. Donec rutrum nunc vel quam suscipit pellentesque. Cras ullamcorper feugiat fringilla. Suspendisse maximus justo ligula, id bibendum mauris ultrices vel. Sed malesuada auctor dui, id aliquet tellus pellentesque quis",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu."
    )

    var officialNewsList : ArrayList<OfficialNews>? = null
        get() {

            if (field!=null)
                return field
            field = ArrayList()

            for (headlinePos in headlineList.indices){
                val headline = headlineList[headlinePos]
                val previewText = officialPreviewTextList[headlinePos]
                val officialNews = OfficialNews(headline,previewText)

                field!!.add(officialNews)
            }
            return field
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

