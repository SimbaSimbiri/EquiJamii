package com.simbiri.equityjamii.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.simbiri.equityjamii.R

class postFragment : Fragment() {

    companion object {
        fun newInstance() = postFragment()
    }

    private lateinit var viewModel: PostViewModel
/*
    private lateinit var fabAdd: FloatingActionButton
*/
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_post, container, false)

        appBarLayout = view.findViewById(R.id.appBarLayout)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        toolbar = view.findViewById(R.id.topAppBar)
/*
        fabAdd = view.findViewById(R.id.fabAddpost)
*/

        val drawerLayout =  requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout) //for you to instantiate  a drawer layout you have to use the activity's context since


        val toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout,toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
