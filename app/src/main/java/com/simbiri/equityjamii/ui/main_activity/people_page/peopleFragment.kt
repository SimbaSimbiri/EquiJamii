package com.simbiri.equityjamii.ui.main_activity.people_page

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.ui.main_activity.news_page.featuring.FeaturingFragment

class peopleFragment : Fragment() {

    companion object {
        fun newInstance() = peopleFragment()
    }

    private lateinit var viewModel: PeopleViewModel
    private lateinit var viewPagerPeople: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var stateAdapter: FragmentStateAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.people_page, container, false)
   /*     appBarLayout = view.findViewById(R.id.appBar)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        toolbar = collapsingToolbarLayout.findViewById(R.id.topAppBar)*/
        viewPagerPeople = view.findViewById(R.id.viewPagerPeople)
    tabLayout = view.findViewById(R.id.tabLayoutPeople)
   /*
        val drawerLayout =  requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)

        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout,toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()*/

        stateAdapter = ScreenPeopleAdapter(this@peopleFragment)
        viewPagerPeople.adapter = stateAdapter

        TabLayoutMediator(tabLayout, viewPagerPeople){ tab, position ->

            when (position){

                0 -> {tab.text = "EquiJamaa"}
                1 -> {tab.text = "EquiLeaders" }
                2 -> { tab.text = "Featuring" }
            }

        }.attach()

        return view

    }

        inner class ScreenPeopleAdapter(peopleFragment: peopleFragment) : FragmentStateAdapter(peopleFragment) {

            override fun getItemCount(): Int {
                return 3
            }

            override fun createFragment(position: Int): Fragment {
                when(position){
                    1 -> {return EquiLeadersFragment()
                    }

                    0 -> {return AllPeople()
                    }

                    2 -> {return  FeaturingFragment()}

                }
                return Fragment()
            }

        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
