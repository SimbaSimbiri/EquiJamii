package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class scholarsFragment : Fragment() {

    companion object {
        fun newInstance() = scholarsFragment()
    }

    private lateinit var viewModel: ScholarsViewModel

    lateinit var textField5: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_scholars, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScholarsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}