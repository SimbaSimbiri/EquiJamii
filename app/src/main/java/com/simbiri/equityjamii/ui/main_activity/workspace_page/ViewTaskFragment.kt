package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.MilestoneAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_DOCUMENTS_STORE
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.MileStone
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.data.objects.UserNetworkUtils
import com.simbiri.equityjamii.databinding.ViewTaskFragBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.DialogDocumentsFragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class ViewTaskFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARGS_TASK_ITEM = "taskItem"
        private const val ARGS_WORKSP_ID = "workspId"

        fun newInstance(taskItem: Task, workspId: String): ViewTaskFragment {
            val frag = ViewTaskFragment()
            val args = Bundle()
            args.putParcelable(ARGS_TASK_ITEM, taskItem)
            args.putString(ARGS_WORKSP_ID, workspId)
            frag.arguments = args

            return frag
        }
    }

    private val viewModel: ViewTaskViewModel by viewModels()
    private lateinit var binding: ViewTaskFragBinding
    private lateinit var pdfAdapter: PdfDescAdapter
    private val pdfFiles = mutableListOf<FileTitle>()
    private var curTask: Task? = null
    private var workspaceId: String? = null
    private lateinit var pdfLauncher: ActivityResultLauncher<String>
    private var isLinkInputVisible = false
    private var postlinksList = mutableListOf<FileTitle>()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {

            setContentView(R.layout.view_task_frag)
            setCanceledOnTouchOutside(true)

            val displayMetrics = DisplayMetrics()
            val windowManager =
                requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)


            setOnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                val bottomSheet =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                setupFullHeight(bottomSheet!!)
                bottomSheet.let {
                    val behavior = BottomSheetBehavior.from(bottomSheet)
                    behavior.apply {
                        isDraggable = false
                        isHideable = true
                        peekHeight = displayMetrics.heightPixels
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }

                }
            }
        }

        return dialog
    }

    private fun displayDate(timestamp: Timestamp?): String? {
        val fullDateFormat = SimpleDateFormat("MMM dd, HHmm", Locale.getDefault())
        return try {

            val dueDate = timestamp?.toDate()
            val currentDate = java.util.Date()

            val timeDifference = dueDate!!.time - currentDate.time
            val daysDifference = timeDifference / (1000 * 60 * 60 * 24)
            val hoursDifference = (timeDifference / (1000 * 60 * 60)) % 24
            val minutesDifference = (timeDifference / (1000 * 60)) % 60

            when {
                timeDifference > 0 -> {
                    val timeLeft = StringBuilder().apply {
                        if (daysDifference > 0) append("$daysDifference d ")
                        if (hoursDifference > 0) append("$hoursDifference h ")
                        if (minutesDifference >= 0) append("$minutesDifference m ")
                    }.toString().trim()

                    "Due on ${fullDateFormat.format(dueDate)} hrs - $timeLeft left"
                }

                timeDifference == 0L -> "Due right now"
                else -> {
                    if (curTask?.complete == true) "task completed - was due on " +
                            "${fullDateFormat.format(dueDate)} hrs"
                    else "task overdue - was due on ${fullDateFormat.format(dueDate)}"

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Couldn't display date"
        }
    }


    private fun populateUI(task: Task) {
        binding.apply {
            if (curTask?.assignorId.contentEquals(AuthUtils.getCurrentUserId()) &&
                curTask?.assigneeListIds?.contains(AuthUtils.getCurrentUserId()) == false
            ) {
                submitProgress.visibility = View.INVISIBLE
                progressBar.visibility = View.GONE
                addPdfButton.visibility = View.GONE
                addLinkButton.visibility = View.GONE
                binding.postLinksRecyclerView.adapter =
                    LinksAdapter(requireContext(), postlinksList, false)

                editTask.visibility = View.VISIBLE
                deleteTask.visibility = View.VISIBLE

                editTask.setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    val frag = AddTaskFragment.newInstance(workspaceId, task.taskId, task)
                    val transaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    frag.show(transaction, frag.tag)
                    Handler().postDelayed({
                        progressBar.visibility = View.GONE
                    }, 2500)
                }

                deleteTask.setOnClickListener {
                    val workspaceDoc =
                        workspaceId?.let { it1 ->
                            firebaseFirestore.collection(WORKSPACE_COLLECTION).document(it1)}

                    workspaceDoc?.collection(TASK_SUB_COLLECTION)?.document(task.taskId!!)?.delete()
                    Toast.makeText(context,"${task.title} task deleted permanently",
                        Toast.LENGTH_LONG).show()
                }

            }

            if (task.importantLinks.isEmpty()) {
                linksTextView.visibility = View.GONE
            }

            documentsTextView.setOnClickListener {
                openDocumentsList(task.preAttachments)
            }

            postDocumentsTextView.setOnClickListener {
                openDocumentsList(task.postAttachments)
            }

            postlinksList.addAll(task.postLinks)
            binding.postLinksRecyclerView.adapter!!.notifyDataSetChanged()

            viewAttachmentsImage.setOnClickListener {
                openDocumentsList(task.preAttachments)
            }

            viewPostAttachmentsImage.setOnClickListener {
                openDocumentsList(task.postAttachments)
            }

            taskTitleTextView.text = task.title
            taskDescriptionTextView.text = task.taskDescription

            AuthUtils.getCurrentPerson(task.assignorId) { person ->
                assignorTextView.text = if (AuthUtils.getCurrentUserId() == person?.userId) {
                    "assigned by me"
                } else "assigned by ${person?.name}"
            }

            taskDueTextView.text = displayDate(task.finalDueDate)


        }

        lifecycleScope.launch {
            val assignees = UserNetworkUtils.narrowDownUsers(task.assigneeListIds)
            if (assignees.count() == 1) binding.taskAssigneesTextView.text = "Task assignee"
            setupAssigneesRecyclerView(assignees)
        }

        setupMilestonesRecyclerView(task.milestonesTask)
        setupLinksRecyclerView(task.importantLinks)
        setupDocumentsRecyclerView()
    }

    private fun openDocumentsList(attachments: MutableList<FileTitle>) {
        if (attachments.size > 0) {
            val frag = DialogDocumentsFragment.newInstance(ArrayList(attachments))
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            frag.show(transaction, frag.tag)
        } else {
            Toast.makeText(
                requireContext(),
                "No preattachments available",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupAssigneesRecyclerView(assignees: List<Person>) {
        val adapter = OtherProfilesAdapter(requireContext(), assignees.toMutableList())
        binding.taskAssigneesRecyclerView.adapter = adapter
        binding.taskAssigneesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupMilestonesRecyclerView(milestones: MutableList<MileStone>) {
        val adapter = MilestoneAdapter(requireContext(), milestones, false, candEditProgess = true)
        binding.taskMilestonesRecyclerView.adapter = adapter
        binding.taskMilestonesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupLinksRecyclerView(links: MutableList<FileTitle>) {
        val adapter = LinksAdapter(requireContext(), links)
        binding.linksRecyclerView.adapter = adapter
        binding.linksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupDocumentsRecyclerView() {
        binding.postDocumentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val canEditPostAttachments = (curTask?.assigneeListIds?.contains(
            AuthUtils.getCurrentUserId()
        ) == true)

        pdfAdapter = PdfDescAdapter(requireContext(), pdfFiles, canEditPostAttachments)
        binding.postDocumentsRecyclerView.adapter = pdfAdapter
        binding.postDocumentsRecyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun toggleLinkVisibility() {
        isLinkInputVisible = !isLinkInputVisible
        if (isLinkInputVisible) {
            binding.confirmLinkButton.visibility = View.VISIBLE
            binding.linkLayout.visibility = View.VISIBLE
            binding.titleLayout.visibility = View.VISIBLE
        } else {
            binding.confirmLinkButton.visibility = View.GONE
            binding.linkLayout.visibility = View.GONE
            binding.titleLayout.visibility = View.GONE
        }
    }

    private fun addLink() {
        val title = binding.titleInput.text.toString()
        val link = binding.linkInput.text.toString()

        if (title.isBlank() || link.isBlank()) {
            Toast.makeText(requireContext(), "Both title and link are required", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val fileTitle = FileTitle(link, title, 0, AuthUtils.getCurrentUserId())
        postlinksList.add(fileTitle)
        binding.postLinksRecyclerView.adapter?.notifyDataSetChanged()
        binding.postLinksRecyclerView.scrollToPosition(postlinksList.size - 1)

        binding.titleInput.text?.clear()
        binding.linkInput.text?.clear()
        toggleLinkVisibility()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewTaskFragBinding.inflate(layoutInflater)

        binding.postLinksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.postLinksRecyclerView.adapter = LinksAdapter(requireContext(), postlinksList, true)

        arguments?.getParcelable<Task>(ARGS_TASK_ITEM)?.let { task ->
            curTask = task
            populateUI(task)
        }

        arguments?.getString(ARGS_WORKSP_ID).let {
            workspaceId = it
        }

        pdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            processSelectedPdf(uri)
        }

        binding.addPdfButton.setOnClickListener {
            pdfLauncher.launch("application/pdf")
        }

        binding.addLinkButton.setOnClickListener {
            toggleLinkVisibility()
        }

        binding.confirmLinkButton.setOnClickListener {
            addLink()
        }

        binding.submitProgress.setOnClickListener {

            if (curTask?.finalDueDate!! < Timestamp.now()) {
                Toast.makeText(
                    requireContext(),
                    "Task overdue, can't submit progress",
                    Toast.LENGTH_LONG
                ).show()

            } else {

                binding.progressBar.visibility = View.VISIBLE
                submitProgress()
            }
        }

        return binding.root
    }


    private fun extractMilestonesFromRecyclerView(recyclerView: RecyclerView): List<MileStone> {
        val adapter = recyclerView.adapter as? MilestoneAdapter
        return adapter?.milestones ?: emptyList()
    }

    private fun processSelectedPdf(uri: Uri?) {
        uri?.let {
            val fileName = DocumentFile.fromSingleUri(requireContext(), it)?.name ?: "New Document"
            val fileTitle =
                FileTitle(uri.toString(), fileName, pdfFiles.size, AuthUtils.getCurrentUserId())
            pdfFiles.add(fileTitle)
            binding.postDocumentsRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private suspend fun uploadDocumentsAndGetFileTitles(documents: List<FileTitle>): List<FileTitle> {

        if (documents.isEmpty()) {
            return listOf()
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val uploadedFileTitles = mutableListOf<FileTitle>()

        Toast.makeText(
            context,
            "Uploading post completion documents.",
            Toast.LENGTH_SHORT
        ).show()

        for (document in documents) {
            if (!document.fileUri.startsWith("http")) {
                val fileRef = storageRef.child("$WORKSPACE_DOCUMENTS_STORE/${document.fileTitle}")
                val fileUri = Uri.parse(document.fileUri)
                fileRef.putFile(fileUri).await()
                val downloadUrl = fileRef.downloadUrl.await().toString()
                uploadedFileTitles.add(
                    FileTitle(
                        downloadUrl,
                        document.fileTitle,
                        documents.indexOf(document), AuthUtils.getCurrentUserId()
                    )
                )
            } else {
                uploadedFileTitles.add(document)
            }
        }


        Toast.makeText(
            context,
            "Uploaded all post completion documents.",
            Toast.LENGTH_SHORT
        ).show()

        return uploadedFileTitles
    }

    private fun extractLinksFromRecyclerView(recyclerView: RecyclerView): List<FileTitle> {
        val adapter = recyclerView.adapter as LinksAdapter
        return adapter.links
    }

    private fun submitProgress() {
        lifecycleScope.launch {

            if (workspaceId != null && curTask?.taskId != null) {
                val milestones =
                    extractMilestonesFromRecyclerView(binding.taskMilestonesRecyclerView)
                val milestoneMap = milestones.map { it.toHash() }
                val allMilestonesComplete = milestones.all { it.complete }
                val pdfFileTitles = uploadDocumentsAndGetFileTitles(pdfFiles).map { it.toHashMap() }
                val postLinks =
                    extractLinksFromRecyclerView(binding.postLinksRecyclerView).map { it.toHashMap() }

                val updates = hashMapOf<String, Any>(
                    "milestonesTask" to milestoneMap,
                    "complete" to allMilestonesComplete,
                    "postAttachments" to pdfFileTitles,
                    "postLinks" to postLinks
                )

                viewModel.updateTaskFields(workspaceId!!, curTask?.taskId!!, updates)

                Toast.makeText(
                    context,
                    "Updated task's progress.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(context, "Workspace ID or Task ID not found.", Toast.LENGTH_SHORT)
                    .show()
            }
        }.invokeOnCompletion {
            binding.progressBar.visibility = View.GONE
            Handler().postDelayed({ dismiss() }, 2000)
        }
    }


    private fun FileTitle.toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "fileUri" to this.fileUri,
            "fileTitle" to this.fileTitle,
            "position" to this.position,
            "ownerId" to this.ownerId
        )
    }

    private fun MileStone.toHash(): HashMap<String, Any?> {
        return hashMapOf(
            "titleMilestone" to this.titleMilestone,
            "timeDueString" to this.timeDueString,
            "complete" to this.complete,
            "inProgress" to this.inProgress
        )
    }

}