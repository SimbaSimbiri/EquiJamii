package com.simbiri.equityjamii

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainEquiActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    DrawerLayout.DrawerListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navDrawer: NavigationView
    private lateinit var drawerLayoutMain: DrawerLayout
    private lateinit var navControllerMain: NavController
    private lateinit var coordLayMain: CoordinatorLayout


    private var callBack: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equi_main)

        bottomNavigationView = findViewById<BottomNavigationView?>(R.id.bottom_nav_view)
        drawerLayoutMain = findViewById(R.id.drawerLayout)
       navDrawer = findViewById(R.id.navigationView)
        coordLayMain = findViewById(R.id.coordinatorLayoutMain)


        navDrawer.setNavigationItemSelectedListener(this)
        navDrawer.bringToFront()

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navControllerMain = navHostFrag.navController

        bottomNavigationView.setupWithNavController(navControllerMain)

        drawerLayoutMain.addDrawerListener(this)

        callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                    slideDrawerIn()

                } else
                    finish()
            }
        }
        onBackPressedDispatcher.addCallback(this@MainEquiActivity, callBack!!)


    }


    fun slideDrawerIn() {

        drawerLayoutMain.closeDrawer(GravityCompat.START)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerMenu = navDrawer.menu

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

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        coordLayMain.translationX = slideOffset * drawerLayoutMain.width/1.3f

    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
    }




}


