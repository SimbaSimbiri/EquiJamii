package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.FeedTabBinding

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private var currPerson: Person? = Person()
    private val viewModel: FeedViewModel by viewModels()
    private lateinit var binding: FeedTabBinding
    private lateinit var feedAdapter: PostAdapter
    val firestore = FirebaseFirestore.getInstance()
    val queryReference = firestore.collection("Post_Gallery")
    private val postList: MutableList<Post> = mutableListOf()


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedTabBinding.inflate(layoutInflater)
        val view = binding.root

        feedAdapter = PostAdapter(requireContext(), postList)
        binding.feedRecyclerView.adapter = feedAdapter

        setUpPostRecycler()

        return view
    }


    private fun genListPosts() {
        AuthUtils.getCurrentPerson { currentPerson ->
            currPerson = currentPerson
            queryReference.orderBy("time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {

                        if (currentPerson != null) {
                            val posts = it.result.toObjects(Post::class.java)

                            val includeFeedPost =
                                posts.filter { post ->
                                    currentPerson.network.followingList?.contains(post.userId) == true ||
                                            post.userId.contentEquals(currentPerson.userId)
                                }.toMutableList()

                            if (includeFeedPost.isEmpty()){
                                Toast.makeText(requireContext(), "Post from people you follow and yours will be posted here", Toast.LENGTH_SHORT).show()
                            }

                            postList.clear()
                            postList.addAll(includeFeedPost)
                        }

                        Log.i("FeedList in feed", "${postList}")
                        binding.feedRecyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
        }

    }

    override fun onResume() {
        super.onResume()
        if (postList.isEmpty()) {
            genListPosts()
        }
    }

    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.feedRecyclerView.layoutManager = layoutManager

    }

}