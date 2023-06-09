package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class newsFragment : Fragment() {

    companion object {
        fun newInstance() = newsFragment()
    }

    private lateinit var viewModel: NewsViewModel
    lateinit var  image_Slider: ImageSlider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_news, container, false)

        image_Slider = view?.findViewById<ImageSlider>(R.id.image_slider) as ImageSlider

        val image_list =  ArrayList<SlideModel>()

        image_list.add(SlideModel(R.drawable.europeanu_talks_image , "EU officials grace Equity Group Holdings", ScaleTypes.FIT))
        image_list.add(SlideModel(  R.drawable.jamesshakinghands, "Equity Group  registers  21% growth in total assets", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.stanfordmba , "Stanford MBA influential visit to the bank", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.elimu_talk, "Elimu Scholarship Program Day", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.equity_assurance , "Equity maintains outstanding financial perfomance", ScaleTypes.FIT))
        image_list.add(SlideModel(R.drawable.wings_to_fly_girls , "Wings to Fly Scholars fly abroad", ScaleTypes.FIT))


        image_Slider.setImageList(image_list)

        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}