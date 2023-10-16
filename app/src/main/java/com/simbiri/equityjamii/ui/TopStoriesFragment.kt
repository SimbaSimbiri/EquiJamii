package com.simbiri.equityjamii.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R

class TopStoriesFragment : Fragment() {

    companion object {
        fun newInstance() = TopStoriesFragment()
    }

    private lateinit var viewModel: TopStoriesViewModel
    private lateinit var recyclerNews: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val  view =  inflater.inflate(R.layout.top_stories_fragment, container, false)

        recyclerNews = view.findViewById(R.id.newsRecylerView)
        setUpRecyclerNews(view)



        return view
    }


    private fun setUpRecyclerNews(view: View?) {
        val context = requireContext()
        val newsAdapter =  NewsAdapter(context, NewsToday.newsTextList!!)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL

        recyclerNews.adapter = newsAdapter
        recyclerNews.layoutManager = layoutManager
        recyclerNews.hasFixedSize()

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TopStoriesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}