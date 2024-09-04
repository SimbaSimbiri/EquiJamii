package com.simbiri.equityjamii.ui.main_activity.my_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.RecommendedBinding

class RecommendedFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object {
        fun newInstance() = RecommendedFragment()
    }

    private lateinit var binding: RecommendedBinding
    private val viewModel : NetworkViewModel by viewModels(ownerProducer = {requireParentFragment()})
    private var fullList = mutableListOf<Person>()
    private var searchList = mutableListOf<Person>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RecommendedBinding.inflate(inflater, container, false)

        setUpRecyclers()
        setUpObservers()
        setUpSearchView()

        return binding.root
    }
    private fun setUpSearchView() {
        binding.searchViewAll.setOnQueryTextListener(this@RecommendedFragment)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText ?: "")
        return true
    }
    private fun setUpRecyclers() {

        binding.recommendedPeopleRecyclerView.apply {
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
        binding.recommendedPeopleRecyclerView.adapter!!.notifyDataSetChanged()
    }
    private fun setUpObservers() {
        viewModel.recommendedList.observe(viewLifecycleOwner){recommendedUsers ->
            fullList.clear()
            fullList.addAll(recommendedUsers)
            filter("")
        }

    }
}