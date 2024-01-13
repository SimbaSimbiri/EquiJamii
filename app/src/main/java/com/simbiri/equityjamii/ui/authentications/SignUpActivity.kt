package com.simbiri.equityjamii.ui.authentications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.simbiri.equityjamii.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.isVisible = false

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textToSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            binding.progressBar.isVisible = true

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            binding.progressBar.isVisible = false

                        } else {
                            Log.i("Error authentication", it.exception.toString())
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBar.isVisible = false

                        }

                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false

                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false


            }

        }
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }
}