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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PeopleDataAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person

class AllPeople : Fragment() {

    companion object {
        fun newInstance() = AllPeople()
    }

    private lateinit var allAdapter: PeopleDataAdapter
    private lateinit var viewModel: AllPeopleViewModel
    private lateinit var recyclerAll: RecyclerView
    private lateinit var searchAll: SearchView
    private lateinit var searchList: MutableList<Person>
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val query = firestore.collection("Users")
    private lateinit var listenerRegistration: ListenerRegistration


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.people_page_all_people, container, false)
        recyclerAll = view.findViewById(R.id.allPeopleRecycler)
        searchList = mutableListOf()
        setUpRecyclers(view)
        searchAll = view.findViewById(R.id.searchViewAll)

        if (firebaseAuth.currentUser != null) {
            genListPersonFireStore()
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
                    recyclerAll.adapter!!.notifyDataSetChanged()

                }
                recyclerAll.adapter!!.notifyDataSetChanged()

            }

        }
    }

    private fun genListPersonFireStore() {
        query.get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                searchList = it.result.toObjects(Person::class.java).filter{person -> person.userId.contentEquals(
                    AuthUtils.getCurrentUserId()!!) }
                    .toMutableList()

                recyclerAll.adapter!!.notifyDataSetChanged()
            }
        }

    }

    private fun setUpRecyclers(view: View?) {
        val context = requireContext()
        allAdapter = PeopleDataAdapter(context, searchList)
        val layoutManagerAll = GridLayoutManager(context, 2)
        layoutManagerAll.orientation = RecyclerView.VERTICAL
        recyclerAll.adapter = allAdapter
        recyclerAll.layoutManager = layoutManagerAll

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
