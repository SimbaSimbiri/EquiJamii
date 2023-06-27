package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class newsFragment : Fragment(){

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
    private lateinit var viewPager2: ViewPager2
    private lateinit var stateAdapter: FragmentStateAdapter
    private lateinit var tabLayout: TabLayout


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
        viewPager2 = view.findViewById(R.id.viewPagerNews)
        tabLayout  = view.findViewById(R.id.tabLayout)


        viewPager2 = view.findViewById(R.id.viewPagerNews)
        stateAdapter = ScreenSlidePageAdapter(this@newsFragment)
        viewPager2.adapter = stateAdapter
        TabLayoutMediator (tabLayout, viewPager2){ tab, position ->

            when (position){
                0 -> {tab.text = "Top Stories"

                }

                1 -> {tab.text = "For You"

                }

            }

        }.attach()

        val drawerLayout =  requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)

        profileToSettings.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return view

    }

    inner class ScreenSlidePageAdapter(newsFragment: newsFragment) : FragmentStateAdapter (newsFragment){

        override fun getItemCount(): Int {
            return  2   }

        override fun createFragment(position: Int): Fragment {

            when(position){
                0 -> {
                    return TopStoriesFragment()
                }

                1 -> {

                    return ForYouFragment()

                }
            }
            return  Fragment()
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
