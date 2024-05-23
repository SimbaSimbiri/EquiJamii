package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.constants.FIREBASE_USER_ID
import com.simbiri.equityjamii.data.model.Jamii
import com.simbiri.equityjamii.databinding.MyPostsTabBinding

class MyPostsFragment : Fragment() {

    companion object {
        fun newInstance() = MyPostsFragment()
    }

    private val viewModel: MyPostsViewModel by viewModels()
    private lateinit var binding: MyPostsTabBinding
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

        binding = MyPostsTabBinding.inflate(layoutInflater)
        val view = binding.root
        feedAdapter = PostAdapter(requireContext(), Jamii.myposts(FIREBASE_USER_ID))
        binding.myPostsRecyclerView.adapter = feedAdapter

        setUpPostRecycler()

        return view
    }


    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.myPostsRecyclerView.adapter!!.notifyDataSetChanged()
        binding.myPostsRecyclerView.layoutManager = layoutManager

    }


}