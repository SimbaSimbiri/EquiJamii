package com.simbiri.equityjamii.ui

import android.os.Bundle
import android.security.identity.AccessControlProfileId
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.simbiri.equityjamii.R
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Headers

class LiveVideosFragment : Fragment() {

    companion object {
        fun newInstance() = LiveVideosFragment()
    }

    private lateinit var viewModel: LiveVideosViewModel
    private lateinit var recyclerVideos: RecyclerView
    private lateinit var buttonData : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.live_videos_fragment, container, false)

        recyclerVideos = view.findViewById(R.id.recyclerViewYoutubeLive)
        buttonData = view.findViewById(R.id.buttonData)

        buttonData.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                YoutubeVideos.videoList

                val adapter = LiveVideoAdapter(requireContext(), YoutubeVideos.videoList!!)

                recyclerVideos.adapter = adapter

                recyclerVideos.adapter!!.notifyDataSetChanged()
            }

        })

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerVideos.layoutManager =  layoutManager



        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}



