package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.POST_COLLECTION
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.adapters.PostAdapter

class postFragment : Fragment() {

    companion object {
        fun newInstance() = postFragment()
    }

    private lateinit var viewModel: PostViewModel
    private lateinit var currentUserImageView: ImageView
    private lateinit var yourThoughtsTv : TextView
    private lateinit var recyclerPosts : RecyclerView
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()
    private val query = firestore.collection("Post_Gallery")
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var personPost : Person
    private lateinit var postList : MutableList<Post>
    private lateinit var adapterPost : PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.jamii_page, container, false)

        firestore.collection(USERS_COLLECTION).document(userId).get().addOnCompleteListener { taskDocSnapShot ->
            if (taskDocSnapShot.isSuccessful){
                if (taskDocSnapShot.result.exists()){
                    val profilePicUrl = taskDocSnapShot.result.getString("profileUri")
                    personPost = taskDocSnapShot.result.toObject(Person::class.java)!!
                    Glide.with(view.context).load(profilePicUrl).into(currentUserImageView)
                }
            }
        }

        currentUserImageView  = view.findViewById(R.id.currentUserImage)
        yourThoughtsTv = view.findViewById(R.id.yourThoughtsTv)
        recyclerPosts = view.findViewById(R.id.postsRecyclerView)
        postList = mutableListOf()
        adapterPost = PostAdapter(requireContext(), postList)
        recyclerPosts.adapter = adapterPost

        setUpPostRecycler()

        if (firebaseAuth.currentUser != null) {
            genListPosts()

        }else{Toast.makeText(requireContext(), "Sign in first to access posts", Toast.LENGTH_SHORT).show()}


        currentUserImageView.setOnClickListener {
            val addPostFragment = AddPostFragment.newInstance(personPost)
            val transaction =  requireActivity().supportFragmentManager.beginTransaction()
            addPostFragment.show(transaction, addPostFragment.tag)
        }

        yourThoughtsTv.setOnClickListener {
            val addPostFragment = AddPostFragment.newInstance(personPost)
            val transaction =  requireActivity().supportFragmentManager.beginTransaction()
            addPostFragment.show(transaction, addPostFragment.tag)
        }

        listenerRegisterForPosts()

        return view
    }

    private fun listenerRegisterForPosts() {
        listenerRegistration = query.addSnapshotListener{snapshots,error ->
            var changesDetected = false
            for (doc in snapshots!!.documentChanges){
                if (doc.type == DocumentChange.Type.ADDED){
                    val newPost = doc.document.toObject(Post::class.java)
                    postList.add(newPost)
                    changesDetected =  true
                }
            }
            if (changesDetected){
                Log.i("list size in listener", postList.size.toString())
                recyclerPosts.adapter!!.notifyDataSetChanged()
            }
            !changesDetected
           Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun genListPosts(){
        query.get().addOnCompleteListener {
            if(it.isSuccessful){
                val posts = it.result.toObjects(Post::class.java)
                postList.clear()
                postList.addAll(posts)
                Log.i("list size", postList.size.toString())
                recyclerPosts.adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun setUpPostRecycler(){
        val context =  requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerPosts.layoutManager = layoutManager

    }

    override fun onStop() {
        super.onStop()
        listenerRegistration.remove()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
