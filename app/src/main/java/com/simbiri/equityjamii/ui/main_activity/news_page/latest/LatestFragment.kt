package com.simbiri.equityjamii.ui.main_activity.news_page.latest

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NewsAdapter
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.data.model.YouTubeVids
import com.simbiri.equityjamii.databinding.NewsPageTopStoriesBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class LatestFragment : Fragment() {

    companion object {
        fun newInstance() = LatestFragment()
    }

    private val viewModel: NewsViewModel by viewModels(ownerProducer =  { requireParentFragment() })
    private lateinit var binding : NewsPageTopStoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = NewsPageTopStoriesBinding.inflate(inflater, container, false)

        binding.newsRecylerView.layoutManager =  LinearLayoutManager(requireContext())
        setUpObservers()

        return binding.root
    }

    private fun setUpObservers(){
        viewModel.newsList.observe(viewLifecycleOwner){ allNewsInstances ->
            val adapter = NewsAdapter(requireContext(), allNewsInstances, true)
            binding.newsRecylerView.adapter = adapter
            binding.newsRecylerView.adapter!!.notifyDataSetChanged()
        }
    }

}
