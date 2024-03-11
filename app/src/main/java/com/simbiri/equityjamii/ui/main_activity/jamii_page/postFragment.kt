package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person

class postFragment : Fragment() {

    companion object {
        fun newInstance() = postFragment()
    }

    private lateinit var viewModel: PostViewModel
    private lateinit var currentUserImageView: ImageView
    private lateinit var yourThoughtsTv : TextView
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var personPost : Person

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.jamii_page, container, false)

        firestore.collection("Users").document(userId).get().addOnCompleteListener { taskDocSnapShot ->
            if (taskDocSnapShot.isSuccessful){
                if (taskDocSnapShot.result.exists()){
                    val profilePicUrl = taskDocSnapShot.result.getString("profileUri")
                    personPost = taskDocSnapShot.result.toObject(Person::class.java)!!
                    Glide.with(view.context).load(profilePicUrl).into(currentUserImageView)
                }
            }
        }

        currentUserImageView  = view.findViewById(R.id.currentUserImage)
        yourThoughtsTv = view.findViewById(R.id.yourThoughtsTv)


        currentUserImageView.setOnClickListener {
            val addPostFragment = AddPostFragment.newInstance(personPost)
            val transaction =  requireActivity().supportFragmentManager.beginTransaction()
            addPostFragment.show(transaction, addPostFragment.tag)
        }

        yourThoughtsTv.setOnClickListener {
            val addPostFragment = AddPostFragment.newInstance(personPost)
            val transaction =  requireActivity().supportFragmentManager.beginTransaction()
            addPostFragment.show(transaction, addPostFragment.tag)
        }



        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
