package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.FeedTabBinding

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private var currPerson: Person? = Person()
    private val viewModel = JamiiPageViewModel()
    private lateinit var binding: FeedTabBinding
    private val USER_ID = AuthUtils.getCurrentUserId()
    private val postList: MutableList<Post> = mutableListOf()
    private  lateinit var feedAdapter: PostAdapter
    val firestore = FirebaseFirestore.getInstance()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        feedAdapter  = PostAdapter(requireContext(), postList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AuthUtils.getCurrentPerson(USER_ID) { currentPerson ->
            if (currentPerson != null) {
                currPerson = currentPerson
                setUpObservers(currentPerson)
            }

        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedTabBinding.inflate(layoutInflater)
        val view = binding.root


        setUpPostRecycler()

        return view
    }

    private fun setUpObservers(currPerson: Person) {

        viewModel.allPosts.observe(viewLifecycleOwner) { allPosts ->
            val includeFeedPost =
                allPosts.filter { post ->
                    currPerson.network.followingList?.contains(post.userId) == true ||
                            post.userId.contentEquals(currPerson.userId)
                }.toMutableList()

            if (includeFeedPost.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Post from people you follow and yours will be posted here",
                    Toast.LENGTH_SHORT
                ).show()
            }

            postList.clear()
            postList.addAll(includeFeedPost)
            binding.feedRecyclerView.adapter!!.notifyDataSetChanged()

        }

    }

    override fun onResume() {
        super.onResume()

        if (binding.feedRecyclerView.visibility == View.INVISIBLE) {
            binding.feedRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpPostRecycler() {
        binding.feedRecyclerView.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

    }


    override fun onPause() {
        super.onPause()
        this@FeedFragment.onDestroy()
    }

}