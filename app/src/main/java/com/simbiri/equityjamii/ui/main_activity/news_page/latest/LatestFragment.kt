package com.simbiri.equityjamii.ui.main_activity.news_page.latest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.NewsAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.NewsPageTopStoriesBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class LatestFragment : Fragment() {

    companion object {
        fun newInstance() = LatestFragment()
    }

    private val viewModel: NewsViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var binding: NewsPageTopStoriesBinding
    private var canPublishEdit = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AuthUtils.getCurrentUserId() != null) {

            setUpObservers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewsPageTopStoriesBinding.inflate(inflater, container, false)

        if (AuthUtils.getCurrentUserId() != null) {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                canPublishEdit = currentPerson?.role?.contentEquals("journalist") == true

                binding.newsRecylerView.layoutManager = LinearLayoutManager(context)
            }
        }

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.newsList.observe(viewLifecycleOwner) { allNewsInstances ->
            val adapter = context?.let { NewsAdapter(it, allNewsInstances, canPublishEdit) }
            binding.newsRecylerView.adapter = adapter
            binding.newsRecylerView.adapter!!.notifyDataSetChanged()
        }
    }

}
