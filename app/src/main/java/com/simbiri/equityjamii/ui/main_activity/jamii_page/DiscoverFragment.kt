package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.DiscoverTabBinding

class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private var viewModel = JamiiPageViewModel()
    private var currPerson: Person? = Person()
    private val USER_ID = AuthUtils.getCurrentUserId()

    private lateinit var adapterPost: PostAdapter
    private val postList: MutableList<Post> = mutableListOf()
    private lateinit var binding: DiscoverTabBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapterPost = PostAdapter(context, postList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AuthUtils.getCurrentPerson(USER_ID) { currentPerson ->
            if (currentPerson != null) {
                currPerson = currentPerson
                setUpObservers(currentPerson)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Sign up first before accessing discover",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DiscoverTabBinding.inflate(layoutInflater)
        val view = binding.root

        setUpPostRecycler()

        return view
    }

    private fun setUpObservers(currPerson: Person) {
        viewModel.allPosts.observe(viewLifecycleOwner) { allPosts ->
            val includeDiscoverPost =
                allPosts.filter { post ->
                    currPerson.network.followingList?.contains(post.userId) == false
                }.filter { post -> !post.userId.contentEquals(currPerson.userId) }
                    .toMutableList().toMutableList()

            if (includeDiscoverPost.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Post from people you follow and yours will be posted here",
                    Toast.LENGTH_SHORT
                ).show()
            }

            postList.clear()
            postList.addAll(includeDiscoverPost)
            binding.discoverRecyclerView.adapter!!.notifyDataSetChanged()

        }

    }


    private fun setUpPostRecycler() {
        binding.discoverRecyclerView.apply {
            adapter = adapterPost
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }


}
