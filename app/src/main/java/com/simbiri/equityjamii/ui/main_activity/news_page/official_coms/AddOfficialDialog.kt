package com.simbiri.equityjamii.ui.main_activity.news_page.official_coms

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.NEWS_COLLECTION
import com.simbiri.equityjamii.constants.NEWS_PDF_STORE
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.databinding.DialogAddOfficialBinding

class AddOfficialDialog : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_NEWS_TEXT = "news_text"

        fun newInstance(newsText: NewsText?): AddOfficialDialog {
            val fragment = AddOfficialDialog()
            val args = Bundle()
            args.putParcelable(ARG_NEWS_TEXT, newsText)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var pdfLauncher: ActivityResultLauncher<String>
    private lateinit var binding: DialogAddOfficialBinding
    private val fileTitleList = mutableListOf<FileTitle>()
    private val firestoreInst: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var pdfDescAdapter: PdfDescAdapter
    private val newsStorageRef = FirebaseStorage.getInstance().reference
    private var newsText: NewsText? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        binding = DialogAddOfficialBinding.inflate(layoutInflater)
        setUpRecyclers(context)

        pdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val filename = uri?.let { DocumentFile.fromSingleUri(context, it)?.name }

            if (filename != null) {
                val fileTitle = FileTitle(uri.toString(), filename,0)
                fileTitleList.add(fileTitle)
                pdfDescAdapter.notifyDataSetChanged()
            }

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

        binding.apply {
            addPdfButton.setOnClickListener {
                pdfLauncher.launch("application/pdf")
            }

            if (newsText != null) {
                populateFields(newsText!!)
            }

            postButton.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Initiating uploading official communication",
                    Toast.LENGTH_LONG
                ).show()
                saveNews()
            }

            dismissFrag.setOnClickListener {
                dismiss()
            }
        }
        return binding.root
    }

    private fun populateFields(newsText: NewsText) {
        binding.editTextTitle.setText(newsText.title)
        binding.editTextCommSummary.setText(newsText.allNews)
        binding.editTextDepartment.setText(newsText.author)

        fileTitleList.addAll(newsText.fileTitleList)
        pdfDescAdapter.notifyDataSetChanged()
    }

    private fun saveNews() {

        val title = binding.editTextTitle.text.toString().trim()
        val allNews = binding.editTextCommSummary.text.toString().trim()
        val author = binding.editTextDepartment.text.toString().trim()
        val newsName = ""
        val newsTag = "Official"

        if (title.isEmpty() || allNews.isEmpty() || author.isEmpty()) {
            Toast.makeText(requireContext(), "All fields must be filled", Toast.LENGTH_LONG).show()
            return
        }


        val uploadPdfList = mutableListOf<FileTitle>()


        for (i in 0 until fileTitleList.size) {
            val viewHolder =
                binding.recyclerPdfs.findViewHolderForAdapterPosition(i) as? PdfDescAdapter.PdfDescViewHolder
            viewHolder?.let {
                uploadPdfList.add(
                    FileTitle(fileTitleList[i].fileUri, it.textPdfName.text.toString(), i+1)
                )
            }
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

    private fun saveNewsToFireStore(newsText: NewsText) {
        val imageUploadsCount = newsText.fileTitleList.size
        var uploadCounter = 0
        val uploadedFileTitleList = mutableListOf<FileTitle>()

        Toast.makeText(
            requireContext(),
            "Uploading documents to cloud",
            Toast.LENGTH_LONG
        ).show()

        binding.progressSaveNews.visibility = View.VISIBLE


        for (i in 0 until pdfDescAdapter.itemCount) {
            val curPdfDesc = newsText.fileTitleList[i]

            if (curPdfDesc.fileUri.startsWith("https://")) {
                uploadedFileTitleList.add(
                    FileTitle(
                        curPdfDesc.fileUri,
                        curPdfDesc.fileTitle, i+1
                    )
                )

                uploadCounter++

                if (uploadCounter == imageUploadsCount) {
                    saveOrUpdate(newsText, uploadedFileTitleList)
                }

                Toast.makeText(
                    requireContext(),
                    "Uploaded file ${uploadCounter} out of ${pdfDescAdapter.itemCount}",
                    Toast.LENGTH_LONG
                ).show()

                continue
            }

            val newsItemRef = newsStorageRef.child(NEWS_PDF_STORE)
                .child(FieldValue.serverTimestamp().toString() + "file${i + 1}.pdf")

            if (curPdfDesc.fileUri.isNotEmpty()) {
                newsItemRef.putFile(Uri.parse(curPdfDesc.fileUri))
                    .addOnCompleteListener { taskUpload ->

                        if (taskUpload.isSuccessful) {

                            newsItemRef.downloadUrl.addOnSuccessListener { newsImageUri ->
                                uploadedFileTitleList.add(
                                    FileTitle(
                                        newsImageUri.toString(),
                                        curPdfDesc.fileTitle, i+1
                                    )
                                )
                                uploadCounter++

                                if (uploadCounter == imageUploadsCount) {
                                    saveOrUpdate(newsText, uploadedFileTitleList)
                                }
                                Toast.makeText(
                                    requireContext(),
                                    "Uploaded file ${uploadCounter} out of ${pdfDescAdapter.itemCount}",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } else {

                            Toast.makeText(
                                requireContext(),
                                "${taskUpload.exception?.message}}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
            }

        }


    }

    private fun saveOrUpdate(newsText: NewsText, pdfList: MutableList<FileTitle>) {
        val newsHashMap: HashMap<String, Any?> = HashMap()
        newsHashMap["allNews"] = newsText.allNews
        newsHashMap["author"] = newsText.author
        newsHashMap["fileTitleList"] =
            pdfList.map { hashMapOf("fileUri" to it.fileUri, "fileTitle" to it.fileTitle, "position" to it.position) }
        newsHashMap["newsName"] = newsText.newsName
        newsHashMap["newsTag"] = newsText.newsTag
        newsHashMap["title"] = newsText.title
        newsHashMap["documentId"] = newsText.documentId
        newsHashMap["time"] = newsText.time

        if (!newsText.documentId.isNullOrEmpty()) {

            firestoreInst.collection(NEWS_COLLECTION).document(newsText.documentId!!)
                .update(newsHashMap).addOnCompleteListener { taskUpdate ->
                    if (taskUpdate.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "News changes will be published soon",
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


    private fun setUpRecyclers(context: Context) {
        binding.recyclerPdfs.apply {
            pdfDescAdapter = PdfDescAdapter(context, fileTitleList, true)
            layoutManager = LinearLayoutManager(context)
            adapter = pdfDescAdapter
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_add_official)
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
                behavior.isDraggable = false
                behavior.isHideable = true
                behavior.peekHeight = metrics.heightPixels
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }
}