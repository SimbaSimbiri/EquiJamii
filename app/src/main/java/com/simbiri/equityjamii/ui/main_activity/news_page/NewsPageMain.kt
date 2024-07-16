package com.simbiri.equityjamii.ui.main_activity.news_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.NewsPageBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.featuring.FeaturingFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.for_you.ForYouFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.latest.LatestFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube.LiveVideosFragment
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.AddOfficialDialog
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.OfficialFragment

class newsFragment : Fragment() {

    companion object {
        fun newInstance() = newsFragment()
    }

    private lateinit var stateAdapter: FragmentStateAdapter
    private var canPublishEdit = false
    private lateinit var binding: NewsPageBinding
    var fabVisible = false


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
                        fabAdd.visibility = View.VISIBLE
                    }
                }
            }

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                if (currentPerson == null) {
                    Toast.makeText(
                        requireContext(),
                        "Set up profile to access EquiJamii features",
                        Toast.LENGTH_LONG
                    ).show()
                }else{

                    fabAdd.setOnClickListener {

                        if (!fabVisible) {

                            fabAddNews.show()
                            fabAddOfficial.show()

                            fabAddNews.visibility = View.VISIBLE
                            fabAddOfficial.visibility = View.VISIBLE

                            fabAdd.setImageDrawable(resources.getDrawable(R.drawable.cancel_fab_icon))
                            fabVisible = true
                        } else {

                            fabAddNews.hide()
                            fabAddOfficial.hide()

                            fabAddNews.visibility = View.GONE
                            fabAddOfficial.visibility = View.GONE

                            fabAdd.setImageDrawable(resources.getDrawable(R.drawable.add_icon))
                            fabVisible = false
                        }
                    }

                    fabAddNews.setOnClickListener {
                        val addNewsFrag = AddNewsFragment()
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        addNewsFrag.show(transaction, addNewsFrag.tag)
                    }

                    fabAddOfficial.setOnClickListener {
                        val addOfficialDialog = AddOfficialDialog()
                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        addOfficialDialog.show(transaction, addOfficialDialog.tag)
                    }

                    stateAdapter = ScreenSlidePageAdapter(this@newsFragment)
                    viewPagerNews.apply {
                        isUserInputEnabled = false
                        adapter = stateAdapter
                    }

                    TabLayoutMediator(tabLayout, viewPagerNews, true, false) { tab, position ->

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

                    swipeRefresh.setOnRefreshListener {
                        refreshNews()
                    }
                }
            }


        }
        return view

    }

    private fun refreshNews() {

        val currentTabPosition = binding.tabLayout.selectedTabPosition

        val fragmentTransactionExit = parentFragmentManager.beginTransaction()
        val fragmentTransactionEnter = parentFragmentManager.beginTransaction()

        fragmentTransactionExit.detach(this).commitNow()
        fragmentTransactionEnter.attach(this).commitNow()

        binding.viewPagerNews.currentItem = currentTabPosition
        binding.swipeRefresh.isRefreshing = false

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


}
