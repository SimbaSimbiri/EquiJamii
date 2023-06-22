package com.simbiri.equityjamii

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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

class MainEquiActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navDrawer: NavigationView
    private lateinit var drawerLayoutMain: DrawerLayout
    private lateinit var navControllerMain: NavController
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var profileToSettings: ImageView

    private lateinit var toolbar: MaterialToolbar


    private var callBack: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equi_main)

        bottomNavigationView = findViewById<BottomNavigationView?>(R.id.bottom_nav_view)
        drawerLayoutMain = findViewById(R.id.drawerLayout)
        appBarLayout = findViewById(R.id.appBarLayout)
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsingToolbar)
        profileToSettings = collapsingToolbarLayout.findViewById(R.id.profileAndSettings)
        toolbar = findViewById(R.id.topAppBar)
        navDrawer = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)

        navDrawer.setNavigationItemSelectedListener(this)
        navDrawer.bringToFront()

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navControllerMain = navHostFrag.navController

        bottomNavigationView.setupWithNavController(navControllerMain)

        profileToSettings.setOnClickListener {
            drawerLayoutMain.openDrawer(GravityCompat.START)
        }

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

}


