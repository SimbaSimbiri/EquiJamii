package com.simbiri.equityjamii

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar

class growthFragment : Fragment() {

    companion object {
        fun newInstance() = growthFragment()
    }

    private lateinit var viewModel: GrowthViewModel

    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var profileToSettings: ImageView
    private lateinit var toolbar: MaterialToolbar

    lateinit var textField3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_growth, container, false)

        appBarLayout = view.findViewById(R.id.appBarLayout)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        profileToSettings = collapsingToolbarLayout.findViewById(R.id.profileAndSettings)
        toolbar = view.findViewById(R.id.topAppBar)

        profileToSettings.setOnClickListener {
            requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout).openDrawer(GravityCompat.START)
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GrowthViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
