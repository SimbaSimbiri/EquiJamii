package com.simbiri.equityjamii.ui.main_activity.news_page.featuring

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NewsAdapter
import com.simbiri.equityjamii.databinding.NewsPageFeaturingBinding
import com.simbiri.equityjamii.databinding.NewsPageTopStoriesBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class FeaturingFragment : Fragment() {

    companion object {
        fun newInstance() = FeaturingFragment()
    }

    private val viewModel: NewsViewModel by viewModels(ownerProducer =  { requireParentFragment() })
    private lateinit var binding : NewsPageFeaturingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewsPageFeaturingBinding.inflate(inflater, container, false)

        binding.featuringRecyclerView.layoutManager =  LinearLayoutManager(requireContext())
        setUpObservers()

        return binding.root
    }

    private fun setUpObservers(){
        viewModel.newsList.observe(viewLifecycleOwner){ allNewsInstances ->
/*
            val featureNews = allNewsInstances.filter { newsInst -> newsInst.newsName.contentEquals("featuring") }
*/
            val adapter = NewsAdapter(requireContext(), allNewsInstances, true)
            binding.featuringRecyclerView.adapter = adapter
            binding.featuringRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }


}