package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class donateFragment : Fragment() {

    companion object {
        fun newInstance() = donateFragment()
    }

    private lateinit var viewModel: DonateViewModel

    lateinit var textField: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donate, container, false)

        textField = view?.findViewById(R.id.textDonate) as TextView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DonateViewModel::class.java)
        // TODO: Use the ViewModel
    }

}