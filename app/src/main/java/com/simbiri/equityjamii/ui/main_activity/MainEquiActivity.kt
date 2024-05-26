package com.simbiri.equityjamii.ui.main_activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.ui.authentications.SignInActivity

class MainEquiActivity : AppCompatActivity()/*, NavigationView.OnNavigationItemSelectedListener*/,
    DrawerLayout.DrawerListener {

    private lateinit var fabWorkspace: FloatingActionButton
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
        setContentView(R.layout.activity_main)

        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()


        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            Toast.makeText(this, "Sign in First to access EquityJamii features", Toast.LENGTH_LONG)
                .show()
            startActivity(intent)
        }else{

            AuthUtils.getCurrentPerson { currentPerson ->
                if (currentPerson == null) {
                    navControllerMain.navigate(R.id.myProfile)
                    Toast.makeText(
                        this,
                        "Set up profile to access EquiJamii features",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        drawerLayoutMain = findViewById(R.id.drawerLayout)
        navDrawer = findViewById(R.id.navigationView)
        coordLayMain = findViewById(R.id.coordinatorLayoutMain)
        fabWorkspace = findViewById(R.id.workspaceFab)

        bottomNavigationView.background = null



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
                } else if (navControllerMain.currentDestination!!.id == R.id.newsFrag) {
                    finish()
                } else
                    navControllerMain.navigateUp()
            }
        }
        onBackPressedDispatcher.addCallback(this@MainEquiActivity, callBack!!)

        fabWorkspace.setOnClickListener {
            val workspaceMenuItem = bottomNavigationView.menu.getItem(2)
            bottomNavigationView.selectedItemId = workspaceMenuItem.itemId
        }


    }


    fun slideDrawerIn() {
        drawerLayoutMain.closeDrawer(GravityCompat.START)
    }


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



