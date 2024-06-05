package com.simbiri.equityjamii.ui.main_activity.news_page

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.ui.main_activity.news_page.featuring.FeaturingFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.for_you.ForYouFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.latest.LatestFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube.LiveVideosFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.OfficialFragment

class newsFragment : Fragment() {

    companion object {
        fun newInstance() = newsFragment()
    }

    private lateinit var addNewsIcon: ImageView
    private lateinit var viewModel: NewsViewModel
    private lateinit var viewPager2: ViewPager2
    private lateinit var stateAdapter: FragmentStateAdapter
    private lateinit var tabLayout: TabLayout
    private val personId = AuthUtils.getCurrentUserId()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.news_page, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        addNewsIcon = view.findViewById(R.id.addNewsIcon)

        if (AuthUtils.getCurrentUserId() != null) {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                if (currentPerson?.role.contentEquals("journalist")) {
                    addNewsIcon.visibility = View.VISIBLE
                }
            }
        }

        addNewsIcon.setOnClickListener {
            val addNewsFrag = AddNewsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            addNewsFrag.show(transaction, addNewsFrag.tag)
        }

        viewPager2 = view.findViewById(R.id.viewPagerNews)
        viewPager2.isUserInputEnabled = false
        stateAdapter = ScreenSlidePageAdapter(this@newsFragment)
        viewPager2.adapter = stateAdapter
        TabLayoutMediator(tabLayout, viewPager2, true, true) { tab, position ->

            when (position) {

                0 -> {
                    tab.text = "Latest"
                }

                1 -> {
                    tab.text = "For You"

                }

                2 -> {
                    tab.text = "Live"

                }

                3 -> {
                    tab.text = "Official"

                }

                4 -> {
                    tab.text = "Featuring"
                }


            }

        }.attach()


        return view

    }

    inner class ScreenSlidePageAdapter(newsFragment: newsFragment) :
        FragmentStateAdapter(newsFragment) {

        override fun getItemCount(): Int {
            return 5
        }

        override fun createFragment(position: Int): Fragment {

            when (position) {

                0 -> {
                    return LatestFragment()
                }

                1 -> {
                    return ForYouFragment()
                }

                2 -> {
                    return LiveVideosFragment()
                }

                3 -> {
                    return OfficialFragment()
                }

                4 -> {
                    return FeaturingFragment()
                }

            }
            return Fragment()
        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
