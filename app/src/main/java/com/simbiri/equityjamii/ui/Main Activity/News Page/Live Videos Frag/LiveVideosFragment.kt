package com.simbiri.equityjamii.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.live_videos_fragment, container, false)

        recyclerVideos = view.findViewById(R.id.recyclerViewYoutubeLive)


        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerVideos.layoutManager = layoutManager

        val adapter = LiveVideoAdapter(requireContext(), YouTubeVids.listVids)

        recyclerVideos.adapter = adapter

        /*val delayedTime : Long = 3000

        val rootView = requireActivity().window.decorView.rootView

        rootView.postDelayed({ YoutubeVideos()}, delayedTime)
        rootView.postDelayed({ recyclerVideos.adapter!!.notifyDataSetChanged()}, delayedTime)
*/
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}
