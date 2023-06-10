package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class peopleFragment : Fragment() {

    companion object {
        fun newInstance() = peopleFragment()
    }

    private lateinit var viewModel: PeopleViewModel

    lateinit var textField2 : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
        textField2 = view?.findViewById(R.id.textEquityAfia) as TextView


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}