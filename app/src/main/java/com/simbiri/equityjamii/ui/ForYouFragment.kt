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

class ForYouFragment : Fragment() {

    companion object {
        fun newInstance() = ForYouFragment()
    }

/*
    private lateinit var image_Slider: ImageSlider
*/

    private lateinit var elpRecyclerView: RecyclerView
    private lateinit var inKenyaRecyclerView: RecyclerView
    private lateinit var inDRCRecyclerView: RecyclerView
    private lateinit var inUgandaRecyclerView: RecyclerView
    private lateinit var inEquityAfyaRecyclerView: RecyclerView


    private val viewModel by lazy {  ViewModelProvider(this).get(ForYouViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.for_you_fragment, container, false)

        elpRecyclerView =view.findViewById(R.id.elpRecyclerView)
        inKenyaRecyclerView = view.findViewById(R.id.inKenyaRecyclerView)
        inUgandaRecyclerView =view.findViewById(R.id.inUgandaRecyclerView)
        inDRCRecyclerView = view.findViewById(R.id.inDRCRecylerView)
        inEquityAfyaRecyclerView = view.findViewById(R.id.equityAfyaRecyclerView)


        setUpRecyclerNews(view)

        return view

    }



    private fun setUpRecyclerNews(view: View?) {
        val context = requireContext()
        val newsForYouAdapter =  FypNewsAdapter(context, NewsToday.newsTextList!!)

        val layoutManagerELP = LinearLayoutManager(context)
        layoutManagerELP.orientation = RecyclerView.VERTICAL
        val layoutManagerKenya = LinearLayoutManager(context)
        layoutManagerKenya.orientation = RecyclerView.VERTICAL
        val layoutManagerUganda = LinearLayoutManager(context)
        layoutManagerUganda.orientation = RecyclerView.VERTICAL
        val layoutManagerDRC = LinearLayoutManager(context)
        layoutManagerDRC.orientation = RecyclerView.VERTICAL
        val layoutManagerEquityAfya = LinearLayoutManager(context)
        layoutManagerEquityAfya.orientation = RecyclerView.VERTICAL

        elpRecyclerView.adapter = newsForYouAdapter
        inKenyaRecyclerView.adapter = newsForYouAdapter
        inUgandaRecyclerView.adapter = newsForYouAdapter
        inDRCRecyclerView.adapter = newsForYouAdapter
        inEquityAfyaRecyclerView.adapter = newsForYouAdapter


        elpRecyclerView.layoutManager = layoutManagerELP
        inKenyaRecyclerView.layoutManager = layoutManagerKenya
        inUgandaRecyclerView.layoutManager = layoutManagerUganda
        inDRCRecyclerView.layoutManager = layoutManagerDRC
        inEquityAfyaRecyclerView.layoutManager = layoutManagerEquityAfya

    }



}

