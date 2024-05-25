package com.simbiri.equityjamii.ui.main_activity.people_page

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var viewModel: AllPeopleViewModel
    private lateinit var searchList: MutableList<Person>
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val query = firestore.collection("Users")
    private lateinit var listenerRegistration: ListenerRegistration
    private  lateinit var binding : PeoplePageAllPeopleBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)

        peopleListFireStore()
        listenerRegisterForUsers()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PeoplePageAllPeopleBinding.inflate(layoutInflater)
        val view = binding.root

        searchList = mutableListOf()
        setUpRecyclers()

        if (firebaseAuth.currentUser != null) {
            peopleListFireStore()
        }

        listenerRegisterForUsers()

        return view
    }

    private fun listenerRegisterForUsers() {
        listenerRegistration = query.addSnapshotListener { snapShots, error ->

            for (doc in snapShots!!.documentChanges) {
                if (doc.type == DocumentChange.Type.ADDED) {
                    val newPerson = doc.document.toObject(Person::class.java)
                    if (!newPerson.userId.contentEquals(AuthUtils.getCurrentUserId()!!)){
                    searchList.add(newPerson)}
                    binding.allPeopleRecycler.adapter!!.notifyDataSetChanged()

                }
            }

        }
    }

    private fun peopleListFireStore() {
        query.get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                searchList = it.result.toObjects(Person::class.java).filter{person -> person.userId.contentEquals(
                    AuthUtils.getCurrentUserId()!!) }
                    .toMutableList()

                binding.allPeopleRecycler.adapter!!.notifyDataSetChanged()
            }
        }

    }

    private fun setUpRecyclers() {
        val context = requireContext()

        allAdapter = PeopleDataAdapter(context, searchList)
        val layoutManagerAll = GridLayoutManager(context, 2)
        layoutManagerAll.orientation = RecyclerView.VERTICAL
        binding.allPeopleRecycler.adapter = allAdapter
        binding.allPeopleRecycler.layoutManager = layoutManagerAll

    }

    override fun onStop() {
        super.onStop()

        listenerRegistration.remove()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllPeopleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
