package com.simbiri.equityjamii.ui.main_activity.people_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    private var searchList: MutableList<Person> = mutableListOf()
    private lateinit var binding: PeoplePageAllPeopleBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeoplePageAllPeopleBinding.inflate(layoutInflater)
        val view = binding.root

        setUpRecyclers()

        AuthUtils.getCurrentPerson(viewModel.currentId){
            if (it !=null){
                setUpObservers()
            }
        }

        return view
    }

    private fun setUpObservers() {
        viewModel.jamaaList.observe(viewLifecycleOwner) { jamaaList ->
            searchList.clear()
            searchList.addAll(jamaaList)
            binding.allPeopleRecycler.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclers() {

        binding.allPeopleRecycler.apply {
            allAdapter = PeopleDataAdapter(context, searchList)
            layoutManager = GridLayoutManager(context,2)
            adapter = allAdapter
        }
    }


}
