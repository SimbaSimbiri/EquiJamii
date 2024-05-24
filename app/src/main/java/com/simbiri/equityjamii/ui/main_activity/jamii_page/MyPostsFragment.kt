package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.MyPostsTabBinding

class MyPostsFragment : Fragment() {

    companion object {
        fun newInstance() = MyPostsFragment()
    }

    private val viewModel: MyPostsViewModel by viewModels()
    private lateinit var binding: MyPostsTabBinding
    private lateinit var myActivityAdapter: PostAdapter
    val firestore = FirebaseFirestore.getInstance()
    lateinit var listenerRegistration: ListenerRegistration
    val queryReference = firestore.collection("Post_Gallery")
    private var postList: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MyPostsTabBinding.inflate(layoutInflater)
        val view = binding.root

        genListPosts()

        myActivityAdapter = PostAdapter(requireContext(), postList)
        binding.myPostsRecyclerView.adapter = myActivityAdapter

        setUpPostRecycler()
        return view
    }


    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.myPostsRecyclerView.layoutManager = layoutManager

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
                    postList.addAll(posts.filter { p-> p.userId.contentEquals(AuthUtils.getCurrentUserId()!!) })

                    Log.i("Feed in myposts", "${postList}")
                    binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()

                }
            }
    }

    override fun onStop() {
        super.onStop()
    }


}