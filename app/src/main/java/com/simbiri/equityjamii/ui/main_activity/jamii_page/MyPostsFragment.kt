package com.simbiri.equityjamii.ui.main_activity.jamii_page

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
import com.simbiri.equityjamii.constants.USER_ID
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
    val queryReference = firestore.collection("Post_Gallery")
    private var postList: MutableList<Post> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MyPostsTabBinding.inflate(layoutInflater)
        val view = binding.root

        myActivityAdapter = PostAdapter(requireContext(), postList, true)
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

    fun listMyPostsFirestore() {
        AuthUtils.getCurrentPerson (AuthUtils.getCurrentUserId()!!){ currentPerson ->

            queryReference.orderBy("time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val posts = it.result.toObjects(Post::class.java)
                        if (currentPerson != null) {
                            postList.clear()
                            val includedPosts =
                                posts.filter { p -> p.userId.contentEquals(currentPerson.userId) }
                                    .toMutableList()

                            if (includedPosts.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Your added posts will be included here",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            postList.addAll(includedPosts)
                        }


                        Log.i("Feed in myposts", "${postList}")
                        binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()

                    }
                }
        }

    }


    override fun onPause() {
        super.onPause()
        this@MyPostsFragment.onDestroy()
    }
    override fun onResume() {
        super.onResume()
        if (postList.isEmpty()) {
            listMyPostsFirestore()
        }
        if (binding.myPostsRecyclerView.visibility == View.INVISIBLE){
            binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()
        }

    }


}