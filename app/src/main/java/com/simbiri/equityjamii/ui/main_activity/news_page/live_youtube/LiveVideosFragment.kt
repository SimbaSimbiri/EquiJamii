package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LiveVideoAdapter
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.data.model.YouTubeVids
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.notify

@OptIn(DelicateCoroutinesApi::class)
class LiveVideosFragment : Fragment() {

    companion object {
        fun newInstance() = LiveVideosFragment()
    }


    private lateinit var viewModel: LiveVideosViewModel
    private lateinit var recyclerVideos: RecyclerView
    private lateinit var airingImageSlider: ImageSlider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.news_page_live, container, false)
        airingImageSlider = view.findViewById(R.id.airingImageSwitcher)
        recyclerVideos = view.findViewById(R.id.recyclerViewYoutubeLive)

        val slideModels = arrayListOf<SlideModel>()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerVideos.layoutManager = layoutManager

        lifecycleScope.launch(Dispatchers.Main) {
            val liveDeferred = async(Dispatchers.IO) { YouTubeVids.YoutubeVideos(requireContext(), "live") }
            val upcomingDeferred = async(Dispatchers.IO) { YouTubeVids.YoutubeVideos(requireContext(), "upcoming") }
            val completedDeferred = async(Dispatchers.IO) { YouTubeVids.YoutubeVideos(requireContext(), "completed") }

            try {
                liveDeferred.await().forEach { video ->
                    slideModels.add(SlideModel(video.thumbnailUrl, video.title, ScaleTypes.CENTER_CROP))
                }
            } catch (e: Exception) {
                Log.d("COROUTINE ERROR", "Error fetching live videos: ${e.message}")
            }

            try {
                upcomingDeferred.await().forEach { video ->
                    slideModels.add(SlideModel(video.thumbnailUrl, video.title, ScaleTypes.CENTER_CROP))
                }
            } catch (e: Exception) {
                Log.d("COROUTINE ERROR", "Error fetching upcoming videos: ${e.message}")
            }

            airingImageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP)

            try {
                val completedList = completedDeferred.await()
                val adapter = LiveVideoAdapter(requireContext(), completedList)
                recyclerVideos.adapter = adapter
                recyclerVideos.adapter!!.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.d("COROUTINE ERROR", "Error fetching completed videos: ${e.message}")
            }
        }


        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
