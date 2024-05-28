package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LiveVideoAdapter
import com.simbiri.equityjamii.data.model.YouTubeVids
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class LiveVideosFragment : Fragment() {

    companion object {
        fun newInstance() = LiveVideosFragment()
    }


    private lateinit var viewModel: LiveVideosViewModel
    private lateinit var recyclerVideos: RecyclerView
    private lateinit var airingImageSlider: ImageSlider
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.news_page_live, container, false)
        airingImageSlider = view.findViewById(R.id.airingImageSwitcher)
        recyclerVideos = view.findViewById(R.id.recyclerViewYoutubeLive)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerVideos.layoutManager = layoutManager

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val liveList = YouTubeVids.YoutubeVideos(requireContext(), "live")
            val upcomingList = YouTubeVids.YoutubeVideos(requireContext(), "upcoming")
            val completedList = YouTubeVids.YoutubeVideos(requireContext(), "completed")
            val slideModels = arrayListOf<SlideModel>()


            if (liveList.isNotEmpty() && upcomingList.isNotEmpty()) {
                val currentTv = view.findViewById<TextView>(R.id.currentTextV)
                val currentCardVid = view.findViewById<CardView>(R.id.airingCurrentCardView)

                currentTv.visibility = View.VISIBLE
                currentCardVid.visibility = View.VISIBLE

                liveList.forEach {video ->
                    slideModels.add(
                        SlideModel(
                            video.thumbnailUrl, video.title, ScaleTypes.CENTER_CROP
                        )
                    )
                }
                upcomingList.forEach { video ->
                    slideModels.add(SlideModel(video.thumbnailUrl, video.title, ScaleTypes.CENTER_CROP))
                }
            }

            airingImageSlider.setImageList(slideModels)

            val adapter = LiveVideoAdapter(requireContext(), completedList)
            recyclerVideos.adapter = adapter
            recyclerVideos.adapter!!.notifyDataSetChanged()
        }
    }


}
