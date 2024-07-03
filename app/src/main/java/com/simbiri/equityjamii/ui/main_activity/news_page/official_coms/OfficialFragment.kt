package com.simbiri.equityjamii.ui.main_activity.news_page.official_coms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.OfficialAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.NewsPageOfficialBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class OfficialFragment : Fragment() {

    companion object {
        fun newInstance() = OfficialFragment()
    }

    private lateinit var binding: NewsPageOfficialBinding
    private val viewModel: NewsViewModel by viewModels(ownerProducer = { requireParentFragment() })
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
        binding = NewsPageOfficialBinding.inflate(layoutInflater)

        if (AuthUtils.getCurrentUserId() != null) {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                canPublishEdit = currentPerson?.role?.contentEquals("journalist") == true
                binding.officialRecycler.layoutManager = LinearLayoutManager(context)

            }
        }

        binding.apply {

        }

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.newsList.observe(viewLifecycleOwner) { allNewsInstances ->
            if (allNewsInstances.isNotEmpty()){
                binding.contentLoadingProgressBar.visibility =  View.INVISIBLE
            }
            val adapter = context?.let {
                OfficialAdapter(
                    it,
                    allNewsInstances.filter { newsText ->
                        newsText.newsTag.contentEquals(
                            "official",
                            true
                        )
                    }.toMutableList(), canPublishEdit
                )
            }

            binding.officialRecycler.adapter = adapter
            binding.officialRecycler.adapter!!.notifyDataSetChanged()
        }
    }


}