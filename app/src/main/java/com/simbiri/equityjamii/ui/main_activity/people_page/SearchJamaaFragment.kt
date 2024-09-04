package com.simbiri.equityjamii.ui.main_activity.people_page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.DialogSearchJamaaBinding

class SearchJamaaFragment : Fragment(), SearchView.OnQueryTextListener {

    companion object {
        fun newInstance() = SearchJamaaFragment()
    }

    private val viewModel = PeopleViewModel()
    private lateinit var binding: DialogSearchJamaaBinding
    private var searchList = mutableListOf<Person>()
    private var fullList = mutableListOf<Person>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchJamaaBinding.inflate(layoutInflater)
        setUpRecyclers()
        setUpObservers()
        setUpSearchView()

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(
            R.id.bottom_nav_view)
        bottomNavigationView?.let { navView ->
            val menuItem = navView.menu.findItem(R.id.peopleFrag)
            menuItem?.let { it.isChecked = true }
        }

        return binding.root
    }
    private fun setUpObservers() {
        viewModel.jamaaList.observe(viewLifecycleOwner) { jamaaList ->

            fullList.addAll(jamaaList)
            filter("")
        }

        viewModel.leadersList.observe(viewLifecycleOwner) { leadersList ->
            fullList.addAll(leadersList)
            filter("")
        }
    }

    private fun setUpSearchView() {
        binding.searchViewAll.setOnQueryTextListener(this@SearchJamaaFragment)
    }

    override fun onQueryTextSubmit(newText: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText ?: "")
        return true
    }

    private fun filter(text: String) {
        val filteredList = if (text.isEmpty()) {
            emptyList()
        } else {
            fullList.filter { it.name.contains(text, ignoreCase = true) }.toSet()
        }

        searchList.clear()
        searchList.addAll(filteredList.sortedBy { it.name }.toMutableList())
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()
    }
    private fun setUpRecyclers() {

        binding.searchPeopleRecyclerView.apply {
            adapter = NetworkAdapter(requireContext(), searchList)
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

}