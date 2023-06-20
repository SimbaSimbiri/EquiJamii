package com.simbiri.equityjamii

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


//Understand -  ask the interviewer clarification, lowcase()
//Match -  what data structure am i dealing with
//Plan - plan your pseudocode, a high level overview of how you'll write code
//Implement - come up with the soln, code it
//Review -  review bugs, see if your code is clean, run a simple test. Try all  edge cases
//Evaluate -  space complexity/time complexity -- how much space your code takes


class MainEquiActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navDrawer: NavigationView
    private lateinit var drawerLayoutMain: DrawerLayout
    private lateinit var navControllerMain: NavController

    private lateinit var toolbar: MaterialToolbar


    private var callBack: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equi_main)

        bottomNavigationView = findViewById<BottomNavigationView?>(R.id.bottom_nav_view)
        drawerLayoutMain = findViewById(R.id.drawerLayout)
        //its easier to link a toolbar when it is not an @include in the layout
        toolbar = findViewById(R.id.topAppBar)
        navDrawer = findViewById(R.id.navigationView)

        //the navDrawer lines below allow items to be selected on the nav Drawer. We implement the interface onNavigation Item selected
        //and add bringtoFront() to make the items that are clicked focusable
        navDrawer.setNavigationItemSelectedListener(this)
        navDrawer.bringToFront()

        //getting the navHostFrag is by the use of supportFragmentManager, this allows you to use <fragmentContainerView in the layout activity
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //after setting it up with the manager, call its navController property which will handle navigation of destinations
        navControllerMain = navHostFrag.navController

        //link up of bottomNav view with your custom NavController and the naming of menu items and respective fragments with similar id's
        //allow for bottomNav View to take control of switching between the fragments
        bottomNavigationView.setupWithNavController(navControllerMain)
/*
        val navHostFragDrawer =  supportFragmentManager.findFragmentById(R.id.navHostFragCont2) as NavHostFragment
        navControllerDrawer = navHostFragDrawer.navController

        val appBarConfiguration = AppBarConfiguration(navControllerDrawer.graph, drawerLayoutMain)
        toolbar.setupWithNavController(navControllerDrawer,appBarConfiguration)
        navDrawer.setupWithNavController(navControllerDrawer)*/

        // create a toggle that will expand the navigation drawer
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayoutMain,
            toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        )
        toggle.drawerArrowDrawable.color = Color.WHITE
        drawerLayoutMain.addDrawerListener(toggle)
        toggle.syncState()

        //override call button so that exiting the nav drawer won't exit the MainEquiActivity
        callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                    slideDrawerIn()
                }
                else
                    finish()
            }
        }
        onBackPressedDispatcher.addCallback(this@MainEquiActivity, callBack!!)


    }


    fun slideDrawerIn() {

        drawerLayoutMain.closeDrawer(GravityCompat.START)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_menu, menu)

        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerMenu = navDrawer.menu
        //add when statement for click listeners for the  menu  items that will be clicked on the nav drawer

        when (item.itemId) {

            R.id.profileSettings -> {

                drawerMenu.findItem(R.id.communicate).isVisible = false
                drawerMenu.findItem(R.id.aboutUs).isVisible = false

                onClick(0)


                return true
            }
            R.id.settingsMenu -> {
                drawerMenu.findItem(R.id.communicate).isVisible = false
                drawerMenu.findItem(R.id.aboutUs).isVisible = false

                onClick(1)

                return true
            }
            R.id.aboutUs -> {
                onClick(2)

                return true
            }
            R.id.moreMenu -> {
                drawerMenu.findItem(R.id.communicate).isVisible = true
                drawerMenu.findItem(R.id.aboutUs).isVisible = true

                return true
            }

            R.id.communicate -> {
                onClick(3)
            }


        }


        return true
    }

    private fun onClick(numFrag: Int) {
        val intent = Intent(this, secondActivity::class.java)
        intent.putExtra(EXTRA_POS, numFrag)
        startActivity(intent)

    }

}


