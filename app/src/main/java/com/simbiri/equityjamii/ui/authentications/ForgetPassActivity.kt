package com.simbiri.equityjamii.ui.authentications

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.simbiri.equityjamii.databinding.ActivityForgetPassBinding

class ForgetPassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPassBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.btnForgotPasswordSubmit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            resetPassword()
        }

        binding.tvSubmitMsg.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun resetPassword() {
        val email = binding.emailEt.text.toString().trim()
        if (email.isNotEmpty()) {

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter valid email!!", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        binding.textView3.text = "A link has been sent to your mailbox, try signing in again"
                        binding.progressBar.visibility = View.INVISIBLE

                    } else {
                        Toast.makeText(this,
                            "Reset password failed, ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.INVISIBLE

                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }

}