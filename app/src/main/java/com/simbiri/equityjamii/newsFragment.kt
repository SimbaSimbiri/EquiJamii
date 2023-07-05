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
import androidx.appcompat.app.ActionBarDrawerToggle
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

    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPager2: ViewPager2
    private lateinit var stateAdapter: FragmentStateAdapter
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_news, container, false)

        appBarLayout = view.findViewById(R.id.appBarNews)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)

        toolbar = collapsingToolbarLayout.findViewById(R.id.topAppBarNews)
        tabLayout  = view.findViewById(R.id.tabLayout)

        val drawerLayout =  requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)


        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        viewPager2 = view.findViewById(R.id.viewPagerNews)
        stateAdapter = ScreenSlidePageAdapter(this@newsFragment)
        viewPager2.adapter = stateAdapter
        TabLayoutMediator (tabLayout, viewPager2){ tab, position ->

            when (position){
                0 -> {tab.text = "Latest"

                }

                1 -> {tab.text = "For You"

                }

            }

        }.attach()

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
