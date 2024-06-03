package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.SocialAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.constants.USER_ID
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.AuthUtils.getCurrentUserId
import com.simbiri.equityjamii.data.model.Network
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.ProfilePageDisplayBinding
import com.simbiri.equityjamii.ui.authentications.SignInActivity

class ProfDisplayFragment : Fragment() {

    companion object {
        fun newInstance() = ProfDisplayFragment()
    }

    private var currentPerson: Person = Person()
    private val viewModel: ProfDisplayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: ProfilePageDisplayBinding
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listsSocials: ArrayList<String>
    private var myNetwork: Network? = Network()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ProfilePageDisplayBinding.inflate(layoutInflater)
        val view = binding.root

        adjustSize()

        val gestureDetectorCompat = GestureDetectorCompat(requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    Toast.makeText(
                        requireContext(),
                        "${binding.nameOnPeople.text} signed out\nCome back soon!",
                        Toast.LENGTH_LONG
                    ).show()
                    signOutApp()
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    Toast.makeText(
                        requireContext(), "Double tap to confirm sign out", Toast.LENGTH_LONG
                    ).show()
                    return true
                }
            })

        binding.logout.setOnTouchListener { view, event ->
            view.performClick()
            gestureDetectorCompat.onTouchEvent(event)
        }



        binding.myJamiiTv.setOnClickListener {
        requireActivity().supportFragmentManager.popBackStackImmediate()
            val action = ProfDisplayFragmentDirections.actionOpenJamii(2)
            findNavController().navigate(action)

        }

        binding.myAssistant.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
            val action = ProfDisplayFragmentDirections.actionOpenWorkspace()
            findNavController().navigate(action)

        }

        binding.myWorkspaces.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
            val action = ProfDisplayFragmentDirections.actionOpenWorkspace()
            findNavController().navigate(action)

        }

        binding.editProfileTv.setOnClickListener {
            binding.contentLoadingProgressBar.visibility = View.VISIBLE
            val editProfileFragment = EditProfileFragment.newInstance(currentPerson)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            editProfileFragment.show(transaction, editProfileFragment.tag)
            Handler().postDelayed({
                binding.contentLoadingProgressBar.visibility = View.INVISIBLE
            }, 2500)

        }

        binding.cardPeople.setOnClickListener {
            binding.contentLoadingProgressBar.visibility = View.VISIBLE
            val networkDialogFrag = NetworkFragment.newInstance(myNetwork)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            networkDialogFrag.show(transaction, networkDialogFrag.tag)
            Handler().postDelayed({
                binding.contentLoadingProgressBar.visibility = View.INVISIBLE
            }, 2500)

        }

        return view
    }

    private fun signOutApp() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireActivity(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun retreiveDisplayInfo(firebaseUserId: String) {
        firestore.collection(USERS_COLLECTION).document(firebaseUserId).get()
            .addOnCompleteListener { snapShotRetreiveTask ->

                if (snapShotRetreiveTask.isSuccessful) {
                    if (snapShotRetreiveTask.result.exists()) {
                        val myProfile = snapShotRetreiveTask.result.toObject(Person::class.java)!!

                        myProfile.let { myProf ->
                            binding.let {
                                Glide.with(requireContext()).load(Uri.parse(myProf.backGUri))
                                    .into(it.backImageView)
                                Glide.with(requireContext()).load(Uri.parse(myProf.profileUri))
                                    .into(it.profileImageView)
                                it.nameOnPeople.text = myProf.name
                                it.designationOnPeople.text =
                                    myProf.designation + " at " + myProf.branch
                                it.aboutTextContent.text = myProf.social.about
                                it.textCounty.text = myProf.city
                                it.countryEmojiText.text = myProf.country
                                listsSocials = arrayListOf(
                                    myProf.social.linkedin,
                                    myProf.social.insta,
                                    myProf.social.webs,
                                    myProf.social.faceb,
                                    myProf.social.xAcc
                                )

                                if (myProf.verified) {
                                    binding.verifiedPersonelImage.visibility = View.VISIBLE
                                }

                                listsSocials.shuffle()
                                setRecyclerViewSocials()
                                currentPerson = myProf
                            }


                            myNetwork = myProf.network
                        }
                    }
                }
            }

    }


    private fun setRecyclerViewSocials() {
        val context = requireContext()
        val filtered = listsSocials.filter { it != "" }
        val socialAdapter = SocialAdapter(context, filtered)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        binding.recyclerSocials.adapter = socialAdapter
        binding.recyclerSocials.layoutManager = layoutManager
        binding.recyclerSocials.hasFixedSize()

    }

    private fun adjustSize() {


        val layoutParamsProfileCardOut = binding.materialCardView.layoutParams
        val layoutParamsBackG = binding.backImageView.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParamsProfileCardOut.width = screenWidth / 3
        layoutParamsProfileCardOut.height = screenWidth / 3

        layoutParamsBackG.height = screenWidth / 3 + 100
        layoutParamsBackG.width = screenWidth


        binding.materialCardView.layoutParams = layoutParamsProfileCardOut
        binding.backImageView.layoutParams = layoutParamsBackG
    }

    override fun onResume() {
        super.onResume()

        AuthUtils.getCurrentPerson(USER_ID) { currPerson ->
            if (currPerson == null) {
                val newPerson = Person()
                newPerson.userId = getCurrentUserId()!!
                val editProfileFragment = EditProfileFragment.newInstance(newPerson)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                editProfileFragment.show(transaction, editProfileFragment.tag)

            } else {
                retreiveDisplayInfo(getCurrentUserId()!!)
            }
        }

    }
}