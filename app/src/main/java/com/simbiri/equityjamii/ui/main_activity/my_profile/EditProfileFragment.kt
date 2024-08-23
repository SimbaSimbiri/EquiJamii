package com.simbiri.equityjamii.ui.main_activity.my_profile

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Social
import com.simbiri.equityjamii.databinding.ProfilePageEditBinding


class EditProfileFragment : Fragment() {

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
    var clickedProfile = false
    var clickedBackG = false
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var person: Person
    private var profileNotComplete = false


    private val openLastPicker = registerForActivityResult(CropImageContract()) { result ->
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

        Handler().postDelayed({retreiveAllInfo()},700)

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
                    imageProfileUri == null ||
                    imageBackgUri == null

            if (profileNotComplete) {
                Toast.makeText(
                    requireContext(),
                    "To save changes, complete the Personal information block",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding!!.progressBar.isVisible = true
                uploadImagesAndSaveInfo()
            }
        }

        binding!!.exitButton.setOnClickListener {
            profileNotComplete = binding!!.nameProfileEdit.text.isNullOrEmpty() ||
                    binding!!.designationProfileEdit.text.isNullOrEmpty() ||
                    binding!!.branchProfileEdit.text.isNullOrEmpty() ||
                    binding!!.countryEmojiEditText.text.isNullOrEmpty() ||
                    binding!!.cityProfileEditText.text.isNullOrEmpty() ||
                    imageProfileUri == null ||
                    imageBackgUri == null

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

    private fun dismiss() {
        findNavController().navigateUp()
    }

    private fun uploadImagesAndSaveInfo() {
        val imageProfileReference = storageReference.child("Profile_pics").child("${AuthUtils.getCurrentUserId()!!}.jpg")
        val backGReference = storageReference.child("BackG_pics").child("${AuthUtils.getCurrentUserId()!!}.jpg")

        val uploadProfileImage = imageProfileUri?.scheme != "https"
        val uploadBackgroundImage = imageBackgUri?.scheme != "https"

        if (uploadProfileImage && imageProfileUri != null) {
            imageProfileReference.putFile(imageProfileUri!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imageProfileReference.downloadUrl.addOnSuccessListener { profileUri ->
                        person.profileUri = profileUri.toString()
                        Toast.makeText(
                            requireContext(),
                            "Uploaded new profile picture",
                            Toast.LENGTH_LONG
                        ).show()
                        if (uploadBackgroundImage && imageBackgUri != null) {
                            uploadBackgroundImageAndSave(backGReference)
                        } else {
                            savePersonalProfileInfo()
                        }
                    }
                } else {
                    showError(task.exception)
                }
            }
        } else if (uploadBackgroundImage && imageBackgUri != null) {
            uploadBackgroundImageAndSave(backGReference)
        } else {
            savePersonalProfileInfo()
        }
    }

    private fun uploadBackgroundImageAndSave(backGReference: StorageReference) {
        backGReference.putFile(imageBackgUri!!).addOnCompleteListener { bgTask ->
            if (bgTask.isSuccessful) {
                backGReference.downloadUrl.addOnSuccessListener { backGUri ->
                    person.backGUri = backGUri.toString()
                    Toast.makeText(
                        requireContext(),
                        "Uploaded new background image",
                        Toast.LENGTH_LONG
                    ).show()
                    savePersonalProfileInfo()
                }
            } else {
                showError(bgTask.exception)
            }
        }
    }


    private fun savePersonalProfileInfo() {
        Toast.makeText(
            requireContext(),
            "Uploading all profile changes",
            Toast.LENGTH_LONG
        ).show()

        val name = binding!!.nameProfileEdit.text!!.toString().trim()
        val designation = binding!!.designationProfileEdit.text!!.toString().trim()
        val branch = binding!!.branchProfileEdit.text!!.toString().trim()
        val city = binding!!.cityProfileEditText.text!!.toString().trim()
        val country = binding!!.countryEmojiEditText.text.toString().trim()
        val aboutMe = binding!!.aboutMeEdit.text!!.toString()
        val insta = binding!!.instaEdit.text!!.toString().trim()
        val faceb = binding!!.facebookEdit.text!!.toString().trim()
        val linkedIn = binding!!.linkedInEdit.text!!.toString().trim()
        val webS = binding!!.websiteEdit.text!!.toString().trim()
        val xAcc = binding!!.xEdit.text!!.toString().trim()

        val social = Social(aboutMe, linkedIn, insta, faceb, webS, xAcc)

        savePersonalToFireStore(
            name, designation, branch, person.profileUri,
            person.backGUri, city, country, social
        )
    }

    private fun savePersonalToFireStore(
        name: String,
        designation: String,
        branch: String,
        profileUri: String,
        imageBackgUri: String, city: String, country: String, social: Social
    ) {
        val mapToFirestore = HashMap<String, Any>().apply {
            put("userId", AuthUtils.getCurrentUserId()!!)
            put("name", name)
            put("designation", designation)
            put("branch", branch)
            put("profileUri", profileUri)
            put("backGUri", imageBackgUri)
            put("city", city)
            put("country", country)
            put(
                "social", hashMapOf(
                    "about" to social.about,
                    "insta" to social.insta,
                    "linkedin" to social.linkedin,
                    "faceb" to social.faceb,
                    "webs" to social.webs,
                    "xAcc" to social.xAcc
                )
            )
            put(
                "network", hashMapOf(
                    "followingList" to person.network.followingList,
                    "followerList" to person.network.followerList
                )
            )
            put("verified", person.verified)
            put("leader", person.leader)
            put("role", person.role)
            put("newsTags", person.newsTags)
            put("workspaces", person.workspaces)
        }

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
                    dismiss()
                } else {
                    showError(taskUpload.exception)
                }
            }
    }

    private fun showError(exception: Exception?) {
        binding!!.progressBar.isVisible = false
        Toast.makeText(
            requireContext(),
            exception?.message ?: "Error occurred",
            Toast.LENGTH_SHORT
        ).show()
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

/*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.profile_page_edit)
        dialog.setCanceledOnTouchOutside(true)
        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
                behavior.isHideable = true
                behavior.peekHeight = displayMetrics.heightPixels

            }
        }

        return dialog
    }
*/

    private fun showImagePicker() {
        val options = CropImageOptions(
            true,
            false,
            CropImageView.CropShape.RECTANGLE,
            cropCornerRadius = 8.0F,
            cropMenuCropButtonTitle = "Done",
            showCropLabel = true,
            activityBackgroundColor = resources.getColor(R.color.black),
            toolbarColor = resources.getColor(R.color.black),
            progressBarColor = resources.getColor(R.color.karbBackgrndtint),
            guidelines = CropImageView.Guidelines.OFF,
            fixAspectRatio = true
        )

        val cropOptions = if (clickedProfile) {
            CropImageContractOptions(
                null, options.copy(
                    activityTitle = "Profile crop",
                    aspectRatioX = 1,
                    aspectRatioY = 1
                )
            )
        } else {
            CropImageContractOptions(
                null, options.copy(
                    activityTitle = "Background crop",
                    aspectRatioX = 16,
                    aspectRatioY = 9
                )
            )
        }

        openLastPicker.launch(cropOptions)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}
