package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Network
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.DialogNetworkBinding

class NetworkFragment : Fragment() {

    companion object {
        private const val ARGS_NETWORK = "NETWORK_INFO"
        private const val ARGS_DEST_ID = "destId"
        fun newInstance(myNetwork: Network?, destId: Int): NetworkFragment {
            val fragReturn = NetworkFragment()
            val networkBundle = Bundle()
            networkBundle.putParcelable(ARGS_NETWORK, myNetwork)
            networkBundle.putInt(ARGS_DEST_ID, destId)

            fragReturn.arguments = networkBundle
            return fragReturn
        }
    }

    private lateinit var binding: DialogNetworkBinding
    private val viewModel: NetworkViewModel by viewModels()
    private var networkParced: Network? = Network()
    private var fragId: Int? = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNetworkBinding.inflate(layoutInflater)
        val view = binding.root
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val allMenu = listOf(
            R.id.newsFrag,
            R.id.myWorkspace,
            R.id.jamiiFrag,
            R.id.peopleFrag,
            R.id.myProfile
        )

        fragId = arguments?.getInt(ARGS_DEST_ID)

        if (fragId != null && allMenu.contains(fragId)) {
            fragId?.let {
                bottomNavigationView.menu.findItem(it).isChecked = true
            }
        }


        networkParced = arguments?.getParcelable<Network>(ARGS_NETWORK)

        setupViewPagerAndTabs(id)

        return view
    }


    private fun setupViewPagerAndTabs(id: Int) {
        val pagerAdapter = NetworkPagerAdapter(this)
        binding.viewPagerNetwork.apply {
            adapter = pagerAdapter
            isUserInputEnabled = false
        }

        TabLayoutMediator(binding.tabLayoutNetwork, binding.viewPagerNetwork) { tab, position ->
            tab.text = if (fragId == R.id.myProfile) {
                when (position) {
                    0 -> "Following ${networkParced?.followingList?.size}"
                    1 -> "Followers ${networkParced?.followerList?.size}"
                    2 -> "Recommended"
                    else -> ""
                }

            } else {
                when (position) {
                    0 -> "Following ${networkParced?.followingList?.size}"
                    1 -> "Followers ${networkParced?.followerList?.size}"
                    else -> ""
                }

            }

        }.attach()

        Handler().postDelayed({
            networkParced.let {
                viewModel.setNetwork(networkParced!!)
            }
        }, 700)

    }

    inner class NetworkPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = if (fragId == R.id.myProfile) 3 else 2

        override fun createFragment(position: Int): Fragment {

            return if (fragId == R.id.myProfile) {
                when (position) {
                    0 -> FollowingFragment()
                    1 -> FollowersFragment()
                    2 -> RecommendedFragment()
                    else -> Fragment()
                }
            } else {
                when (position) {
                    0 -> FollowingFragment()
                    1 -> FollowersFragment()
                    else -> Fragment()
                }
            }
        }
    }

}