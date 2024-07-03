package com.simbiri.equityjamii.ui.main_activity.people_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.simbiri.equityjamii.adapters.PeopleDataAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.PeoplePageAllPeopleBinding

class AllPeople : Fragment() {

    companion object {
        fun newInstance() = AllPeople()
    }

    private lateinit var allAdapter: PeopleDataAdapter
    private var viewModel = PeopleViewModel()
    private var peopleList: MutableList<Person> = mutableListOf()
    private lateinit var binding: PeoplePageAllPeopleBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeoplePageAllPeopleBinding.inflate(layoutInflater)

        setUpRecyclers()

        AuthUtils.getCurrentPerson(viewModel.currentId){
            if (it !=null){
                setUpObservers()
            }
        }

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.jamaaList.observe(viewLifecycleOwner) { jamaaList ->
            peopleList.clear()
            peopleList.addAll(jamaaList)
            binding.allPeopleRecycler.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclers() {

        binding.allPeopleRecycler.apply {
            allAdapter = PeopleDataAdapter(context, peopleList)
            layoutManager = GridLayoutManager(context,2)
            adapter = allAdapter
        }
    }

}
