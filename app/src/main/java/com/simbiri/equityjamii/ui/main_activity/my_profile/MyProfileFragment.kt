package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.simbiri.equityjamii.R
import androidx.core.view.isVisible
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Social
import com.simbiri.equityjamii.databinding.ProfilePageBinding


class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private var _binding: ProfilePageBinding? = null
    private val binding get() = _binding

    private var imageProfileUri: Uri? = null
    private var imageBackgUri: Uri? = null
    var storagePerms: Array<String>? = null
    var clickedProfile = false
    var clickedBackG = false
    var firebaseAuth = FirebaseAuth.getInstance()
    var userId = firebaseAuth.currentUser!!.uid
    private lateinit var cropProfileContractOptions: CropImageContractOptions
    private lateinit var cropBackGContractOptions: CropImageContractOptions
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    val openLastPicker = registerForActivityResult(CropImageContract()) { result ->

        if (result.isSuccessful) {
            if (clickedProfile) {
                imageProfileUri = result.uriContent
                Glide.with(this).load(imageProfileUri).into(binding!!.profileImage)
            } else if (clickedBackG) {
                imageBackgUri = result.uriContent
                Glide.with(this).load(imageBackgUri).into(binding!!.imageBackGround)
            }
        }

    }


    private lateinit var viewModel: MyProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ProfilePageBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root


        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        adjustSize()

        binding!!.progressBar.isVisible = true
        storagePerms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        retreiveAllInfo()

        binding!!.profileImage.setOnClickListener {
            clickedProfile = true
            clickedBackG = false
            showImagePicker()

        }

        binding!!.imageBackGround.setOnClickListener {
            clickedBackG = true
            clickedProfile = false
            showImagePicker()

        }

        binding!!.saveProfileButton.setOnClickListener {
            savePersonalProfileInfo()
        }


        return view
    }


    private fun retreiveAllInfo() {
        firestore.collection("Users").document(userId).get()
            .addOnCompleteListener { taskDocSnapShot ->
                if (taskDocSnapShot.isSuccessful) {
                    if (taskDocSnapShot.result.exists()) {



                        val person =  taskDocSnapShot.result.toObject(Person::class.java)


                        person.let {

                            imageProfileUri = Uri.parse(person!!.profileUri)
                            imageBackgUri = Uri.parse(person.backGUri)
                            binding!!.nameProfileEdit.setText(person.name)
                            binding!!.designationProfileEdit.setText(person.designation)
                            binding!!.branchProfileEdit.setText(person.branch)
                            binding!!.cityProfileEditText.setText(person.city)
                            binding!!.countryEmojiEditText.setText(person.country)

                            binding!!.aboutMeEdit.setText(person.social.about)
                            binding!!.linkedInEdit.setText(person.social.linkedin)
                            binding!!.instaEdit.setText(person.social.insta)
                            binding!!.facebookEdit.setText(person.social.faceb)
                            binding!!.websiteEdit.setText(person.social.webs)

                            if (person.profileUri != "null") {
                                Glide.with(this).load(Uri.parse(person.profileUri))
                                    .into(binding!!.profileImage)
                            }
                            if (person.backGUri != "null") {
                                Glide.with(this).load(Uri.parse(person.backGUri))
                                    .into(binding!!.imageBackGround)
                            }

                        }

                        }


                } else {
                    Toast.makeText(
                        requireContext(),
                        "Profile data unchanged from app restart",
                        Toast.LENGTH_LONG
                    ).show()
                }

                binding!!.progressBar.isVisible = false

            }
    }

    private fun adjustSize() {


        val layoutParamsProfileCardOut = binding!!.materialCardView.layoutParams
        val layoutParamsProfileCardIn = binding!!.materialCardViewIn.layoutParams
        val layoutParamsBackG = binding!!.imageBackGround.layoutParams

        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        layoutParamsProfileCardOut.width = screenWidth / 3 + 80
        layoutParamsProfileCardIn.width = screenWidth / 3 - 10 + 80

        layoutParamsProfileCardOut.height = screenWidth / 3 + 80
        layoutParamsProfileCardIn.height = screenWidth / 3 - 10 + 80
        layoutParamsBackG.height = screenWidth / 3 + 100
        layoutParamsBackG.width = screenWidth


        binding!!.materialCardView.layoutParams = layoutParamsProfileCardOut
        binding!!.materialCardViewIn.layoutParams = layoutParamsProfileCardIn
        binding!!.imageBackGround.layoutParams = layoutParamsBackG
    }

    private fun savePersonalProfileInfo() {
        binding!!.progressBar.isVisible = true

        val name = binding!!.nameProfileEdit.text!!.toString()
        val designation = binding!!.designationProfileEdit.text!!.toString()
        val branch = binding!!.branchProfileEdit.text!!.toString()
        val city = binding!!.cityProfileEditText.text!!.toString()
        val country = binding!!.countryEmojiEditText.text.toString()
        val imageProfileReference = storageReference.child("Profile_pics").child("$userId.jpg")
        val backGReference = storageReference.child("BackG_pics").child("$userId.jpg")

        val aboutMe = binding!!.aboutMeEdit.text!!.toString()
        val insta = binding!!.instaEdit.text!!.toString()
        val faceb = binding!!.facebookEdit.text!!.toString()
        val linkedIn = binding!!.linkedInEdit.text!!.toString()
        val webS =  binding!!.websiteEdit.text!!.toString()

        val social =  Social(aboutMe, linkedIn,insta,faceb, webS)

        if (clickedProfile && !clickedBackG) {
            if (imageProfileUri != null || !name.isNotEmpty() || !designation.isNotEmpty() || !branch.isNotEmpty()) {
                //upload image profile uri only
                imageProfileReference.putFile(imageProfileUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageProfileReference.downloadUrl.addOnSuccessListener { profileUri ->
                            savePersonalToFireStore(
                                name,
                                designation,
                                branch,
                                profileUri,
                                imageBackgUri,
                                city,
                                country,social
                            )
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            } else {
                binding!!.progressBar.isVisible = false

            }
        } else if (clickedBackG && !clickedProfile) {
            if (imageBackgUri != null || !name.isNotEmpty() || !designation.isNotEmpty() || !branch.isNotEmpty()) {
                //upload image profile uri only
                backGReference.putFile(imageBackgUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        backGReference.downloadUrl.addOnSuccessListener { backGUri ->
                            savePersonalToFireStore(
                                name,
                                designation,
                                branch,
                                imageProfileUri,
                                backGUri,
                                city,
                                country,social
                            )

                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            } else {
                binding!!.progressBar.isVisible = false

            }
        } else {
            savePersonalToFireStore(
                name,
                designation,
                branch,
                imageProfileUri,
                imageBackgUri,
                city,
                country,social
            )

        }



    }

    private fun savePersonalToFireStore(
        name: String,
        designation: String,
        branch: String,
        profileUri: Uri?,
        imageBackgUri: Uri?, city: String, country: String, social: Social
    ) {

        val mapToFirestore = HashMap<String, Any>()
        mapToFirestore["name"] = name
        mapToFirestore["designation"] = designation
        mapToFirestore["branch"] = branch
        mapToFirestore["profileUri"] = profileUri.toString()
        mapToFirestore["backGUri"] = imageBackgUri.toString()
        mapToFirestore["city"] = city
        mapToFirestore["country"] = country
        mapToFirestore["social"] = hashMapOf( "about" to social.about, "insta" to  social.insta, "linkedin" to social.linkedin, "faceb" to social.faceb,"webs" to social.webs )


        firestore.collection("Users").document(userId).set(mapToFirestore)
            .addOnCompleteListener { taskUpload ->
                if (taskUpload.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.progressBar.isVisible = false

                } else {
                    Toast.makeText(
                        requireContext(),
                        taskUpload.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("Error saving to firestore", taskUpload.exception.toString())
                }
            }


    }

/*    private fun saveSocialToFireStore(
        about: String,
        linkedIn: String,
        insta: String,
        faceb: String,
        webS: String
    ) {
        val mapSocialToFirestore = HashMap<String, Any>()
        mapSocialToFirestore["about"] = about
        mapSocialToFirestore["linkedIn"] = linkedIn
        mapSocialToFirestore["insta"] = insta
        mapSocialToFirestore["faceb"] = faceb
        mapSocialToFirestore["webS"] = webS


        firestore.collection("Users").document(userId).set(mapSocialToFirestore)
            .addOnCompleteListener { taskUpload ->
                if (taskUpload.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Social  Info updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding!!.progressBar.isVisible = false

                } else {
                    Toast.makeText(
                        requireContext(),
                        taskUpload.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("Error saving to firestore", taskUpload.exception.toString())
                }
            }


    }*/

    fun showImagePicker() {

        val profileHeight = requireActivity().windowManager.defaultDisplay.height
        val backGroundHeight = binding!!.imageBackGround.height

        if (clickedProfile) {

            cropProfileContractOptions = CropImageContractOptions(
                null, CropImageOptions(
                    true,
                    true,
                    CropImageView.CropShape.OVAL,
                    cropCornerRadius = 8.0F,
                    cropMenuCropButtonTitle = "Done",
                    showCropLabel = true,
                    activityTitle = "CROP IMAGE",
                    activityBackgroundColor = this.resources.getColor(R.color.black),
                    toolbarColor = this.resources.getColor(R.color.black),
                    progressBarColor = this.resources.getColor(R.color.karbBackgrndtint),
                    guidelines = CropImageView.Guidelines.OFF,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                )
            )
        }

        cropBackGContractOptions = CropImageContractOptions(
            null, CropImageOptions(
                true,
                true,
                CropImageView.CropShape.RECTANGLE,
                cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done",
                showCropLabel = true,
                activityTitle = "CROP IMAGE",
                activityBackgroundColor = this.resources.getColor(R.color.black),
                toolbarColor = this.resources.getColor(R.color.black),
                progressBarColor = this.resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF,
                fixAspectRatio = true
            )
        )

        if (!checkStoragePermission()) {
            requestStoragePermission()

        } else {
            if (clickedProfile) {
                openLastPicker.launch(cropBackGContractOptions)

            } else if (clickedBackG) {
                openLastPicker.launch(cropBackGContractOptions)
            }

        }


    }


    private fun requestStoragePermission() {
        requestPermissions(storagePerms!!, 200)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200) {
            if (grantResults.isNotEmpty()) {
                val writeStorageIsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (writeStorageIsAccepted) {
                    /*if (clickedProfile) {
                        openLastPicker.launch(cropProfileContractOptions)
                    } else if (clickedBackG) {
                        openLastPicker.launch(cropBackGContractOptions)
                    }*/
                    openLastPicker.launch(cropBackGContractOptions)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enable access to Gallery",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
