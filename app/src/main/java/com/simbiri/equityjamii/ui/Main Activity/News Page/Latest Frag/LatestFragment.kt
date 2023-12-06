package com.simbiri.equityjamii.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseListOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.simbiri.equityjamii.R

class LatestFragment : Fragment() {

    companion object {
        fun newInstance() = LatestFragment()
    }

    private lateinit var viewModel: LatestViewModel
    private lateinit var recyclerNews: RecyclerView
    private var  firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseRecyclerAdapter: NewsAdapter
    private lateinit var options: FirebaseRecyclerOptions<NewsText>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       val  view =  inflater.inflate(R.layout.top_stories_fragment, container, false)

        recyclerNews = view.findViewById(R.id.newsRecylerView)
        databaseReference = firebaseDatabase.getReference("Latest")
        setUpRecyclerNews(view)

        YouTubeVids.YoutubeVideos(requireContext())

        return view
    }


    private fun setUpRecyclerNews(view: View?) {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        options = FirebaseRecyclerOptions.Builder<NewsText>().setQuery(databaseReference, NewsText::class.java).build()
         firebaseRecyclerAdapter = NewsAdapter(context, options)

        recyclerNews.adapter = firebaseRecyclerAdapter
        recyclerNews.layoutManager = layoutManager
        recyclerNews.hasFixedSize()

    }

    override fun onStart() {
        super.onStart()
        firebaseRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        firebaseRecyclerAdapter.stopListening()
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LatestViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
