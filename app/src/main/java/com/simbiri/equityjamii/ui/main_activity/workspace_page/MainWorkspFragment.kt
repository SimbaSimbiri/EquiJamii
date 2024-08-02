package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.MainWorkspBinding

class MainWorkspFragment : Fragment() {

    companion object {
        fun newInstance() = MainWorkspFragment()
    }

    private lateinit var binding: MainWorkspBinding
    private val viewModel: MainWorkspViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainWorkspBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkAuthentication()

    }

    private fun checkAuthentication() {
        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
            if (currentPerson == null) {
                Toast.makeText(
                    requireContext(),
                    "Set up profile to access EquiJamii features",
                    Toast.LENGTH_LONG
                ).show()
            } else{
                
                setupViewPager()

                val initialTabIndex = arguments?.getInt("initial_tab_index") ?: 0

                binding.apply {
                    viewPagerWorksp.setCurrentItem(initialTabIndex, false)

                    swipeRefresh.setOnRefreshListener {
                        refreshWorkspace()
                    }
                }

            }
        }
    }

    private fun setupViewPager() {
        val adapterPager = WorkspacePagerAdapter(this)
        binding.viewPagerWorksp.apply{
            adapter = adapterPager
            isUserInputEnabled = false
        }


        TabLayoutMediator(binding.tabLayoutWorksp, binding.viewPagerWorksp,true,
            false) { tab, position ->
            when (position) {
                0 -> tab.text = "My workspaces"
                1 -> tab.text = "My EquiAI"
            }
        }.attach()
    }

    private fun refreshWorkspace() {
        val currentTabPosition = binding.tabLayoutWorksp.selectedTabPosition
        val currentFragment = childFragmentManager.fragments[currentTabPosition] as Fragment

        childFragmentManager.beginTransaction().detach(currentFragment).commitNow()
        childFragmentManager.beginTransaction().attach(currentFragment).commitNow()

        binding.swipeRefresh.isRefreshing = false
    }

    inner class WorkspacePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MyWorkspace()
                1 -> Fragment()
                else -> Fragment()
            }
        }
    }
}
