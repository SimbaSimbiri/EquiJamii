package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LiveVideoAdapter
import com.simbiri.equityjamii.data.model.YouTubeVids

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
        val view = inflater.inflate(R.layout.news_page_live, container, false)

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
    /*
    "https://youtube.googleapis.com/youtube/v3/search?channelId=${channelD}&eventType=live&key=${API_KEY}"

    //working
    "https://www.googleapis.com/youtube/v3/search?key=${API_KEY}&channelId=${channelD}&part=snippet,id&order=date&maxResults=20"
*/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}
