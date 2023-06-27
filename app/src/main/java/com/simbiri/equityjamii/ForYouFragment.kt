package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

    private lateinit var viewModel: ForYouViewModel
    private lateinit var recyclerForYou: RecyclerView
    private lateinit var image_Slider: ImageSlider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.for_you_fragment, container, false)

         recyclerForYou = view.findViewById(R.id.forYouRecylerView)


        image_Slider = view?.findViewById<ImageSlider>(R.id.image_slider) as ImageSlider

        val imageList =  ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.hackathonequity , "Meet the winners of Equity Hackathon 2nd Edition", ScaleTypes.FIT))
        imageList.add(SlideModel( R.drawable.bankacquisition, "Equity Group  acquires 91% of COGEBANQUE  equity stake", ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.cewicequity , "Commonwealth Enterprise partners with Equity", ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.americanecommerce, "Why is Africa The Future of the world?", ScaleTypes.FIT))


        image_Slider.setImageList(imageList)

        Toast.makeText(context, "Saved items appear here", Toast.LENGTH_SHORT).show()

        return view
    }

    override fun onResume() {

         setUpRecyclerForYou(view)

        super.onResume()
    }
    private fun setUpRecyclerForYou(view: View?) {

        val context = requireContext()
        val newsLaterAdapter =  WatchLaterAdapter(context, NewsToday.isBookMarkedNews)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL

        recyclerForYou.adapter = newsLaterAdapter
        recyclerForYou.layoutManager = layoutManager
        recyclerForYou.setHasFixedSize(true)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForYouViewModel::class.java)
        // TODO: Use the ViewModel
    }

}