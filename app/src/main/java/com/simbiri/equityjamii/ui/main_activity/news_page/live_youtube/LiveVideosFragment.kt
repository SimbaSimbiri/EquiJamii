package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LiveVideoAdapter
import com.simbiri.equityjamii.data.model.YouTubeVids

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

        val slideModels = arrayListOf(
            SlideModel("https://firebasestorage.googleapis.com/v0/b/equityjamii-live-page.appspot.com/o/Profile_pics%2F5Xd5VOfp27Z1GtVSyxrSKFHtBY72.jpg?alt=media&token=5017e78a-eeac-4c7c-863c-3150741c8cb1", "A close talk with Ray EquiJamii Engineer", ScaleTypes.CENTER_CROP),

        )
        airingImageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerVideos.layoutManager = layoutManager

        val adapter = LiveVideoAdapter(requireContext(), YouTubeVids.listVids)

        recyclerVideos.adapter = adapter


        return view
    }

    private fun adjustsizes() {
        val layoutImageSliderParams = airingImageSlider.layoutParams
        val displayMetrics =  DisplayMetrics()

        layoutImageSliderParams.height =displayMetrics.heightPixels/2
        layoutImageSliderParams.width = layoutImageSliderParams.width

        airingImageSlider.layoutParams = layoutImageSliderParams

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}
