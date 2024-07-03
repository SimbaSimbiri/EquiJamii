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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.DialogSearchJamaaBinding

class SearchJamaaFragment : BottomSheetDialogFragment(), SearchView.OnQueryTextListener {

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

    override fun onQueryTextSubmit(query: String?): Boolean {
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
            fullList.filter { it.name.contains(text, ignoreCase = true) }
        }

        searchList.clear()
        searchList.addAll(filteredList)
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()
    }
    private fun setUpRecyclers() {

        binding.searchPeopleRecyclerView.apply {
            adapter = NetworkAdapter(requireContext(), searchList)
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {

            setContentView(R.layout.dialog_search_jamaa)
            setCanceledOnTouchOutside(true)
            val bottomSheetDialog = this as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            val displayMetrics = DisplayMetrics()
            val windowManager =
                requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            setupFullHeight(bottomSheet)

            setOnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(bottomSheet)
                    behavior.apply {
                        isDraggable = true
                        isHideable = true
                        peekHeight = displayMetrics.heightPixels
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }

                }
            }
        }

        return dialog
    }

}