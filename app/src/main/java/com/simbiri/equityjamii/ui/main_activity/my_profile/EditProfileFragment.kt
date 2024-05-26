package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Social
import com.simbiri.equityjamii.databinding.ProfilePageEditBinding


class EditProfileFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_PERSON_INFO = "person"
        fun newInstance(person: Person): EditProfileFragment {
            val fragment = EditProfileFragment()
            val argumentBundle = Bundle()
            argumentBundle.putParcelable(ARGS_PERSON_INFO, person)
            fragment.arguments = argumentBundle
            return fragment
        }
    }

    private var _binding: ProfilePageEditBinding? = null
    private val binding get() = _binding

    private var imageProfileUri: Uri? = null
    private var imageBackgUri: Uri? = null
    var storagePerms: Array<String>? = null
    var clickedProfile = false
    var clickedBackG = false
    private lateinit var cropProfileContractOptions: CropImageContractOptions
    private lateinit var cropBackGContractOptions: CropImageContractOptions
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var person: Person
    private var profileNotComplete = false


    val openLastPicker = registerForActivityResult(CropImageContract()) { result ->

        if (result.isSuccessful) {
            if (clickedProfile) {
                imageProfileUri = Uri.parse(result.uriContent.toString())
                savePersonalProfileInfo()
            } else if (clickedBackG) {
                imageBackgUri = result.uriContent
                savePersonalProfileInfo()
            }
        }

    }


    private lateinit var viewModel: EditProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ProfilePageEditBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root
        person = arguments?.getParcelable<Person>(ARGS_PERSON_INFO)!!

        adjustSize()
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        storagePerms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        retreiveAllInfo()

        binding!!.profileEditCard.setOnClickListener {
            clickedProfile = true
            clickedBackG = false
            showImagePicker()

        }

        binding!!.backGEditCard.setOnClickListener {
            clickedBackG = true
            clickedProfile = false
            showImagePicker()

        }

        binding!!.saveProfileButton.setOnClickListener {

            profileNotComplete = binding!!.nameProfileEdit.text.isNullOrEmpty() ||
                    binding!!.designationProfileEdit.text.isNullOrEmpty() ||
                    binding!!.branchProfileEdit.text.isNullOrEmpty() ||
                    binding!!.countryEmojiEditText.text.isNullOrEmpty() ||
                    binding!!.cityProfileEditText.text.isNullOrEmpty() ||
                    imageProfileUri?.toString().isNullOrEmpty() ||
                    imageBackgUri?.toString().isNullOrEmpty()

            if (profileNotComplete) {
                Toast.makeText(
                    requireContext(),
                    "To save changes, complete the Personal information block",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                binding!!.progressBar.isVisible = true
                savePersonalProfileInfo()
            }
        }


        binding!!.exitButton.setOnClickListener {
            profileNotComplete = binding!!.nameProfileEdit.text.isNullOrEmpty() ||
                    binding!!.designationProfileEdit.text.isNullOrEmpty() ||
                    binding!!.branchProfileEdit.text.isNullOrEmpty() ||
                    binding!!.countryEmojiEditText.text.isNullOrEmpty() ||
                    binding!!.cityProfileEditText.text.isNullOrEmpty() ||
                    imageProfileUri?.toString().isNullOrEmpty() ||
                    imageBackgUri?.toString().isNullOrEmpty()

            if (profileNotComplete) {
                Toast.makeText(
                    requireContext(),
                    "Personal information block and user images must be uploaded",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                dismiss()
            }
        }


        return view
    }


    private fun retreiveAllInfo() {

        person.let {

            imageProfileUri = Uri.parse(person.profileUri)
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
            binding!!.xEdit.setText(person.social.xAcc)

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

        val name = binding!!.nameProfileEdit.text!!.toString()
        val designation = binding!!.designationProfileEdit.text!!.toString()
        val branch = binding!!.branchProfileEdit.text!!.toString()
        val city = binding!!.cityProfileEditText.text!!.toString()
        val country = binding!!.countryEmojiEditText.text.toString()
        val imageProfileReference =
            storageReference.child("Profile_pics").child("${AuthUtils.getCurrentUserId()!!}.jpg")
        val backGReference =
            storageReference.child("BackG_pics").child("${AuthUtils.getCurrentUserId()!!}.jpg")

        val aboutMe = binding!!.aboutMeEdit.text!!.toString()
        val insta = binding!!.instaEdit.text!!.toString()
        val faceb = binding!!.facebookEdit.text!!.toString()
        val linkedIn = binding!!.linkedInEdit.text!!.toString()
        val webS = binding!!.websiteEdit.text!!.toString()
        val xAcc = binding!!.xEdit.text!!.toString()

        val social = Social(aboutMe, linkedIn, insta, faceb, webS, xAcc)

        if (clickedProfile) {
            if (imageProfileUri != null || name.isEmpty() || designation.isEmpty() || branch.isEmpty()) {
                //upload image profile uri only
                imageProfileReference.putFile(imageProfileUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageProfileReference.downloadUrl.addOnSuccessListener { profileUri ->
                            savePersonalToFireStore(
                                name,
                                designation,
                                branch,
                                profileUri.toString(),
                                person.backGUri,
                                city,
                                country, social
                            )

                            person.profileUri = profileUri.toString()
                            Glide.with(this).load(person.profileUri).into(binding!!.profileImage)
                        }

                        Toast.makeText(
                            requireContext(),
                            "Image uri uploaded to database",
                            Toast.LENGTH_SHORT
                        )
                            .show()

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

            clickedProfile = false

        } else if (clickedBackG) {
            if (imageBackgUri != null || name.isEmpty() || designation.isEmpty() || branch.isEmpty()) {
                //upload image profile uri only
                backGReference.putFile(imageBackgUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        backGReference.downloadUrl.addOnSuccessListener { backGUri ->
                            savePersonalToFireStore(
                                name,
                                designation,
                                branch,
                                person.profileUri,
                                backGUri.toString(),
                                city,
                                country, social
                            )

                            person.backGUri = backGUri.toString()
                            Glide.with(this).load(person.backGUri).into(binding!!.imageBackGround)
                        }

                        Toast.makeText(
                            requireContext(),
                            "Background uri uploaded to database",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
            clickedBackG = false
        } else {
            savePersonalToFireStore(
                name,
                designation,
                branch,
                person.profileUri,
                person.backGUri,
                city,
                country, social
            )

        }

        clickedProfile = false
        clickedBackG = false

    }

    private fun savePersonalToFireStore(
        name: String,
        designation: String,
        branch: String,
        profileUri: String,
        imageBackgUri: String, city: String, country: String, social: Social
    ) {

        val mapToFirestore = HashMap<String, Any>()
        mapToFirestore["userId"] = AuthUtils.getCurrentUserId()!!
        mapToFirestore["name"] = name
        mapToFirestore["designation"] = designation
        mapToFirestore["branch"] = branch
        mapToFirestore["profileUri"] = profileUri
        mapToFirestore["backGUri"] = imageBackgUri
        mapToFirestore["city"] = city
        mapToFirestore["country"] = country
        mapToFirestore["social"] = hashMapOf(
            "about" to social.about,
            "insta" to social.insta,
            "linkedin" to social.linkedin,
            "faceb" to social.faceb,
            "webs" to social.webs,
            "xAcc" to social.xAcc
        )
        mapToFirestore["network"] = hashMapOf(
            "followingList" to person.network.followingList,
            "followerList" to person.network.followerList
        )
        mapToFirestore["verified"] = person.verified
        mapToFirestore["leader"] = person.leader


        firestore.collection(USERS_COLLECTION).document(AuthUtils.getCurrentUserId()!!)
            .set(mapToFirestore)
            .addOnCompleteListener { taskUpload ->
                if (taskUpload.isSuccessful) {
                    binding!!.progressBar.isVisible = false

                    Toast.makeText(
                        requireContext(),
                        "Profile information updated",
                        Toast.LENGTH_LONG
                    ).show()

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.profile_page_display)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = 1000
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
        }

        return dialog
    }

    private fun showImagePicker() {

        if (clickedProfile) {

            cropProfileContractOptions = CropImageContractOptions(
                null, CropImageOptions(
                    true,
                    false,
                    CropImageView.CropShape.RECTANGLE,
                    cropCornerRadius = 8.0F,
                    cropMenuCropButtonTitle = "Done",
                    showCropLabel = true,
                    activityTitle = "Profile crop",
                    activityBackgroundColor = this.resources.getColor(R.color.black),
                    toolbarColor = this.resources.getColor(R.color.black),
                    progressBarColor = this.resources.getColor(R.color.karbBackgrndtint),
                    guidelines = CropImageView.Guidelines.OFF,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    fixAspectRatio = true

                )
            )
        }

        cropBackGContractOptions = CropImageContractOptions(
            null, CropImageOptions(
                true,
                false,
                CropImageView.CropShape.RECTANGLE,
                cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done",
                showCropLabel = true,
                activityTitle = "Background crop",
                activityBackgroundColor = this.resources.getColor(R.color.black),
                toolbarColor = this.resources.getColor(R.color.black),
                progressBarColor = this.resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF,
                aspectRatioX = 16,
                aspectRatioY = 9,
                fixAspectRatio = true
            )
        )

        if (clickedProfile) {
            openLastPicker.launch(cropProfileContractOptions)

        } else if (clickedBackG) {
            openLastPicker.launch(cropBackGContractOptions)

        }


    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
