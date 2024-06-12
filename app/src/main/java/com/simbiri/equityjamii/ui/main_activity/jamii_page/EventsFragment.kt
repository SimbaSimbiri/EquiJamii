package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.EventsAdapter
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.databinding.EventsFragBinding

class EventsFragment : Fragment() {

    companion object {
        fun newInstance() = EventsFragment()
    }

    private val viewModel: EventsViewModel by viewModels()
    private lateinit var binding: EventsFragBinding
    private var eventsList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EventsFragBinding.inflate(inflater, container, false)

        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = EventsAdapter(requireActivity(), eventsList)
            hasFixedSize()
        }

        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.eventList.observe(viewLifecycleOwner) { allEvents ->
            eventsList.clear()
            eventsList.addAll(allEvents)
            binding.eventsRecyclerView.adapter!!.notifyDataSetChanged()

        }
    }
}