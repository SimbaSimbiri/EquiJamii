package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.databinding.FollowingBinding


class FollowingFragment : Fragment() {

    companion object {
        fun newInstance() = FollowingFragment()

    }

    private lateinit var binding: FollowingBinding
    private val viewModel: NetworkViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FollowingBinding.inflate(inflater, container, false)

        binding.followingPeopleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.followingPeopleRecyclerView.hasFixedSize()

        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.followingList.observe(viewLifecycleOwner) { followingUsers ->

            val adapter = NetworkAdapter(requireContext(), followingUsers)
            binding.followingPeopleRecyclerView.adapter = adapter
            binding.followingPeopleRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }
}