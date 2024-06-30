package com.simbiri.equityjamii.ui.main_activity.news_page.for_you

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.adapters.NewsAdapter
import com.simbiri.equityjamii.adapters.TagNewsAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.databinding.NewsPageForYouBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.NewsViewModel

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

    private val viewModel: NewsViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var binding: NewsPageForYouBinding
    private var canPublishEdit = false
    private var allNews = mutableListOf<NewsText>()
    private var filteredNews = mutableListOf<NewsText>()
    private val selectedTags = mutableSetOf<String>()

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

        binding = NewsPageForYouBinding.inflate(inflater, container, false)


        if (AuthUtils.getCurrentUserId() != null) {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
                canPublishEdit = currentPerson?.role?.contentEquals("journalist") == true

                binding.forYouRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.tagsRecyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.newsList.observe(viewLifecycleOwner) { allNewsInstances ->

            allNews = allNewsInstances.filter { newsText ->
                !newsText.newsTag.contentEquals(
                    "official",
                    true
                )
            }.filter { newsText ->
                !newsText.newsName.contentEquals(
                    "featuring",
                    true
                )

            }.toMutableList()

            filteredNews = allNews.toMutableList()

        }

        viewModel.selectedTagList.observe(viewLifecycleOwner) { allTags ->
            val tagsAdapter = TagNewsAdapter(allTags) { tag ->

                if (tag.isSelected) {
                    selectedTags.add(tag.name)
                } else {
                    selectedTags.remove(tag.name)
                }

                filterNews()
                updateNewsAdapter()
            }

            binding.tagsRecyclerView.adapter = tagsAdapter
            binding.tagsRecyclerView.adapter!!.notifyDataSetChanged()


        }
    }


    private fun filterNews() {

        if (filteredNews.isNotEmpty()) filteredNews.clear()

        filteredNews.addAll(allNews.filter { newsText ->
            selectedTags.any { tag -> newsText.newsTag.contentEquals(tag, true) }
        })

        updateNewsAdapter()
    }

    private fun updateNewsAdapter() {
        val adapter = context?.let {
            NewsAdapter(it, filteredNews, canPublishEdit)
        }

        binding.forYouRecyclerView.adapter = adapter
        binding.forYouRecyclerView.adapter!!.notifyDataSetChanged()
    }

}

