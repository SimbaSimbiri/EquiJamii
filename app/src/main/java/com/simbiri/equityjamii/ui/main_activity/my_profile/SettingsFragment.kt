package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.databinding.SettingsFragBinding
import com.simbiri.equityjamii.ui.authentications.SignInActivity
import com.simbiri.equityjamii.ui.main_activity.news_page.for_you.EditNewsPrefFragment

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private var currentPerson: Person = Person()
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragBinding
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragBinding.inflate(layoutInflater)

        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()) { person ->
            if (person != null) {
                currentPerson = person
                binding.editProfile.setOnClickListener {
                    progressBarToggle()
                    val action = SettingsFragmentDirections.actionGlobalToProfileEdit(currentPerson)
                    findNavController().navigate(action)
                }

                binding.manageNewsPreferences.setOnClickListener {
                    progressBarToggle()
                    val editNews = EditNewsPrefFragment()
                    val transaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    editNews.show(transaction, editNews.tag)
                }

                binding.logout.setOnClickListener {
                    showSignOutConfirmationDialog()
                }
            }

        }




        return binding.root
    }

    private fun showSignOutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?\nYou will need to sign in again to use EquiJamii")
            .setPositiveButton("Log Out") { dialogInterface, _ ->
                signOutApp()
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun signOutApp() {
        firebaseAuth.signOut()
        val intent = Intent(requireActivity(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun progressBarToggle() {
        binding.contentLoadingProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.contentLoadingProgressBar.visibility = View.INVISIBLE
        }, 3000)
    }


}