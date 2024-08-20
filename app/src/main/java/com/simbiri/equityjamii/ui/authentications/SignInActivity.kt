package com.simbiri.equityjamii.ui.authentications

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.databinding.ActivitySignInBinding
import com.simbiri.equityjamii.ui.karibu_splash.KaribuActivity
import com.simbiri.equityjamii.ui.main_activity.MainEquiActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class SignInActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var oneTapClient: SignInClient
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult())
        { result ->
            if (result.resultCode == RESULT_OK) {
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    signInWithIntent(result.data ?: return@launch)
                }
            } else if (result.resultCode == RESULT_CANCELED){
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        oneTapClient = Identity.getSignInClient(this)
        binding.progressBar.isVisible = false

        this.auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.toSignUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPassText.setOnClickListener {
            startActivity(Intent(this, ForgetPassActivity::class.java))
        }


        binding.signInGoogleButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            lifecycleScope.launch {
                val signInIntentSender = signIn()
                resultLauncher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            binding.progressBar.visibility = View.VISIBLE

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Enter valid email!!", Toast.LENGTH_SHORT).show()
                } else {

                    this.auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, KaribuActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Sign In successful.", Toast.LENGTH_LONG).show()
                            binding.progressBar.visibility = View.INVISIBLE

                        } else {
                            Toast.makeText(
                                this,
                                "Wrong password, try resetting or signing in via your google account",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.progressBar.visibility = View.INVISIBLE

                        }
                    }

                }
            } else {
                Toast.makeText(
                    this, "Empty Fields Are not Allowed !!",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar.visibility = View.INVISIBLE

            }
        }

    }

    private fun signInWithIntent(intent: Intent) {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val idToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(googleCredentials).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Sign In successful.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, KaribuActivity::class.java))
                binding.progressBar.visibility = View.INVISIBLE
                finish()
            } else {
                Toast.makeText(this, "Sign In Failed, try again.", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()

        } catch (e: Exception) {
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(this, "OneTap Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()

            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }

        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getString(R.string.default_web_client_id)).build()
            ).setAutoSelectEnabled(false).build()

    }

    override fun onStart() {
        super.onStart()

        if (this.auth.currentUser != null) {
            val intent = Intent(this, MainEquiActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (auth.currentUser != null) finish()
    }

}
