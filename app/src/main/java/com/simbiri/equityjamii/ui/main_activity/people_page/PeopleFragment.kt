package com.simbiri.equityjamii.ui.main_activity.people_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.databinding.PeoplePageBinding

class PeopleFragment : Fragment() {

    companion object {
        fun newInstance() = PeopleFragment()
    }

    private lateinit var stateAdapter: FragmentStateAdapter
    private lateinit var binding: PeoplePageBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeoplePageBinding.inflate(layoutInflater)

        val view = binding.root

        binding.apply {

            stateAdapter = ScreenPeopleAdapter(this@PeopleFragment)
            viewPagerPeople.adapter = stateAdapter
            viewPagerPeople.isUserInputEnabled = false

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                if (currentPerson == null) {
                    Toast.makeText(
                        requireContext(),
                        "Set up profile to access EquiJamii features",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    swipeRefresh.setOnRefreshListener {
                        refreshJamii()
                    }

                    searchViewAll.setOnClickListener {
                       val navHostFrag =
                            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                        navHostFrag.navController.navigate(PeopleFragmentDirections.actionGlobalOpenSearch())
                    }
                }
            }

            TabLayoutMediator(tabLayoutPeople, viewPagerPeople, true, false) { tab, position ->

                when (position) {

                    0 -> {
                        tab.text = "EquiJamaa"
                    }

                    1 -> {
                        tab.text = "EquiLeaders"
                    }
                }

            }.attach()

        }

        return view

    }

    private fun refreshJamii() {

        val currentTabPosition = binding.tabLayoutPeople.selectedTabPosition

        val fragmentTransactionExit = parentFragmentManager.beginTransaction()
        val fragmentTransactionEnter = parentFragmentManager.beginTransaction()

        fragmentTransactionExit.detach(this).commitNow()
        fragmentTransactionEnter.attach(this).commitNow()

        binding.viewPagerPeople.currentItem = currentTabPosition
        binding.swipeRefresh.isRefreshing = false


    }

    inner class ScreenPeopleAdapter(peopleFragment: PeopleFragment) :
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


}
