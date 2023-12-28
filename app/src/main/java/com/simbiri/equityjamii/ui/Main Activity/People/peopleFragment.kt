package com.simbiri.equityjamii.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R

class peopleFragment : Fragment() {

    companion object {
        fun newInstance() = peopleFragment()
    }

    private lateinit var viewModel: PeopleViewModel
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewPagerPeople: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var stateAdapter: FragmentStateAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_people, container, false)
        appBarLayout = view.findViewById(R.id.appBar)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        toolbar = collapsingToolbarLayout.findViewById(R.id.topAppBar)
        viewPagerPeople = view.findViewById(R.id.viewPagerPeople)
        tabLayout = view.findViewById(R.id.tabLayoutPeople)

        val drawerLayout =  requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)

        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout,toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        stateAdapter = ScreenPeopleAdapter(this@peopleFragment)
        viewPagerPeople.adapter = stateAdapter
        TabLayoutMediator(tabLayout, viewPagerPeople){ tab, position ->

            when (position){

                0 -> {tab.text = "EquiLeaders"}
                1 -> {tab.text = "EquiJamaa" }
            }

        }.attach()

        return view

    }

        inner class ScreenPeopleAdapter(peopleFragment: peopleFragment) : FragmentStateAdapter(peopleFragment) {

            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                when(position){
                    0 -> {return EquiLeadersFragment()
                    }

                    1 -> {return AllPeople()
                    }

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
