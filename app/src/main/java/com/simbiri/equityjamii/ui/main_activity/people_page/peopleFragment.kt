package com.simbiri.equityjamii.ui.main_activity.people_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils

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

        val view = inflater.inflate(R.layout.people_page, container, false)

        viewPagerPeople = view.findViewById(R.id.viewPagerPeople)
        tabLayout = view.findViewById(R.id.tabLayoutPeople)

        stateAdapter = ScreenPeopleAdapter(this@peopleFragment)
        viewPagerPeople.adapter = stateAdapter
        viewPagerPeople.isUserInputEnabled = false


        AuthUtils.getCurrentPerson { currentPerson ->
            if (currentPerson == null) {
                Toast.makeText(
                    requireContext(),
                    "Set up profile to access EquiJamii features",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        TabLayoutMediator(tabLayout, viewPagerPeople) { tab, position ->

            when (position) {

                0 -> {
                    tab.text = "EquiJamaa"
                }

                1 -> {
                    tab.text = "EquiLeaders"
                }
            }

        }.attach()

        return view

    }

    inner class ScreenPeopleAdapter(peopleFragment: peopleFragment) :
        FragmentStateAdapter(peopleFragment) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                1 -> {
                    return EquiLeadersFragment()
                }

                0 -> {
                    return AllPeople()
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
