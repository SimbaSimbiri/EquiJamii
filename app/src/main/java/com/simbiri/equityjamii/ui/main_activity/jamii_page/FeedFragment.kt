package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.Jamii
import com.simbiri.equityjamii.databinding.FeedTabBinding

class FeedFragment : Fragment() {

    companion object {
        fun newInstance() = FeedFragment()
    }

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var binding: FeedTabBinding
    private lateinit var feedAdapter: PostAdapter
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedTabBinding.inflate(layoutInflater)
        val view = binding.root
        feedAdapter = PostAdapter(requireContext(), Jamii.feedPosts())
        binding.feedRecyclerView.adapter = feedAdapter

        setUpPostRecycler()

        return view
    }

    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.feedRecyclerView.layoutManager = layoutManager
        binding.feedRecyclerView.adapter!!.notifyDataSetChanged()

    }

}