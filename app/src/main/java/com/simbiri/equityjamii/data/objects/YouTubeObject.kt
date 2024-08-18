package com.simbiri.equityjamii.data.objects

import android.content.Context
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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

    private val ioDispatcher = Dispatchers.IO
    suspend fun getVideoDetails(context: Context, videoId: String): Video? {
        val apiKey = YoutubeKeyProvider.keyProvider(context, 0)

        return withContext(ioDispatcher) {
            try {
                val response = RetrofitClient.instance.getYoutubeVideoDetails(
                    videoId = videoId,
                    apiKey = apiKey,
                    part = "snippet"
                )

                val videoItem = response.items.firstOrNull()
                if (videoItem != null) {
                    val snippet = videoItem.snippet
                    val title = snippet.title
                    val imageUrl = snippet.thumbnails.high.url
                    Video(title, imageUrl, videoId)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun YoutubeVideos(context: Context, eventType: String): List<Video> {
        val apiKey = YoutubeKeyProvider.keyProvider(context, 0)
        val channelId = YoutubeKeyProvider.keyProvider(context, 1)

        val params = mapOf(
            "limit" to "20", "eventType" to eventType, "type" to "video", "page" to "1"
        )

        return withContext(ioDispatcher) {
            try {
                val response = RetrofitClient.instance.getYoutubeVideos(
                    apiKey = apiKey,
                    channelId = channelId,
                    part = "snippet,id",
                    order = "date",
                    maxResults = 20,
                    params = params
                )

                response.items.mapNotNull {
                    val videoId = it.id.videoId ?: return@mapNotNull null
                    val snippet = it.snippet
                    val title = snippet.title
                    val imageUrl = snippet.thumbnails.high.url
                    Video(title, imageUrl, videoId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

        }
    }


}

