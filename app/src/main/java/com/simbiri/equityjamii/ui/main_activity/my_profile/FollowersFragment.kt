package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.databinding.FollowersBinding

class FollowersFragment : Fragment() {

    companion object {
        fun newInstance() = FollowersFragment()
    }

    private lateinit var binding: FollowersBinding
    private val viewModel: NetworkViewModel by viewModels(ownerProducer = { requireParentFragment() })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FollowersBinding.inflate(inflater, container, false)

        binding.followerPeopleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.followersList.observe(viewLifecycleOwner) { followerUsers ->

            val adapter = NetworkAdapter(requireContext(), followerUsers)
            binding.followerPeopleRecyclerView.adapter = adapter
            binding.followerPeopleRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }
}