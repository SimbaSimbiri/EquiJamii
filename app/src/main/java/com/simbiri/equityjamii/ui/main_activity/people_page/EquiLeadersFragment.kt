package com.simbiri.equityjamii.ui.main_activity.people_page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.simbiri.equityjamii.adapters.LeadersAllAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.PeoplePageEquileadersBinding

class EquiLeadersFragment : Fragment() {

    companion object {
        fun newInstance() = EquiLeadersFragment()
    }

    private lateinit var leadersAdapter: LeadersAllAdapter
    private var viewModel = PeopleViewModel()
    private var searchList: MutableList<Person> = mutableListOf()
    private lateinit var binding: PeoplePageEquileadersBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AuthUtils.getCurrentPerson(viewModel.currentId) {
            if (it != null) {
                setUpObservers()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeoplePageEquileadersBinding.inflate(layoutInflater)
        val view = binding.root

        setUpRecyclers()

        return view
    }

    private fun setUpObservers() {
        viewModel.leadersList.observe(viewLifecycleOwner) { leadersList ->
            searchList.clear()
            searchList.addAll(leadersList)
            binding.equiLeadersRecycler.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclers() {
        binding.equiLeadersRecycler.apply {
            leadersAdapter = LeadersAllAdapter(context, searchList)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = leadersAdapter
        }
    }

}
