package com.simbiri.equityjamii.ui.main_activity.news_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.NewsPageBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.featuring.FeaturingFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.for_you.ForYouFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.latest.LatestFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube.LiveVideosFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.OfficialFragment

class newsFragment : Fragment() {

    companion object {
        fun newInstance() = newsFragment()
    }

    private lateinit var viewModel: NewsViewModel
    private lateinit var stateAdapter: FragmentStateAdapter
    private var canPublishEdit = false
    private lateinit var binding: NewsPageBinding

    private val personId = AuthUtils.getCurrentUserId()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewsPageBinding.inflate(layoutInflater)

        val view = binding.root


        binding.apply {


            if (AuthUtils.getCurrentUserId() != null) {

                AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                    canPublishEdit = currentPerson?.role?.contentEquals("journalist") == true
                    if (canPublishEdit) {
                        addNewsIcon.visibility = View.VISIBLE
                    }
                }
            }

            addNewsIcon.setOnClickListener {
                val addNewsFrag = AddNewsFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                addNewsFrag.show(transaction, addNewsFrag.tag)
            }

            stateAdapter = ScreenSlidePageAdapter(this@newsFragment)
            viewPagerNews.apply {
                isUserInputEnabled = false
                adapter = stateAdapter
            }

            TabLayoutMediator(tabLayout, viewPagerNews, true, true) { tab, position ->

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

        }
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
