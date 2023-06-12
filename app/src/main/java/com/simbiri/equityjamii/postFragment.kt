package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class postFragment : Fragment() {

    companion object {
        fun newInstance() = postFragment()
    }

    private lateinit var viewModel: PostViewModel
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewPost =  inflater.inflate(R.layout.fragment_post, container, false)

        fabAdd = viewPost.findViewById(R.id.fabAddpost)


        return viewPost
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}