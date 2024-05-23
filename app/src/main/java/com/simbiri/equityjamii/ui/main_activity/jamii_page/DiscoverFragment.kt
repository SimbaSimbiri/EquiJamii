package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PostAdapter
import com.simbiri.equityjamii.data.model.Jamii

class DiscoverFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoverFragment()
    }

    private lateinit var viewModel: DiscoverViewModel
    private lateinit var recyclerPosts: RecyclerView
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var adapterPost: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.discover_tab, container, false)

        recyclerPosts = view.findViewById(R.id.discoverRecyclerView)
        adapterPost = PostAdapter(requireContext(), Jamii.discoverPosts())
        recyclerPosts.adapter = adapterPost
        setUpPostRecycler()

        return view
    }

    private fun setUpPostRecycler() {
        val context = requireContext()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerPosts.adapter!!.notifyDataSetChanged()
        recyclerPosts.layoutManager = layoutManager

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiscoverViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
