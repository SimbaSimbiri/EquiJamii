package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class EquiLeadersFragment : Fragment() {

    companion object {
        fun newInstance() = EquiLeadersFragment()
    }

    private lateinit var viewModel: EquiLeadersViewModel
    private lateinit var recyclerLeaders: RecyclerView
    private lateinit var searchLeaders: SearchView
    private lateinit var searchList: ArrayList<Person>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.equileadersfrag, container, false)

        recyclerLeaders = view.findViewById(R.id.equiLeadersRecycler)
        searchLeaders = view.findViewById(R.id.searchViewEquiLeaders)
        searchList = PeopleData.peopleList!!

        setUpLeaderRecycler(view)


        /*searchLeaders.apply {
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchLeaders.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val searchText = newText!!.lowercase(Locale.getDefault())

                    if (searchText.isNotEmpty()) {

                        searchForLeader(searchText)
                    } else {
                        searchList.clear()
                        searchList.addAll(PeopleData.peopleList!!)
                        recyclerLeaders.adapter!!.notifyDataSetChanged()
                    }

                    return false
                }
            })
        }*/



        return view
    }

    private fun searchForLeader(searchText: String) {

        PeopleData.peopleList?.forEach {
            if (it.name.lowercase(Locale.getDefault()).contains(searchText)) {
                searchList.add(it)
            }
        }
        recyclerLeaders.adapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setUpLeaderRecycler(view: View?) {
        val context = requireContext()
        val peopleAdapter = PeopleDataAdapter(context, searchList)
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.orientation = RecyclerView.VERTICAL

        recyclerLeaders.adapter = peopleAdapter
        recyclerLeaders.layoutManager = layoutManager
        recyclerLeaders.hasFixedSize()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EquiLeadersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
