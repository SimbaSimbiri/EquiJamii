package com.simbiri.equityjamii.ui.authentications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.databinding.ActivitySignInBinding
import com.simbiri.equityjamii.ui.karibu_splash.KaribuActivity
import com.simbiri.equityjamii.ui.main_activity.MainEquiActivity

class SignInActivity : AppCompatActivity() {


    private lateinit var binding : ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.progressBar.isVisible = false

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        binding.toSignUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            binding.progressBar.isVisible = true

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, KaribuActivity::class.java)
                        startActivity(intent)
                        binding.progressBar.isVisible = false

                    } else {
                        Log.i("Error authentication", it.exception.toString())
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false

            }
        }


    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null){
            val intent =  Intent(this, MainEquiActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }


}