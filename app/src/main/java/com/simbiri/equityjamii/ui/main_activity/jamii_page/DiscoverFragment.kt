package com.simbiri.equityjamii.ui.main_activity.jamii_page

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
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.Post

class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private lateinit var viewModel: DiscoverViewModel
    private lateinit var recyclerPosts: RecyclerView
    private lateinit var adapterPost: PostAdapter
    val firestore = FirebaseFirestore.getInstance()
    lateinit var listenerRegistration: ListenerRegistration
    val queryReference = firestore.collection("Post_Gallery")
    private val postList:MutableList<Post> = mutableListOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.discover_tab, container, false)
        genListPosts()

        recyclerPosts = view.findViewById(R.id.discoverRecyclerView)
        adapterPost = PostAdapter(requireContext(), postList)
        recyclerPosts.adapter = adapterPost


        setUpPostRecycler()

        return view
    }


    fun listenerRegisterForPosts() {

        listenerRegistration = queryReference.addSnapshotListener { snapshots, error ->
            var changesDetected = false
            for (doc in snapshots!!.documentChanges) {
                if (doc.type == DocumentChange.Type.ADDED) {
                    val newPost = doc.document.toObject(Post::class.java)
                    postList.add(newPost)
                    changesDetected = true
                }
            }

            !changesDetected
        }
    }

    fun genListPosts() {
        queryReference.orderBy("time", Query.Direction.DESCENDING)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val posts = it.result.toObjects(Post::class.java)
                    postList.clear()
                    postList.addAll(posts)
                    Log.i("Feed discover in disc", postList.toString())
                    recyclerPosts.adapter!!.notifyDataSetChanged()
                }
            }
    }


    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerPosts.layoutManager = layoutManager

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiscoverViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
