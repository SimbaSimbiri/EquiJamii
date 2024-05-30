package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Network
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.UserNetworkUtils
import com.simbiri.equityjamii.databinding.DialogNetworkBinding
import com.simbiri.equityjamii.databinding.MyPostsTabBinding

class NetworkFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_NETWORK = "NETWORK_INFO"
        fun newInstance(myNetwork: Network?): NetworkFragment{
            val fragReturn = NetworkFragment()
            val networkBundle = Bundle()
            networkBundle.putParcelable(ARGS_NETWORK, myNetwork)

            fragReturn.arguments = networkBundle
            return fragReturn
        }
    }

    private lateinit var binding: DialogNetworkBinding
    private val viewModel: NetworkViewModel by viewModels()
    private var networkParced : Network? = Network()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNetworkBinding.inflate(layoutInflater)
        val view = binding.root
        networkParced = arguments?.getParcelable<Network>(ARGS_NETWORK)

        networkParced.let {
            viewModel.setNetwork(networkParced!!)
        }


        setupViewPagerAndTabs()

        return view
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = NetworkPagerAdapter(this)
        binding.viewPagerNetwork.adapter = pagerAdapter
        binding.viewPagerNetwork.isUserInputEnabled = false


        TabLayoutMediator(binding.tabLayoutNetwork, binding.viewPagerNetwork) { tab, position ->
            tab.text = when (position) {
                0 -> "Following ${networkParced?.followingList?.size}"
                1 -> "Followers ${networkParced?.followerList?.size}"
                2 -> "Recommended"
                else -> ""
            }
        }.attach()
    }
    class NetworkPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                2 -> RecommendedFragment()
                0 -> FollowingFragment()
                1 -> FollowersFragment()
                else -> Fragment()
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_network)
        dialog.setCanceledOnTouchOutside(true)
        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

}