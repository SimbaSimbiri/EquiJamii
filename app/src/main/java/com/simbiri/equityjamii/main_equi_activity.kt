package com.simbiri.equityjamii

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class main_equi_activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navDrawer : NavigationView
    private lateinit var drawerLayoutMain : DrawerLayout
    private lateinit var navControllerMain: NavController
    private lateinit var toolbar: MaterialToolbar

    private var callBack : OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equi_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        drawerLayoutMain = findViewById(R.id.drawerLayout)
        //its easier to link a toolbar when it is not an @include in the layout
        toolbar =  findViewById(R.id.topappBar)
        navDrawer = findViewById(R.id.navigationView)

        //the navDrawer lines below allow items to be selected on the nav Drawer. We implement the interface onNavigation Item selected
        //and add bringtoFront() to make the items that are clicked focusable
        navDrawer.setNavigationItemSelectedListener(this)
        navDrawer.bringToFront()

        //getting the navHostFrag is by the use of supportFragmentManager, this allows you to use <fragmentContainerView in the layout activity
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //after setting it up with the manager, call its navController property which will handle navigation of destinations
        navControllerMain =navHostFrag.navController

        //link up of bottomNav view with your custom NavController and the naming of menu items and respective fragments with similar id's
        //allow for bottomNav View to take control of switching between the fragments
        bottomNavigationView.setupWithNavController(navControllerMain)

        // create a toggle that will expand the navigation drawer
        val toggle =  ActionBarDrawerToggle(this, drawerLayoutMain, toolbar, R.string.openDrawer, R.string.closeDrawer)
        toggle.drawerArrowDrawable.color = Color.WHITE
        drawerLayoutMain.addDrawerListener(toggle)
        toggle.syncState()
        //override call button so that exiting the nav drawer won't exit the main_equi_activity
        callBack = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                slideDrawerIn()
            }
        }
        onBackPressedDispatcher.addCallback(this, callBack!!)





        }



    fun slideDrawerIn(){
        if(drawerLayoutMain.isDrawerOpen(GravityCompat.START)){
            drawerLayoutMain.closeDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        //add when statement for click listeners for the  menu  items that will be clicked on the nav drawer

        when(item.itemId){


        }

        slideDrawerIn()
        return true   }

}