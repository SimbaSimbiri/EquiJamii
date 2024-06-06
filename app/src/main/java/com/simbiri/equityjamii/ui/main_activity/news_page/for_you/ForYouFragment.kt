package com.simbiri.equityjamii.ui.main_activity.news_page.for_you

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
import com.simbiri.equityjamii.adapters.FypNewsAdapter
import com.simbiri.equityjamii.adapters.NewsAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.databinding.NewsPageFeaturingBinding
import com.simbiri.equityjamii.databinding.NewsPageForYouBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

    private val viewModel: NewsViewModel by viewModels(ownerProducer =  { requireParentFragment() })
    private lateinit var binding : NewsPageForYouBinding
    private var canPublishEdit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = NewsPageForYouBinding.inflate(inflater, container, false)

        if (AuthUtils.getCurrentUserId() != null) {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                canPublishEdit = currentPerson?.role?.contentEquals("journalist") == true

                binding.elpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                setUpObservers()
            }
        }

        return binding.root
    }
    private fun setUpObservers(){
        viewModel.newsList.observe(viewLifecycleOwner){ allNewsInstances ->
/*
            val featureNews = allNewsInstances.filter { newsInst -> newsInst.newsName.contentEquals("featuring") }
*/
            val adapter = NewsAdapter(requireContext(), allNewsInstances, canPublishEdit)
            binding.elpRecyclerView.adapter = adapter
            binding.elpRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }


}

