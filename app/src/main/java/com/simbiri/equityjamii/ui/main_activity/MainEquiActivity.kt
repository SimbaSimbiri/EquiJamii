package com.simbiri.equityjamii.ui.main_activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.ui.authentications.SignInActivity

class MainEquiActivity : AppCompatActivity() {

    private lateinit var fabJamii: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navControllerMain: NavController
    private lateinit var coordLayMain: CoordinatorLayout

    private var callBack: OnBackPressedCallback? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val firestore = FirebaseFirestore.getInstance()

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        firestore.firestoreSettings = settings
    }

    private fun updateBottomNavSelection() {
        when (navControllerMain.currentDestination?.id) {
            R.id.newsFrag -> bottomNavigationView.menu.findItem(R.id.newsFrag).isChecked = true
            R.id.jamiiFrag -> bottomNavigationView.menu.findItem(R.id.jamiiFrag).isChecked = true
            R.id.peopleFrag -> bottomNavigationView.menu.findItem(R.id.peopleFrag).isChecked = true
            R.id.myProfile -> bottomNavigationView.menu.findItem(R.id.myProfile).isChecked = true
            R.id.myWorkspace -> bottomNavigationView.menu.findItem(R.id.myWorkspace).isChecked =
                true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseAuth = FirebaseAuth.getInstance()


        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        coordLayMain = findViewById(R.id.coordinatorLayoutMain)
        fabJamii = findViewById(R.id.jamiiFab)



        bottomNavigationView.background = null

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navControllerMain = navHostFrag.navController

        bottomNavigationView.setupWithNavController(navControllerMain)
        navControllerMain.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.jamiiFrag -> {
                    fabJamii.background = ContextCompat.getDrawable(this, R.color.logoColour)
                    fabJamii.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.logoColour)
                    fabJamii.foregroundTintList =
                        ContextCompat.getColorStateList(this, R.color.logoColour)
                }

                else -> {
                    fabJamii.background = ContextCompat.getDrawable(
                        this,
                        com.denzcoskun.imageslider.R.color.grey_font
                    )

                    fabJamii.backgroundTintList =
                        ContextCompat.getColorStateList(
                            this,
                            com.denzcoskun.imageslider.R.color.grey_font
                        )
                    fabJamii.foregroundTintList =
                        ContextCompat.getColorStateList(
                            this,
                            com.denzcoskun.imageslider.R.color.grey_font
                        )
                }
            }
            updateBottomNavSelection()
        }


        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            Toast.makeText(this, "Sign in First to access EquityJamii features", Toast.LENGTH_LONG)
                .show()
            startActivity(intent)
            finish()
        } else {

            AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
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

        callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = supportFragmentManager

                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStackImmediate()
                    updateBottomNavSelection()
                } else if (navControllerMain.currentDestination?.id == R.id.newsFrag) {
                    finish()
                } else {
                    navControllerMain.navigateUp()
                    updateBottomNavSelection()

                }
            }
        }
        onBackPressedDispatcher.addCallback(this@MainEquiActivity, callBack!!)

/*        callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navControllerMain.currentDestination?.id == R.id.newsFrag) {
                    finish() // Exit the app if on the news fragment
                } else {
                    // Handle back navigation and update bottom navigation selection
                    if (!navControllerMain.popBackStack()) {
                        finish() // Close the activity if there's nothing in the back stack
                    } else {
                        updateBottomNavSelection()
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this@MainEquiActivity, callBack!!)*/

        fabJamii.setOnClickListener {
            navigateJamii()
        }


    }

    private fun navigateJamii() {
        bottomNavigationView.selectedItemId = bottomNavigationView.menu.getItem(2).itemId
        fabJamii.background =
            ContextCompat.getDrawable(this@MainEquiActivity, R.color.logoColour)
    }
}
