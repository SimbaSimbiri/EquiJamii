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

class OfficialFragment : Fragment() {

    companion object {
        fun newInstance() = OfficialFragment()
    }

    private lateinit var viewModel: OfficialViewModel
    private lateinit var recylerViewOfficial : RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.official_fragment, container, false)

        recylerViewOfficial = view.findViewById(R.id.officialRecycler)
        setUpRecyclerOfficial(view)


        return view
    }


    private fun setUpRecyclerOfficial(view: View?) {
        val context = requireContext()
        val officialAdapter = OfficialAdapter(context, OfficialNewsTexts.officialNewsList!!)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL

        recylerViewOfficial.adapter =  officialAdapter
        recylerViewOfficial.layoutManager = layoutManager
        recylerViewOfficial.hasFixedSize()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OfficialViewModel::class.java)
        // TODO: Use the ViewModel
    }

}