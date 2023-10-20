package com.simbiri.equityjamii.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.simbiri.equityjamii.R
import okhttp3.Headers
import java.util.Properties

class LiveVideosFragment : Fragment() {

    companion object {
        fun newInstance() = LiveVideosFragment()
    }

    private lateinit var viewModel: LiveVideosViewModel
    private lateinit var recyclerVideos: RecyclerView
    private lateinit var buttonData: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.live_videos_fragment, container, false)

        recyclerVideos = view.findViewById(R.id.recyclerViewYoutubeLive)


        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerVideos.layoutManager = layoutManager

        val adapter = LiveVideoAdapter(requireContext(), YoutubeVideos())

        recyclerVideos.adapter = adapter

        val delayedTime : Long = 500

        val rootView = requireActivity().window.decorView.rootView
        rootView.postDelayed({recyclerVideos.adapter!!.notifyDataSetChanged()}, delayedTime)

        return view
    }


    fun YoutubeVideos(): ArrayList<Video> {

        val API_KEY = YoutubeKeyProvider.keyProvider(requireContext(),0)
        val channelD = YoutubeKeyProvider.keyProvider(requireContext(),1)

        var videoList: ArrayList<Video>  =  ArrayList()

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

        return videoList

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
