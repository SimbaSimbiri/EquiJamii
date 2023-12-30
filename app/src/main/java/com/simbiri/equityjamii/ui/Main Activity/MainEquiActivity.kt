package com.simbiri.equityjamii.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R

class MainEquiActivity : AppCompatActivity()/*, NavigationView.OnNavigationItemSelectedListener*/,
    DrawerLayout.DrawerListener {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    var firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navDrawer: NavigationView
    private lateinit var drawerLayoutMain: DrawerLayout
    private lateinit var navControllerMain: NavController
    private lateinit var coordLayMain: CoordinatorLayout


    private var callBack: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equi_main)


        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            Toast.makeText(this, "Sign in First to access EquityJamii features", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        drawerLayoutMain = findViewById(R.id.drawerLayout)
       navDrawer = findViewById(R.id.navigationView)
        coordLayMain = findViewById(R.id.coordinatorLayoutMain)

        val signOutTextView = findViewById<TextView>(R.id.signOut)
        val imageSignOut =  findViewById<ImageView>(R.id.imageViewSignOut)
        signOutTextView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish() }

        imageSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
            finish() }



/*
        navDrawer.setNavigationItemSelectedListener(this)
*/
        navDrawer.bringToFront()

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navControllerMain = navHostFrag.navController
        navControllerMain.enableOnBackPressed(true)


        bottomNavigationView.setupWithNavController(navControllerMain)

        drawerLayoutMain.addDrawerListener(this)
        YouTubeVids.YoutubeVideos(this)

        callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (drawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                    slideDrawerIn()
                }
                else if (navControllerMain.currentDestination!!.id == R.id.newsFrag){
                    finish()
                }
                else
                    navControllerMain.navigateUp()
            }
        }
        onBackPressedDispatcher.addCallback(this@MainEquiActivity, callBack!!)




    }


    fun slideDrawerIn() {
        drawerLayoutMain.closeDrawer(GravityCompat.START)
    }


    /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerMenu = navDrawer.menu

        when (item.itemId) {

            R.id.profileSettings -> {
                onClick()
                return true
            }
            R.id.settingsMenu -> {
                onClick()
                return true
            }

            R.id.communicate -> {
                onClick()
                return true
            }
            R.id.aboutUs -> {
                onClick()
                return true
            }

        }
        return true
    }*/

    /*private fun onClick() {


    }*/

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

       //coordLayMain.translationX = slideOffset * drawerLayoutMain.width/1.3f

    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
    }




}



