package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar

class newsFragment : Fragment() {

    companion object {
        fun newInstance() = newsFragment()
    }

    private lateinit var viewModel: NewsViewModel
/*
    lateinit var image_Slider: ImageSlider
*/
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var profileToSettings: ImageView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerNews: RecyclerView


    lateinit var textField3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_news, container, false)

        appBarLayout = view.findViewById(R.id.appBarNews)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        profileToSettings = collapsingToolbarLayout.findViewById(R.id.profileAndSettings)
        toolbar = view.findViewById(R.id.topAppBarNews)
        recyclerNews = view.findViewById(R.id.newsRecylerView)

        setUpRecyclerNews(view)

        val drawerLayout =  requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)

        profileToSettings.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }





        return view

/*
        image_Slider = view?.findViewById<ImageSlider>(R.id.image_slider) as ImageSlider

        val image_list = ArrayList<SlideModel>()

        image_list.add(
            SlideModel(
                R.drawable.europeanu_talks_image,
                "EU officials grace Equity Group Holdings",
                ScaleTypes.FIT
            )
        )
        image_list.add(
            SlideModel(
                R.drawable.jamesshakinghands,
                "Equity Group  registers  21% growth in total assets",
                ScaleTypes.FIT
            )
        )
        image_list.add(
            SlideModel(
                R.drawable.stanfordmba,
                "Stanford MBA influential visit to the bank",
                ScaleTypes.FIT
            )
        )
        image_list.add(
            SlideModel(
                R.drawable.elimu_talk,
                "Elimu Scholarship Program Day",
                ScaleTypes.FIT
            )
        )
        image_list.add(
            SlideModel(
                R.drawable.equity_assurance,
                "Equity maintains outstanding financial perfomance",
                ScaleTypes.FIT
            )
        )
        image_list.add(
            SlideModel(
                R.drawable.wings_to_fly_girls,
                "Wings to Fly Scholars fly abroad",
                ScaleTypes.FIT
            )
        )


        image_Slider.setImageList(image_list)*/

    }

    private fun setUpRecyclerNews(view: View?) {
        val context = requireContext()
        val newsAdapter =  NewsAdapter(context, NewsToday.newsTextList!!)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL

        recyclerNews.adapter = newsAdapter
        recyclerNews.layoutManager = layoutManager
        recyclerNews.hasFixedSize()

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
