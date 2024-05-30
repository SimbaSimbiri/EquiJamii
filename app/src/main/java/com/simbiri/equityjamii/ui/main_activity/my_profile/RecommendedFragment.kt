package com.simbiri.equityjamii.ui.main_activity.my_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.databinding.RecommendedBinding

class RecommendedFragment : Fragment() {

    companion object {
        fun newInstance() = RecommendedFragment()
    }

    private lateinit var binding: RecommendedBinding
    private val viewModel : NetworkViewModel by viewModels(ownerProducer = {requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecommendedBinding.inflate(inflater, container, false)

        binding.recommendedPeopleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.recommendedList.observe(viewLifecycleOwner){recommendedUsers ->
            val adapter = NetworkAdapter(requireContext(), recommendedUsers)
            binding.recommendedPeopleRecyclerView.adapter = adapter
            binding.recommendedPeopleRecyclerView.adapter!!.notifyDataSetChanged()
        }

    }
}