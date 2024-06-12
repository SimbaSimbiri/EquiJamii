package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.adapters.EventsAdapter
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.MyActivityTabBinding

class MyActivityFragment : Fragment() {

    companion object {
        fun newInstance() = MyActivityFragment()
    }

    private val viewModel = EventsViewModel()
    private lateinit var binding: MyActivityTabBinding
    private lateinit var myActivityAdapter: PostAdapter
    val firestore = FirebaseFirestore.getInstance()
    val queryReference = firestore.collection("Post_Gallery")
    private var postList: MutableList<Post> = mutableListOf()
    private var eventsList = mutableListOf<Event>()
    private val currentUserId = AuthUtils.getCurrentUserId()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MyActivityTabBinding.inflate(layoutInflater)
        val view = binding.root

        binding.myEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = EventsAdapter(requireActivity(), eventsList, true)
            hasFixedSize()
        }

        myActivityAdapter = PostAdapter(requireContext(), postList, true)

        binding.myPostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myActivityAdapter
            hasFixedSize()
        }

        binding.addEvent.setOnClickListener {
            val addNewEvent = AddEventsDialog()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            addNewEvent.show(transaction, addNewEvent.tag)
        }


        setUpObservers()

        return view
    }

    private fun setUpObservers() {
        viewModel.eventList.observe(viewLifecycleOwner) { allEvents ->
            eventsList.clear()
            eventsList.addAll(allEvents.filter { event: Event ->
                event.userId.contentEquals(currentUserId)
            })
            binding.myEventsRecyclerView.adapter!!.notifyDataSetChanged()

        }
    }


    fun listMyPostsFirestore() {
        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->

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
        this@MyActivityFragment.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (postList.isEmpty()) {
            listMyPostsFirestore()
        }
        if (binding.myPostsRecyclerView.visibility == View.INVISIBLE) {
            binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()
        }

    }


}