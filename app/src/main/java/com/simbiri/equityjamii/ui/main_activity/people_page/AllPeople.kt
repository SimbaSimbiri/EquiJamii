package com.simbiri.equityjamii.ui.main_activity.people_page

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PeopleDataAdapter
import com.simbiri.equityjamii.data.model.PeopleData
import com.simbiri.equityjamii.data.model.Person
import java.util.*
import kotlin.collections.ArrayList

class AllPeople : Fragment() {

    companion object {
        fun newInstance() = AllPeople()
    }

    private lateinit var viewModel: AllPeopleViewModel
    private lateinit var recyclerRecFollow: RecyclerView
    private lateinit var recyclerAll: RecyclerView
    private lateinit var searchAll: SearchView
    private lateinit var searchList: ArrayList<Person>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.people_page_all_people, container, false)
        recyclerAll = view.findViewById(R.id.allPeopleRecycler)
/*
        recyclerRecFollow = view.findViewById(R.id.peopleFollowRecycler)
*/
        searchAll = view.findViewById(R.id.searchViewAll)
        searchList = PeopleData.peopleList!!

        setUpRecyclers(view)


/*

        searchAll.apply {
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchAll.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val searchText = newText!!.lowercase(Locale.getDefault())

                    if (searchText.isNotEmpty()) {

                        searchForAll(searchText)

                    } else {
                        searchList.clear()
                        searchList.addAll(PeopleData.peopleList!!)
                        recyclerAll.adapter!!.notifyDataSetChanged()
                    }


                    return false
                }


            })
        }*/

        return view
    }

    private fun searchForAll(searchText: String) {
        PeopleData.peopleList?.forEach {

            if (it.name.lowercase(Locale.getDefault()).contains(searchText)) {
                searchList.add(it)

            }
            recyclerAll.adapter!!.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()
    }

    private fun setUpRecyclers(view: View?) {
        val context = requireContext()

        val allAdapter = PeopleDataAdapter(context, searchList)
/*
        val recommendFollowAdapter = PeopleDataAdapter(context, PeopleData.peopleList!!)
*/

        val layoutManagerAll = GridLayoutManager(context,2)
/*
        val layoutManagerRecommend = LinearLayoutManager(context)
*/

        layoutManagerAll.orientation = RecyclerView.VERTICAL
/*
        layoutManagerRecommend.orientation = RecyclerView.HORIZONTAL
*/


        recyclerAll.adapter = allAdapter
/*
        recyclerRecFollow.adapter = recommendFollowAdapter
*/

        recyclerAll.layoutManager = layoutManagerAll
/*
        recyclerRecFollow.layoutManager = layoutManagerRecommend
*/

        recyclerAll.hasFixedSize()
/*
        recyclerRecFollow.hasFixedSize()
*/

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllPeopleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
