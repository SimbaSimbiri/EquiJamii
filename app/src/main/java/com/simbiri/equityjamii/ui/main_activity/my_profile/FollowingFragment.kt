package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.FollowingBinding


class FollowingFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object {
        fun newInstance() = FollowingFragment()

    }

    private lateinit var binding: FollowingBinding
    private val viewModel: NetworkViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var fullList = mutableListOf<Person>()
    private var searchList = mutableListOf<Person>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FollowingBinding.inflate(inflater, container, false)

        setUpRecyclers()
        setupObservers()
        setUpSearchView()

        return binding.root
    }

    private fun setUpSearchView() {
        binding.searchViewAll.setOnQueryTextListener(this@FollowingFragment)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText ?: "")
        return true
    }
    private fun setUpRecyclers() {

        binding.followingPeopleRecyclerView.apply {
            adapter = NetworkAdapter(requireContext(), searchList)
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun filter(text: String) {
        val filteredList = if (text.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name.contains(text, ignoreCase = true) }
        }

        searchList.clear()
        searchList.addAll(filteredList.sortedBy { it.name }.toMutableList())
        binding.followingPeopleRecyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun setupObservers() {
        viewModel.followingList.observe(viewLifecycleOwner) { followingUsers ->
            fullList.clear()
            fullList.addAll(followingUsers)
            filter("")
        }
    }
}