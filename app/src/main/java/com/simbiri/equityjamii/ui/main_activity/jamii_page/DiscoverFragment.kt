package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.DiscoverTabBinding

class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private lateinit var viewModel: DiscoverViewModel
    private lateinit var adapterPost: PostAdapter
    val firestore = FirebaseFirestore.getInstance()
    val queryReference = firestore.collection("Post_Gallery")
    private val postList: MutableList<Post> = mutableListOf()
    private lateinit var binding: DiscoverTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DiscoverTabBinding.inflate(layoutInflater)
        val view = binding.root

        adapterPost = PostAdapter(requireContext(), postList)
        binding.discoverRecyclerView.adapter = adapterPost


        setUpPostRecycler()

        return view
    }


    fun genListPosts() {
        AuthUtils.getCurrentPerson { currentPerson ->
            queryReference.orderBy("time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {

                        val posts = it.result.toObjects(Post::class.java)

                        if (currentPerson != null){
                        val includeFeedPost =
                            posts.filter { post ->
                                currentPerson?.network?.followingList?.contains(post.userId) == false

                            }.filter { post -> !post.userId.contentEquals(currentPerson?.userId) }
                                .toMutableList()

                        postList.clear()
                        postList.addAll(includeFeedPost)}

                        Log.i("FeedList in feed", "${postList}")
                        binding.discoverRecyclerView.adapter!!.notifyDataSetChanged()

                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (postList.isEmpty()) {
            genListPosts()
        }
        if (binding.discoverRecyclerView.visibility == View.INVISIBLE){
            binding.discoverRecyclerView.adapter!!.notifyDataSetChanged()
        }

    }


    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.discoverRecyclerView.layoutManager = layoutManager

    }

    override fun onPause() {
        super.onPause()
        this@DiscoverFragment.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiscoverViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
