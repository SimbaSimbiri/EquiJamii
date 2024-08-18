package com.simbiri.equityjamii.ui.main_activity.jamii_page

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
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
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
import com.simbiri.equityjamii.constants.POST_COLLECTION
import com.simbiri.equityjamii.constants.POST_STORAGE_REF
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Post
import com.simbiri.equityjamii.databinding.DialogAddPostBinding

class AddPostFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_NEW_POST = "Posted post"
        fun newInstance(post: Post): AddPostFragment {
            val fragment = AddPostFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARGS_NEW_POST, post)
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
    private var currentPost: Post? = null

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
                false,
                CropImageView.CropShape.RECTANGLE,
                cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done",
                showCropLabel = true,
                activityTitle = "Crop post image",
                activityBackgroundColor = requireContext().resources.getColor(R.color.black),
                toolbarColor = requireContext().resources.getColor(R.color.black),
                progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF,
                aspectRatioX = 1,
                aspectRatioY = 1,
                fixAspectRatio = false
            )
        )

        binding!!.addPicturePost.setOnClickListener {
            openPostPicker.launch(cropPostContractOptions)
        }

        currentPost = arguments?.getParcelable<Post>(ARGS_NEW_POST)

        currentPost?.let { parceledPost ->
            Glide.with(requireContext()).load(parceledPost.image)
                .into(binding!!.imagePostUpload)
            binding!!.captionEditTv.setText(parceledPost.caption)
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

        binding!!.dismissFrag.setOnClickListener {
            dismiss()
        }
        return view
    }


    private fun savePostToFirestore(captionPost: String, imageUri: String? = null) {
        val postHashMap: HashMap<String, Any?> = HashMap()

        val postItemRef = postStorageRef.child(POST_STORAGE_REF)
            .child(FieldValue.serverTimestamp().toString() + ".jpg")

        if (imageUri != null) {
            postItemRef.putFile(Uri.parse(imageUri)).addOnCompleteListener { taskUpload ->
                if (taskUpload.isSuccessful) {

                    postItemRef.downloadUrl.addOnSuccessListener { postImageUri ->

                        if (currentPost != null) {

                            firestoreInst.collection(POST_COLLECTION)
                                .document(currentPost!!.documentId!!).update("caption", captionPost)
                            firestoreInst.collection(POST_COLLECTION)
                                .document(currentPost!!.documentId!!)
                                .update("image", postImageUri.toString())

                            Toast.makeText(
                                requireContext(),
                                "Changes will be updated soon",
                                Toast.LENGTH_SHORT
                            ).show()

                            dismiss()

                        } else {

                            postHashMap["caption"] = captionPost
                            postHashMap["image"] = postImageUri.toString()
                            postHashMap["time"] = FieldValue.serverTimestamp()
                            postHashMap["userId"] = AuthUtils.getCurrentUserId()!!
                            postHashMap["likes"] = 0
                            postHashMap["liked"] = false
                            postHashMap["documentId"] = null

                            firestoreInst.collection(POST_COLLECTION).add(postHashMap)
                                .addOnCompleteListener { taskDocref ->
                                    if (taskDocref.isSuccessful) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Successfully posted to feed",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        dismiss()
                                        val documentId = taskDocref.result.id
                                        updateDocWithId(documentId)
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
            }
        } else {
            if (currentPost != null) {

                firestoreInst.collection(POST_COLLECTION).document(currentPost!!.documentId!!)
                    .update("caption", captionPost)
                Toast.makeText(requireContext(), "Changes will be updated soon", Toast.LENGTH_SHORT)
                    .show()
                dismiss()

            } else {

                postHashMap["caption"] = captionPost
                postHashMap["image"] = ""
                postHashMap["time"] = FieldValue.serverTimestamp()
                postHashMap["userId"] = AuthUtils.getCurrentUserId()!!
                postHashMap["likes"] = 0
                postHashMap["liked"] = false
                postHashMap["documentId"] = null


                firestoreInst.collection(POST_COLLECTION).add(postHashMap)
                    .addOnCompleteListener { taskDocref ->
                        if (taskDocref.isSuccessful) {

                            Toast.makeText(
                                requireContext(),
                                "Successfully posted to feed",
                                Toast.LENGTH_SHORT
                            ).show()
                            dismiss()

                            val documentId = taskDocref.result.id
                            updateDocWithId(documentId)
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

    private fun updateDocWithId(documentId: String) {
        firestoreInst.collection(POST_COLLECTION).document(documentId)
            .update("documentId", documentId)
            .addOnCompleteListener { taskUpdate ->
                if (taskUpdate.isSuccessful) {
                    Log.i("DocumentID", "Document ID: $documentId")
                } else {
                    Log.e("DocumentUpdate", "Error updating document ID")
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