package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.EventsAdapter
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.databinding.MyEventsFragmentBinding

class MyEventsFragment : Fragment() {

    companion object {
        fun newInstance() = MyEventsFragment()
    }

    private lateinit var binding: MyEventsFragmentBinding
    private var eventsList = mutableListOf<Event>()
    private val viewModel = EventsViewModel()
    private val currentUserId = AuthUtils.getCurrentUserId()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyEventsFragmentBinding.inflate(layoutInflater)

        binding.myEventsRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = EventsAdapter(requireActivity(), eventsList, true)
            hasFixedSize()
        }

        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.eventList.observe(viewLifecycleOwner) { allEvents ->
            eventsList.clear()
            eventsList.addAll(allEvents.filter { event: Event ->
                event.userId.contentEquals(currentUserId)
            })
            binding.myEventsRecycler.adapter!!.notifyDataSetChanged()

        }
    }

}