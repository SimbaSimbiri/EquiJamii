package com.simbiri.equityjamii

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class main_equi_activity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var fragmentContainerView: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equi_main)

        fragmentContainerView = findViewById(R.id.nav_host_fragment)
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView.setupWithNavController(navController)


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu, menu)

        return true
    }

    fun onClickEntreMenu(viewId:Int){

        when (viewId) {

            R.id.profile_menu_icon ->{}

            R.id.people_menu_icon ->{}


        }

    }

}