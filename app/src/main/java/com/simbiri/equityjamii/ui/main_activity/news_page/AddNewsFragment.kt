package com.simbiri.equityjamii.ui.main_activity.news_page

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.ImageDescAdapter
import com.simbiri.equityjamii.data.model.ImageDesc
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.databinding.AddNewsDialogBinding

class AddNewsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddNewsDialogBinding
    private var newsText: NewsText? = null
    private lateinit var openImagePicker: ActivityResultLauncher<CropImageContractOptions>
    private val firestoreInst: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().getReference()
    private val imageDescList = mutableListOf<ImageDesc>()
    private lateinit var imageDescAdapter: ImageDescAdapter

    companion object {
        private const val ARG_NEWS_TEXT = "news_text"

        fun newInstance(newsText: NewsText?): AddNewsFragment {
            val fragment = AddNewsFragment()
            val args = Bundle()
            args.putParcelable(ARG_NEWS_TEXT, newsText)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            newsText = it.getParcelable(ARG_NEWS_TEXT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddNewsDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()

        openImagePicker = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { uri ->
                    val imageDesc = ImageDesc(uri.toString(), "")
                    imageDescAdapter.addImageDesc(imageDesc)
                }
            }
        }

        binding.addPicturePost.setOnClickListener {
            if (imageDescList.size < 5) {
                openImagePicker.launch(
                    CropImageContractOptions(
                        null,
                        CropImageOptions(
                            cropCornerRadius = 8.0F,
                            cropMenuCropButtonTitle = "Done",
                            showCropLabel = true,
                            activityTitle = "Crop news image",
                            activityBackgroundColor = requireContext().resources.getColor(R.color.black),
                            toolbarColor = requireContext().resources.getColor(R.color.black),
                            progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
                            guidelines = CropImageView.Guidelines.OFF,
                            fixAspectRatio = false,
                            imageSourceIncludeCamera = false
                        )
                    )
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "You can only add up to 5 images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (newsText != null) {
            populateFields(newsText!!)
        }

        binding.postButton.setOnClickListener {
            saveNews()
        }

        binding.dismissFrag.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun setupRecyclerView() {
        imageDescAdapter = ImageDescAdapter(requireContext(), imageDescList)
        binding.recyclerViewImages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = imageDescAdapter
        }
    }

    private fun populateFields(newsText: NewsText) {
        binding.editTextTitle.setText(newsText.title)
        binding.editTextAllNews.setText(newsText.allNews)
        binding.editTextAuthor.setText(newsText.author)
        binding.editTextNewsName.setText(newsText.newsName)
        binding.editTextNewsTag.setText(newsText.newsTag)

        imageDescList.clear()
        imageDescList.addAll(newsText.imageDescList)
        imageDescAdapter.notifyDataSetChanged()
    }

    private fun saveNews() {

        val title = binding.editTextTitle.text.toString().trim()
        val allNews = binding.editTextAllNews.text.toString().trim()
        val author = binding.editTextAuthor.text.toString().trim()
        val newsName = binding.editTextNewsName.text.toString().trim()
        val newsTag = binding.editTextNewsTag.text.toString().trim()

        if (title.isEmpty() || allNews.isEmpty() || author.isEmpty() || newsName.isEmpty() || newsTag.isEmpty()) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        for (i in 0 until imageDescList.size) {
            val viewHolder = binding.recyclerViewImages.findViewHolderForAdapterPosition(i) as? ImageDescAdapter.ImageDescViewHolder
            viewHolder?.let {
                imageDescList[i] = ImageDesc(imageDescList[i].image, it.editTextDescription.text.toString())
            }
        }

        if (newsText == null) {
            newsText = NewsText(imageDescList, title, allNews, author, newsName, newsTag)
        } else {
            newsText!!.apply {
                this.imageDescList.clear()
                this.imageDescList.addAll(imageDescList)
                this.title = title
                this.allNews = allNews
                this.author = author
                this.newsName = newsName
                this.newsTag = newsTag
            }
        }

        uploadImagesAndSaveNews(newsText!!)
    }

    private fun uploadImagesAndSaveNews(newsText: NewsText) {
        val newsCollection = firestoreInst.collection("News")
        val storageRef = FirebaseStorage.getInstance().reference

        val imageDescList = newsText.imageDescList
        val uploadTasks = mutableListOf<Task<Uri>>()

        for (i in 0 until imageDescList.size) {
            val imageDesc = imageDescList[i]
            val imageUri = Uri.parse(imageDesc.image)
            val imageRef = storageRef.child("news_images/${imageUri.lastPathSegment}_${System.currentTimeMillis()}.jpg")

            val uploadTask = imageRef.putFile(imageUri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }

            uploadTasks.add(uploadTask)
        }

        Tasks.whenAllComplete(uploadTasks).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (i in 0 until task.result.size) {
                    val result = task.result[i]
                    if (result.isSuccessful) {
                        val downloadUri = result.result as Uri
                        imageDescList[i] = ImageDesc(downloadUri.toString(), imageDescList[i].description)
                    } else {
                        Toast.makeText(requireContext(), "Error uploading image: ${result.exception?.message}", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }

                if (newsText.documentId.isEmpty()) {
                    newsCollection.add(newsText)
                        .addOnSuccessListener { documentReference ->
                            val documentId = documentReference.id
                            newsCollection.document(documentId)
                                .update("documentId", documentId)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "News successfully added", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Error updating document ID", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error adding news", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    newsCollection.document(newsText.documentId)
                        .set(newsText)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "News successfully updated", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error updating news", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Error uploading images", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.add_news_dialog)
        dialog.setCanceledOnTouchOutside(false)

        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = metrics.heightPixels
                it.requestLayout()

                val behavior = BottomSheetBehavior.from(it)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight = metrics.heightPixels
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }
}
