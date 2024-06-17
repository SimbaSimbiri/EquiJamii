package com.simbiri.equityjamii.ui.main_activity.news_page.official_coms

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OfficialAdapter
import com.simbiri.equityjamii.data.model.OfficialNewsTexts

class OfficialFragment : Fragment() {

    companion object {
        fun newInstance() = OfficialFragment()
    }

    private lateinit var recylerViewOfficial : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.news_page_official, container, false)

        recylerViewOfficial = view.findViewById(R.id.officialRecycler)
        setUpRecyclerOfficial()


        return view
    }


    private fun setUpRecyclerOfficial() {

        val officialAdapter = context?.let { OfficialAdapter(it, OfficialNewsTexts.officialNewsList!!) }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL

        recylerViewOfficial.adapter =  officialAdapter
        recylerViewOfficial.layoutManager = layoutManager
        recylerViewOfficial.hasFixedSize()
    }


}