package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.adapters.EventsAdapter
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
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
    private lateinit var registeredEventsAdapter: EventsAdapter
    private val USER_ID = AuthUtils.getCurrentUserId()

    private var postList: MutableList<Post> = mutableListOf()
    private var eventsList = mutableListOf<Event>()
    private var registeredList = mutableListOf<Event>()

    private val firestoreCollection = FirebaseFirestore.getInstance().collection(EVENTS_C0LLECTION)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivityAdapter = PostAdapter(context, postList, true)
        myEventsAdapter = EventsAdapter(context, eventsList, true)
        registeredEventsAdapter = EventsAdapter(context, registeredList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AuthUtils.getCurrentPerson(USER_ID) { currentPerson ->
            if (currentPerson != null) {
                currPerson = currentPerson
                setUpEventObservers(currPerson.userId)
                setUpPostObservers(currPerson.userId)

                binding.cardAddEvent.setOnClickListener {
                    binding.contentLoadingProgressBar.visibility = View.VISIBLE
                    val addNewEvent = AddEventsDialog()
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    addNewEvent.show(transaction, addNewEvent.tag)

                    Handler().postDelayed({
                        binding.contentLoadingProgressBar.visibility = View.INVISIBLE
                    },4000)
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MyActivityTabBinding.inflate(layoutInflater)
        val view = binding.root

        setUpRecyclers()

        return view
    }

    private fun setUpRecyclers() {
        binding.myEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myEventsAdapter
        }

        binding.myPostsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myActivityAdapter
        }

        binding.myRegisteredEventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = registeredEventsAdapter
        }
    }

    private fun setUpPostObservers(userId: String) {
        viewModelPosts.allPosts.observe(viewLifecycleOwner) { allPosts ->

            val includedPosts =
                allPosts.filter { p -> p.userId.contentEquals(userId) }
                    .toMutableList()

            postList.clear()
            postList.addAll(includedPosts)
            binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setUpEventObservers(userId: String) {
        viewModelEvent.eventList.observe(viewLifecycleOwner) { allEvents ->
            eventsList.clear()
            eventsList.addAll(allEvents.filter { event: Event ->
                event.userId.contentEquals(userId)
            })
            binding.myEventsRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModelEvent.registeredEventsList.observe(viewLifecycleOwner) { registeredEvents ->
            registeredList.clear()
            registeredList.addAll(registeredEvents.sortedBy { event: Event -> event.dateTime })

            binding.myRegisteredEventsRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun checkIfregistered(event: Event, userId: String?) {
        firestoreCollection.document(event.documentId!!).collection(EVENTS_C0LLECTION)
            .document(userId!!).get().addOnSuccessListener { taskSnapShot ->
                if (taskSnapShot.exists()) {
                    registeredList.add(event)
                }
            }
        binding.myRegisteredEventsRecyclerView.adapter!!.notifyDataSetChanged()
    }


}