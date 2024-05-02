package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.databinding.DialogAddPostBinding
import com.simbiri.equityjamii.constants.FIREBASE_USER_ID
import com.simbiri.equityjamii.constants.POST_COLLECTION
import com.simbiri.equityjamii.constants.POST_STORAGE_REF

class AddPostFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_PERSON_POST = "Poster"
        fun newInstance(person: Person): AddPostFragment {
            val fragment = AddPostFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARGS_PERSON_POST, person)
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var viewModel: AddPostViewModel
    private var _binding: DialogAddPostBinding? = null
    private val binding get() = _binding
    private lateinit var openPostPicker: ActivityResultLauncher<CropImageContractOptions>
    private val firestoreInst: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null
    private val postStorageRef = FirebaseStorage.getInstance().getReference()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        openPostPicker = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                Glide.with(requireContext()).load(result.uriContent).into(binding!!.imagePostUpload)
                imageUri = result.uriContent
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddPostBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root


        val cropPostContractOptions = CropImageContractOptions(
            null, CropImageOptions(
                true,
                true,
                CropImageView.CropShape.RECTANGLE,
                cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done",
                showCropLabel = true,
                activityTitle = "CROP IMAGE",
                activityBackgroundColor = requireContext().resources.getColor(R.color.black),
                toolbarColor = requireContext().resources.getColor(R.color.black),
                progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF,
                fixAspectRatio = true
            )
        )

        binding!!.dismissFrag.setOnClickListener {
            dismiss()
        }
        binding!!.addPicturePost.setOnClickListener {
            openPostPicker.launch(cropPostContractOptions)
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding!!.postButton.setOnClickListener {
            val captionPost = binding!!.captionEditTv.text.toString()

            if (captionPost.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    " Caption must be added before posting to Jamii Feed",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (imageUri != null) {
                    savePostToFirestore(captionPost, imageUri.toString())
                } else {
                    savePostToFirestore(captionPost)
                }
            }
        }

        return view
    }


    private fun savePostToFirestore(captionPost: String, imageUri: String? = null) {
        val personPost = arguments?.getParcelable<Person>(ARGS_PERSON_POST)
        var postHashMap: HashMap<String, Any?> = HashMap()

        val postItemRef = postStorageRef.child(POST_STORAGE_REF)
            .child(FieldValue.serverTimestamp().toString() + ".jpg")

        if (imageUri != null) {
            postItemRef.putFile(Uri.parse(imageUri)).addOnCompleteListener { taskUpload ->
                if (taskUpload.isSuccessful) {
                    postItemRef.downloadUrl.addOnSuccessListener { postImageUri ->
                        postHashMap = hashMapOf(
                            "caption" to captionPost,
                            "image" to postImageUri,
                            "time" to FieldValue.serverTimestamp(),
                            "userId" to FIREBASE_USER_ID,
                            "likes" to 0,
                            "liked" to false,
                            "person" to personPost
                        )


                        firestoreInst.collection(POST_COLLECTION).add(postHashMap)
                            .addOnCompleteListener { taskDocref ->
                                if (taskDocref.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Successfully posted to feed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.i("PostHashMap", postHashMap.toString())

                                    dismiss()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error posting, try again later",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.i("PostHashMap Failed", postHashMap.toString())

                                }
                            }

                    }
                }
            }
        } else {
            postHashMap = hashMapOf(
                "caption" to captionPost,
                "image" to "",
                "time" to FieldValue.serverTimestamp(),
                "userId" to FIREBASE_USER_ID,
                "likes" to 0,
                "liked" to false,
                "person" to personPost
            )

            firestoreInst.collection(POST_COLLECTION).add(postHashMap)
                .addOnCompleteListener { taskDocref ->
                    if (taskDocref.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Successfully posted to feed",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("PostHashMap", postHashMap.toString())

                        dismiss()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error posting, try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("PostHashMap Failed", postHashMap.toString())

                    }
                }
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_add_post)
        dialog.setCanceledOnTouchOutside(false)


        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet!!.layoutParams.height = metrics.heightPixels
            bottomSheet.requestLayout()

            bottomSheet.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = metrics.heightPixels
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}