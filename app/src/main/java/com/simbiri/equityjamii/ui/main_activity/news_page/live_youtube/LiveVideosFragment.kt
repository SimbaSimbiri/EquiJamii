package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.simbiri.equityjamii.adapters.LiveVideoAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.databinding.NewsPageLiveBinding
import kotlinx.coroutines.launch

class LiveVideosFragment : Fragment() {

    companion object {
        fun newInstance() = LiveVideosFragment()
    }

    private val viewModel = LiveVideosViewModel()
    private val slideModels = arrayListOf<SlideModel>()
    private lateinit var binding: NewsPageLiveBinding
    private val liveListAll = mutableListOf<Video>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch {
            viewModel.fetchVideos(context)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = NewsPageLiveBinding.inflate(layoutInflater)

        if (AuthUtils.getCurrentUserId() != null) {
            setUpObservers(requireContext())
        }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.recyclerViewYoutubeLive.layoutManager = layoutManager

        val layoutManagerLive = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.currentlyAiringLiveRecyclerView.layoutManager = layoutManagerLive

        binding.currentlyAiringLiveRecyclerView.adapter =
            LiveVideoAdapter(requireContext(), liveListAll)

        return binding.root
    }

    private fun setUpObservers(context: Context) {

        lifecycleScope.launch {
            viewModel.liveList.observe(viewLifecycleOwner) { liveList ->

                if (liveList.isNotEmpty()) {
                    liveListAll.addAll(liveList)
                    binding.currentTextV.visibility = View.VISIBLE
                    binding.currentlyAiringLiveRecyclerView.adapter!!.notifyDataSetChanged()
                    binding.progressBar.visibility = View.INVISIBLE

                }
            }

            viewModel.completedList.observe(viewLifecycleOwner) { completedList ->
                if (completedList.isNotEmpty()) {
                    val adapterVideo = LiveVideoAdapter(context, completedList)
                    binding.recyclerViewYoutubeLive.apply {
                        adapter = adapterVideo
                    }
                    binding.recyclerViewYoutubeLive.adapter!!.notifyDataSetChanged()
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }
}
