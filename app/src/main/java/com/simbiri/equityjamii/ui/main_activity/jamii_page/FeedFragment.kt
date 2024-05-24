package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.FeedTabBinding

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var binding: FeedTabBinding
    private lateinit var feedAdapter: PostAdapter
    private val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    lateinit var listenerRegistration: ListenerRegistration
    val queryReference = firestore.collection("Post_Gallery")
    private val postList:MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedTabBinding.inflate(layoutInflater)
        val view = binding.root

        genListPosts()
        feedAdapter = PostAdapter(requireContext(), postList)
        binding.feedRecyclerView.adapter = feedAdapter
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
                    postList.reverse()
                    Log.i("FeedList in feed", "${postList}")
                    binding.feedRecyclerView.adapter!!.notifyDataSetChanged()
                }
            }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onStop() {
        super.onStop()
    }
    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.feedRecyclerView.layoutManager = layoutManager

    }

}