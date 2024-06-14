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
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.PeoplePageEquileadersBinding

class EquiLeadersFragment : Fragment() {

    companion object {
        fun newInstance() = EquiLeadersFragment()
    }

    private lateinit var leadersAdapter: LeadersAllAdapter
    private lateinit var viewModel: EquiLeadersViewModel
    private lateinit var searchList: MutableList<Person>
    private val firestore = FirebaseFirestore.getInstance()
    private val query = firestore.collection("Users")
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var binding: PeoplePageEquileadersBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PeoplePageEquileadersBinding.inflate(layoutInflater)
        val view = binding.root

        searchList = mutableListOf()
        setUpRecyclers()

        return view
    }

    override fun onResume() {
        super.onResume()
        if (searchList.isEmpty()) {
            leadersListFireStore()
        }
    }

    private fun listenerRegisterForLeaders() {
        listenerRegistration = query.addSnapshotListener { snapShots, error ->

            for (doc in snapShots!!.documentChanges) {
                if (doc.type == DocumentChange.Type.ADDED) {
                    val newLeader = doc.document.toObject(Person::class.java)
                    if (!newLeader.userId.contentEquals(AuthUtils.getCurrentUserId()!!)) {
                        searchList.add(newLeader)
                    }
                    binding.equiLeadersRecycler.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private fun leadersListFireStore() {
        AuthUtils.getCurrentPerson (AuthUtils.getCurrentUserId()!!){ currPerson ->

            query.get().addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    if (currPerson != null) {
                        val allLeaders = it.result.toObjects(Person::class.java)

                        val includedLeaders = allLeaders.filter { person -> person.leader }

                        searchList.clear()
                        searchList.addAll(includedLeaders)
                    }

                    binding.equiLeadersRecycler.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setUpRecyclers() {
        val context = requireContext()

        leadersAdapter = LeadersAllAdapter(context, searchList)
        val layoutManagerAll = LinearLayoutManager(context)
        layoutManagerAll.orientation = RecyclerView.VERTICAL
        binding.equiLeadersRecycler.adapter = leadersAdapter
        binding.equiLeadersRecycler.layoutManager = layoutManagerAll
        }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EquiLeadersViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
