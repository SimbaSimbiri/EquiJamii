package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.app.Dialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Network
import com.simbiri.equityjamii.data.model.Person
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_network)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = 900

            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNetworkBinding.inflate(layoutInflater)
        val view = binding.root
        networkParced = arguments?.getParcelable<Network>(ARGS_NETWORK)

        val usersFollowing  = Person().following(networkParced?.followingList)
        val usersFollowed = Person().followers(networkParced?.followerList)
        val usersFollowingFollower = Person().followingFollowers(networkParced?.followingList)

        return view
    }
}