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
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.MilestoneAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_DOCUMENTS_STORE
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.MileStone
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.data.model.UserNetworkUtils
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
        val fullDateFormat = SimpleDateFormat("MMM dd, yyyy HHmm", Locale.getDefault())
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
                        if (daysDifference > 0) append("$daysDifference days ")
                        if (hoursDifference > 0) append("$hoursDifference hours ")
                        if (minutesDifference > 0) append("$minutesDifference minutes ")
                    }.toString().trim()

                    "Due on ${fullDateFormat.format(dueDate)} hrs\n$timeLeft left"
                }

                timeDifference == 0L -> "Due right now"
                else -> "task overdue - was due on ${fullDateFormat.format(dueDate)} hrs"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Couldn't display date"
        }
    }


    private fun populateUI(task: Task) {
        binding.apply {
            if (curTask?.assignorId.contentEquals(AuthUtils.getCurrentUserId()) && curTask?.assigneeListIds?.contains(
                    AuthUtils.getCurrentUserId()
                ) == false
            ) {
                Handler().postDelayed({
                    Toast.makeText(
                        requireContext(),
                        "You can only view assignee's task's progress here",
                        Toast.LENGTH_LONG
                    ).show()
                }, 2000)

                submitProgress.visibility = View.INVISIBLE
                progressBar.visibility = View.GONE
            }

            documentsTextView.setOnClickListener {
                openDocumentsList(task)
            }

            viewAttachmentsImage.setOnClickListener {
                openDocumentsList(task)
            }

            taskTitleTextView.text = task.title
            taskDescriptionTextView.text = task.taskDescription

            AuthUtils.getCurrentPerson(task.assignorId) { person ->
                assignorTextView.text = if (AuthUtils.getCurrentUserId() == person?.userId) {
                    "assigned by me"
                } else "assigned by ${person?.name}"
            }

            taskDueTextView.text = if (task.milestonesTask.all { it.complete }) {
                "task completed"
            } else {
                displayDate(task.finalDueDate)
            }


        }

        lifecycleScope.launch {
            val assignees = UserNetworkUtils.narrowDownUsers(task.assigneeListIds)
            if (assignees.count() == 1) binding.taskAssigneesTextView.text = "Task assignee"
            setupAssigneesRecyclerView(assignees)
        }

        setupMilestonesRecyclerView(task.milestonesTask)
        setupLinksRecyclerView(task.importantLinks)
        setupDocumentsRecyclerView(task.postAttachments)
    }

    private fun openDocumentsList(task: Task) {
        if (task.preAttachments.size > 0) {
            val frag = DialogDocumentsFragment.newInstance(ArrayList(task.preAttachments))
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

    private fun setupDocumentsRecyclerView(documents: MutableList<FileTitle>) {
        binding.postDocumentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pdfFiles.addAll(documents)
        pdfAdapter = PdfDescAdapter(requireContext(), pdfFiles, true)
        binding.postDocumentsRecyclerView.adapter = pdfAdapter
        binding.postDocumentsRecyclerView.adapter!!.notifyDataSetChanged()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewTaskFragBinding.inflate(layoutInflater)

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

        binding.submitProgress.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            submitProgress()
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
            val fileTitle = FileTitle(uri.toString(), fileName, pdfFiles.size)
            pdfFiles.add(fileTitle)
            binding.postDocumentsRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private suspend fun uploadDocumentsAndGetFileTitles(documents: List<FileTitle>): List<FileTitle> {
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
                        documents.indexOf(document)
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

    private fun submitProgress() {
        lifecycleScope.launch {

            if (workspaceId != null && curTask?.taskId != null) {
                val milestones =
                    extractMilestonesFromRecyclerView(binding.taskMilestonesRecyclerView)
                val milestoneMap = milestones.map { it.toHash() }
                val allMilestonesComplete = milestones.all { it.complete }
                val pdfFileTitles = uploadDocumentsAndGetFileTitles(pdfFiles).map { it.toHashMap() }

                val updates = hashMapOf<String, Any>(
                    "milestonesTask" to milestoneMap,
                    "isComplete" to allMilestonesComplete,
                    "postAttachments" to pdfFileTitles
                )
                Toast.makeText(
                    context,
                    "Updating task's progress.",
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.updateTaskFields(workspaceId!!, curTask?.taskId!!, updates)

            } else {
                Toast.makeText(context, "Workspace ID or Task ID not found.", Toast.LENGTH_SHORT)
                    .show()
            }
        }.invokeOnCompletion {
            binding.progressBar.visibility = View.GONE
            Handler().postDelayed({ dismiss() }, 5000)
        }
    }


    private fun FileTitle.toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "fileUri" to this.fileUri,
            "fileTitle" to this.fileTitle,
            "position" to this.position
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