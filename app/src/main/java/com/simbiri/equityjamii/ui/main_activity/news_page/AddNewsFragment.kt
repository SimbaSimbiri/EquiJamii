package com.simbiri.equityjamii.ui.main_activity.news_page

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.ImageDescAdapter
import com.simbiri.equityjamii.constants.NEWS_COLLECTION
import com.simbiri.equityjamii.constants.NEWS_STORAGE_REF
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.databinding.AddNewsDialogBinding

class AddNewsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AddNewsDialogBinding
    private var newsText: NewsText? = null
    private lateinit var openImagePicker: ActivityResultLauncher<CropImageContractOptions>
    private val firestoreInst: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val fileTitleList = mutableListOf<FileTitle>()
    private lateinit var imageDescAdapter: ImageDescAdapter
    private val newsStorageRef = FirebaseStorage.getInstance().reference
    private var featuringFile: FileTitle? = null

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
    ): View {
        binding = AddNewsDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        setupRecyclerView()

        openImagePicker = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { uri ->
                    val fileTitle = FileTitle(uri.toString(), "")
                    fileTitleList.add(fileTitle)
                    imageDescAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.addPicturePost.setOnClickListener {
            if (fileTitleList.size < 5) {
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
                            aspectRatioY = 9,
                            aspectRatioX = 16,
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
            Toast.makeText(requireContext(), "Initiating publishing news", Toast.LENGTH_LONG).show()
            saveNews()
        }

        binding.dismissFrag.setOnClickListener {
            dismiss()
        }

        binding.editTextNewsName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.toString().equals("featuring", ignoreCase = true)) {
                    binding.addFeaturingLink.visibility = View.VISIBLE
                } else {
                    binding.addFeaturingLink.visibility = View.GONE
                    binding.newsLinkLayout.visibility = View.GONE
                    binding.editTextNewsLink.visibility = View.GONE
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().equals("featuring", ignoreCase = true)) {
                    binding.addFeaturingLink.visibility = View.VISIBLE
                } else {
                    binding.addFeaturingLink.visibility = View.GONE
                    binding.newsLinkLayout.visibility = View.GONE
                    binding.editTextNewsLink.visibility = View.GONE
                }
            }
        })

        binding.addFeaturingLink.setOnClickListener {
            binding.newsLinkLayout.visibility = View.VISIBLE
            binding.editTextNewsLink.visibility = View.VISIBLE
        }

        return view
    }

    private fun setupRecyclerView() {
        imageDescAdapter = ImageDescAdapter(requireContext(), fileTitleList)
        binding.recyclerViewImages.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = imageDescAdapter
        }

    }

    private fun populateFields(newsText: NewsText) {
        binding.editTextTitle.setText(newsText.title)
        binding.editTextAllNews.setText(newsText.allNews)
        binding.editTextAuthor.setText(newsText.author)
        binding.editTextNewsName.setText(newsText.newsName)
        binding.editTextNewsTag.setText(newsText.newsTag)

        val isYouTubeFile = { curFile: FileTitle ->
            curFile.fileTitle.contentEquals(
                "youTube",
                true
            )
        }

        val hasYouTubeVid: Boolean = newsText.fileTitleList.any { isYouTubeFile(it) }

        if (hasYouTubeVid) {

            if (newsText.newsName.contentEquals("featuring", true)) {
                binding.newsLinkLayout.visibility = View.VISIBLE
                binding.editTextNewsLink.visibility = View.VISIBLE
                binding.addFeaturingLink.visibility = View.VISIBLE
            }

            val toAdapter =
                newsText.fileTitleList.filter { fileTitle: FileTitle -> !isYouTubeFile(fileTitle) }

            val toFeaturingLink = newsText.fileTitleList.first(isYouTubeFile)
            featuringFile = toFeaturingLink

            binding.editTextNewsLink.setText("https://www.youtube.com/watch?v=${toFeaturingLink.fileUri}")
            fileTitleList.addAll(toAdapter)
        } else {
            fileTitleList.addAll(newsText.fileTitleList)
        }

        imageDescAdapter.notifyDataSetChanged()
    }

    private fun saveNews() {

        val title = binding.editTextTitle.text.toString().trim()
        val allNews = binding.editTextAllNews.text.toString().trim()
        val author = binding.editTextAuthor.text.toString().trim()
        val newsName = binding.editTextNewsName.text.toString().trim()
        val newsTag = binding.editTextNewsTag.text.toString().trim()
        val featuringUri = binding.editTextNewsLink.text.toString().trim()

        if (title.isEmpty() || allNews.isEmpty() || author.isEmpty() || newsName.isEmpty() || newsTag.isEmpty()) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_LONG).show()
            return
        }

        val uploadImageList = mutableListOf<FileTitle>()


        for (i in 0 until fileTitleList.size) {
            val viewHolder =
                binding.recyclerViewImages.findViewHolderForAdapterPosition(i) as? ImageDescAdapter.ImageDescViewHolder
            viewHolder?.let {
                uploadImageList.add(
                    FileTitle(fileTitleList[i].fileUri, it.editTextDescription.text.toString())
                )
            }
        }

        if (featuringUri.contains("https://")) {

            val featuringFileTitle =
                extractYouTubeVideoId(featuringUri)?.let { FileTitle(it, "youTube") }
                featuringFile = featuringFileTitle
        }

        val timestamp = Timestamp.now()
        if (newsText == null) {
            newsText = NewsText(fileTitleList, title, allNews, author, newsName, newsTag, timestamp)
        } else {
            newsText!!.let { news ->
                news.fileTitleList = fileTitleList
                news.title = title
                news.allNews = allNews
                news.author = author
                news.newsName = newsName
                news.newsTag = newsTag
                news.time = news.time
            }
        }

        saveNewsToFireStore(newsText!!)
    }

    fun extractYouTubeVideoId(youtubeUrl: String): String? {
        val pattern = Regex(
            pattern = "(?:youtube\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})"
        )
        return pattern.find(youtubeUrl)?.groupValues?.get(1)
    }

    private fun saveNewsToFireStore(newsText: NewsText) {
        val imageUploadsCount = newsText.fileTitleList.size
        var uploadCounter = 0
        val uploadedFileTitleList = mutableListOf<FileTitle>()

        Toast.makeText(
            requireContext(),
            "Uploading images to cloud",
            Toast.LENGTH_LONG
        ).show()

        binding.progressSaveNews.visibility = View.VISIBLE

        for (i in 0 until imageDescAdapter.itemCount) {
            val curImageDesc = newsText.fileTitleList[i]

            if (curImageDesc.fileUri.startsWith("https://")) {
                uploadedFileTitleList.add(
                    FileTitle(
                        curImageDesc.fileUri,
                        curImageDesc.fileTitle
                    )
                )

                uploadCounter++

                if (uploadCounter == imageUploadsCount) {
                    if (featuringFile != null) {
                        uploadedFileTitleList.add(featuringFile!!)
                    }
                    saveOrUpdate(newsText, uploadedFileTitleList)
                }

                continue
            }

            val newsItemRef = newsStorageRef.child(NEWS_STORAGE_REF)
                .child(FieldValue.serverTimestamp().toString() + "image${i + 1}.jpg")

            if (curImageDesc.fileUri.isNotEmpty()) {
                newsItemRef.putFile(Uri.parse(curImageDesc.fileUri))
                    .addOnCompleteListener { taskUpload ->

                        if (taskUpload.isSuccessful) {

                            newsItemRef.downloadUrl.addOnSuccessListener { newsImageUri ->
                                uploadedFileTitleList.add(
                                    FileTitle(
                                        newsImageUri.toString(),
                                        curImageDesc.fileTitle
                                    )
                                )
                                uploadCounter++

                                if (uploadCounter == imageUploadsCount) {
                                    if (featuringFile != null) {
                                        uploadedFileTitleList.add(featuringFile!!)
                                    }
                                    saveOrUpdate(newsText, uploadedFileTitleList)
                                }

                            }

                        } else {

                            Toast.makeText(
                                requireContext(),
                                "${taskUpload.exception?.message}}",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    }
            }

        }


    }

    private fun saveOrUpdate(newsText: NewsText, imageList: MutableList<FileTitle>) {
        val newsHashMap: HashMap<String, Any?> = HashMap()
        newsHashMap["allNews"] = newsText.allNews
        newsHashMap["author"] = newsText.author
        newsHashMap["fileTitleList"] =
            imageList.map { hashMapOf("fileUri" to it.fileUri, "fileTitle" to it.fileTitle) }
        newsHashMap["newsName"] = newsText.newsName
        newsHashMap["newsTag"] = newsText.newsTag
        newsHashMap["title"] = newsText.title
        newsHashMap["documentId"] = newsText.documentId
        newsHashMap["time"] = newsText.time
        binding.progressSaveNews.visibility = View.VISIBLE

        if (!newsText.documentId.isNullOrEmpty()) {

            firestoreInst.collection(NEWS_COLLECTION).document(newsText.documentId!!)
                .update(newsHashMap).addOnCompleteListener { taskUpdate ->
                    if (taskUpdate.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Updated changes will be published soon",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Couldn't publish changes, try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.progressSaveNews.visibility = View.INVISIBLE
                    dismiss()
                }

        } else {
            firestoreInst.collection(NEWS_COLLECTION).add(newsHashMap)
                .addOnCompleteListener { taskDocRef ->
                    if (taskDocRef.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Successfully published news",
                            Toast.LENGTH_SHORT
                        ).show()

                        val docId = taskDocRef.result.id
                        updateDocWithId(docId)
                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Error publishing news, try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dismiss()
                    binding.progressSaveNews.visibility = View.INVISIBLE

                }
        }


    }

    private fun updateDocWithId(docId: String) {
        firestoreInst.collection(NEWS_COLLECTION).document(docId).update("documentId", docId)
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
