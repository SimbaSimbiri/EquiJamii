package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.EventsAdapter
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.MyActivityTabBinding

class MyActivityFragment : Fragment() {

    companion object {
        fun newInstance() = MyActivityFragment()
    }

    private var currPerson = Person()

    private val viewModelEvent = EventsViewModel()
    private val viewModelPosts = JamiiPageViewModel()

    private lateinit var binding: MyActivityTabBinding
    private lateinit var myActivityAdapter: PostAdapter
    private lateinit var myEventsAdapter: EventsAdapter
    private val USER_ID = AuthUtils.getCurrentUserId()

    private var postList: MutableList<Post> = mutableListOf()
    private var eventsList = mutableListOf<Event>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivityAdapter = PostAdapter(context, postList, true)
        myEventsAdapter = EventsAdapter(context, eventsList, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MyActivityTabBinding.inflate(layoutInflater)
        val view = binding.root

        setUpRecyclers()

        binding.addEvent.setOnClickListener {
            val addNewEvent = AddEventsDialog()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            addNewEvent.show(transaction, addNewEvent.tag)
        }

        AuthUtils.getCurrentPerson(USER_ID) { currentPerson ->
            if (currentPerson != null) {
                currPerson = currentPerson
                setUpEventObservers(currPerson)
                setUpPostObservers(currPerson)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Sign up first before accessing feed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return view
    }

    private fun setUpRecyclers() {
        binding.myEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myEventsAdapter
            hasFixedSize()
        }

        binding.myPostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myActivityAdapter
            hasFixedSize()
        }
    }

    private fun setUpPostObservers(currentPerson: Person) {
        viewModelPosts.allPosts.observe(viewLifecycleOwner) { allPosts ->

            val includedPosts =
                allPosts.filter { p -> p.userId.contentEquals(currentPerson.userId) }
                    .toMutableList()

            if (includedPosts.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Your added posts will be included here",
                    Toast.LENGTH_SHORT
                ).show()
            }

            postList.clear()
            postList.addAll(includedPosts)
            binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpEventObservers(currentPerson: Person) {
        viewModelEvent.eventList.observe(viewLifecycleOwner) { allEvents ->
            eventsList.clear()
            eventsList.addAll(allEvents.filter { event: Event ->
                event.userId.contentEquals(currentPerson.userId)
            })
            binding.myEventsRecyclerView.adapter!!.notifyDataSetChanged()

        }
    }


}