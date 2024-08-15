package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.MilestoneAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.MileStone
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.databinding.AddTaskFragBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskFragment : BottomSheetDialogFragment(), SearchView.OnQueryTextListener {

    companion object {
        private const val ARGS_WORKSP_ID = "workspid"
        private const val ARGS_TASK_ID = "taskId"
        private const val ARGS_TASK = "CurTask"
        fun newInstance(
            workspId: String?,
            taskId: String?, task: Task?
        ): AddTaskFragment {
            val frag = AddTaskFragment()
            val bundle = Bundle()
            bundle.putString(ARGS_WORKSP_ID, workspId)
            bundle.putString(ARGS_TASK_ID, taskId)
            bundle.putParcelable(ARGS_TASK, task)
            frag.arguments = bundle
            return frag
        }
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val viewModel: AddTaskViewModel by viewModels()
    private lateinit var binding: AddTaskFragBinding
    private var workspaceId: String? = null
    private var taskId: String? = null
    private var linksList = mutableListOf<FileTitle>()
    private var milestonesList = mutableListOf<MileStone>()
    private var assigneesList = mutableListOf<Person>()
    private var documentsList = mutableListOf<FileTitle>()
    private var isLinkInputVisible = false
    private var isMilestoneInputVisible = false
    private var membersList = mutableListOf<Person>()
    private var adminList = mutableListOf<Person>()
    private val viewModelWorksp = AddWorkspaceDialogViewModel()
    private val listPeopleAll = mutableListOf<Person>()
    private var finalDate: Calendar = Calendar.getInstance()
    private var taskCur: Task? = null
    private lateinit var pdfLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddTaskFragBinding.inflate(layoutInflater)
        setupUI()
        observeViewModel()

        workspaceId = arguments?.getString(ARGS_WORKSP_ID)
        taskId = arguments?.getString(ARGS_TASK_ID)
        taskId?.let { viewModel.loadTask(workspaceId!!, it) }
        workspaceId?.let { viewModelWorksp.loadWorkspace(it) }
        taskCur = arguments?.getParcelable<Task>(ARGS_TASK)
        taskCur?.let { populateUI(it) }

        if (taskCur == null) {
            setUpEventDateTimeDialogs()
        }

        return binding.root
    }

    private fun setupUI() {
        binding.addMilestonesButton.setOnClickListener {
            toggleMilestoneVisibility()
        }

        binding.addLinkButton.setOnClickListener {
            toggleLinkVisibility()
        }

        binding.confirmLinkButton.setOnClickListener {
            addLink()
        }

        pdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val filename = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }

            if (filename != null) {
                val fileTitle = FileTitle(uri.toString(), filename, 0, AuthUtils.getCurrentUserId())
                documentsList.add(fileTitle)
                binding.documentsRecyclerView.adapter!!.notifyDataSetChanged()
                binding.documentsRecyclerView.scrollToPosition(documentsList.size - 1)
            }

        }

        binding.addPdfButton.setOnClickListener {
            pdfLauncher.launch("application/pdf")
        }

        binding.saveTask.setOnClickListener {
            saveTask()
        }

        binding.cancelTask.setOnClickListener {
            dismiss()
        }

        binding.confirmMilestoneButton.setOnClickListener {
            addMilestone()
        }


        binding.searchViewAll.setOnQueryTextListener(this)

        binding.linksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.milestonesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.searchPeopleRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.taskAssigneesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.documentsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.linksRecyclerView.adapter = LinksAdapter(requireContext(), linksList, true)
        binding.milestonesRecyclerView.adapter =
            MilestoneAdapter(requireContext(), milestonesList, true)
        binding.taskAssigneesRecyclerView.adapter =
            OtherProfilesAdapter(
                requireContext(),
                assigneesList, workspaceId, taskId,
                canDelete = true,
                editingTask = true
            )
        binding.documentsRecyclerView.adapter =
            PdfDescAdapter(requireContext(), documentsList, true)
        binding.searchPeopleRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            listPeopleAll,
            editingTask = true
        ) { person, flag ->
            if (flag == 4) {
                if (!assigneesList.contains(person)) {
                    assigneesList.add(person)
                    binding.taskAssigneesRecyclerView.adapter!!.notifyDataSetChanged()
                    binding.taskAssigneesRecyclerView.scrollToPosition(assigneesList.size - 1)
                } else Toast.makeText(
                    requireContext(),
                    "${person.name} already added as an assignee",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
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

    private fun toggleMilestoneVisibility() {
        isMilestoneInputVisible = !isMilestoneInputVisible
        if (isMilestoneInputVisible) {
            binding.nameMilestoneLayout.visibility = View.VISIBLE
            binding.dueDateMilestoneLayout.visibility = View.VISIBLE
            binding.confirmMilestoneButton.visibility = View.VISIBLE
        } else {
            binding.nameMilestoneLayout.visibility = View.GONE
            binding.dueDateMilestoneLayout.visibility = View.GONE
            binding.confirmMilestoneButton.visibility = View.GONE

        }
    }

    private fun addMilestone() {
        val titleMilestone = binding.nameMilestoneInput.text.toString()
        val duedatemileston = binding.dueDateMilestoneInput.text.toString()

        if (titleMilestone.isBlank() || duedatemileston.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Both title and due date are required",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        val milestone = MileStone(titleMilestone, duedatemileston, false, false)
        milestonesList.add(milestone)
        binding.milestonesRecyclerView.adapter?.notifyDataSetChanged()
        binding.milestonesRecyclerView.scrollToPosition(milestonesList.size - 1)

        binding.nameMilestoneInput.text?.clear()
        binding.dueDateMilestoneInput.text?.clear()
        toggleMilestoneVisibility()
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
        linksList.add(fileTitle)
        binding.linksRecyclerView.adapter?.notifyDataSetChanged()
        binding.linksRecyclerView.scrollToPosition(linksList.size - 1)

        binding.titleInput.text?.clear()
        binding.linkInput.text?.clear()
        toggleLinkVisibility()
    }

    private fun saveTask() {
        val taskTitle = binding.nameTaskInput.text.toString()
        val taskDescription = binding.descriptionInput.text.toString()
        val finalDueDate = Timestamp(finalDate.time)
        val taskOwnerId = AuthUtils.getCurrentUserId()
        val isComplete = false

        if (taskTitle.isBlank() || taskDescription.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Title and Description are required",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val milestones = extractMilestonesFromRecyclerView(binding.milestonesRecyclerView)
        val links = extractLinksFromRecyclerView(binding.linksRecyclerView)
        val assignees =
            extractPeopleFromRecyclerView(binding.taskAssigneesRecyclerView).map { it.userId }
        val documents = extractFileTitlesFromPdfRecyclerView(binding.documentsRecyclerView)

        if (assignees.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "At least one assignee is required",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            binding.contentLoadingProgressBar.visibility = View.VISIBLE

            if (taskId == null) {
                taskId =
                    firestore.collection(WORKSPACE_COLLECTION).document(workspaceId!!).collection(
                        TASK_SUB_COLLECTION
                    ).document().id

                viewModel.saveTaskToFirebase(
                    taskOwnerId,
                    taskTitle,
                    taskDescription,
                    assignees.toMutableList(),
                    documents.toMutableList(),
                    taskCur?.postAttachments ?: mutableListOf(),
                    milestones.toMutableList(),
                    links.toMutableList(),
                    workspaceId!!,
                    finalDueDate,
                    taskId!!,
                    isComplete,
                    true
                )
                Toast.makeText(requireContext(), "Registering new task", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.saveTaskToFirebase(
                    taskOwnerId,
                    taskTitle,
                    taskDescription,
                    assignees.toMutableList(),
                    documents.toMutableList(),
                    taskCur?.postAttachments ?: mutableListOf(),
                    milestones.toMutableList(),
                    links.toMutableList(),
                    workspaceId!!,
                    finalDueDate,
                    taskId!!,
                    isComplete,
                    false
                )

                Toast.makeText(
                    requireContext(),
                    "Updating task changes",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }.invokeOnCompletion {
            binding.contentLoadingProgressBar.visibility = View.INVISIBLE
            dismiss()
        }
    }

    private fun observeViewModel() {

        viewModelWorksp.adminsList.observe(viewLifecycleOwner) { admins ->
            adminList.addAll(admins)
            listPeopleAll.addAll(admins)
            binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModelWorksp.membersList.observe(viewLifecycleOwner) { members ->
            membersList.addAll(members)
            listPeopleAll.addAll(members)
            binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()
        }

    }

    private fun populateUI(task: Task) {
        setUpEventDateTimeDialogs()

        workspaceId?.let { workspId ->
            binding.taskAssigneesRecyclerView.adapter =
                OtherProfilesAdapter(
                    requireContext(),
                    assigneesList,
                    workspId = workspId,
                    taskId = task.taskId,
                    canDelete = true,
                    editingTask = true
                )
        }

        binding.apply {
            nameTaskInput.setText(task.title)
            descriptionInput.setText(task.taskDescription)
            milestonesList.addAll(task.milestonesTask)
            milestonesRecyclerView.adapter!!.notifyDataSetChanged()
            task.assigneeListIds.forEach {
                AuthUtils.getCurrentPerson(it) { person ->
                    assigneesList.add(
                        person!!
                    )
                }
            }
            taskAssigneesRecyclerView.adapter!!.notifyDataSetChanged()
            linksList.addAll(task.importantLinks)
            linksRecyclerView.adapter!!.notifyDataSetChanged()
            documentsList.addAll(task.preAttachments)
            documentsRecyclerView.adapter!!.notifyDataSetChanged()

            val eventDateTime = task.finalDueDate?.toDate() ?: Calendar.getInstance().time

            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(eventDateTime).toString()
            val formattedTime = DateFormat.format("HHmm", eventDateTime).toString()

            binding.selectedDateDisplayTv.text = formattedDate
            binding.selectedTimeDisplayTv.text = formattedTime + " hrs"

        }
    }

    private fun setUpEventDateTimeDialogs() {

        if (taskCur?.finalDueDate != null) {
            finalDate.time = taskCur?.finalDueDate?.toDate() ?: Calendar.getInstance().time
        } else {
            finalDate = Calendar.getInstance()
        }

        binding.selectDateTv.setOnClickListener {
            DatePickerDialog(
                requireContext(), R.style.CustomDatePickerTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    finalDate.set(Calendar.YEAR, year)
                    finalDate.set(Calendar.MONTH, monthOfYear)
                    finalDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(finalDate.time)
                    binding.selectedDateDisplayTv.text = formattedDate
                },
                finalDate.get(Calendar.YEAR),
                finalDate.get(Calendar.MONTH),
                finalDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.selectTimeTv.setOnClickListener {
            TimePickerDialog(
                requireContext(), R.style.CustomTimePickerTheme,
                { _, hourOfDay, minute ->
                    finalDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    finalDate.set(Calendar.MINUTE, minute)

                    val formattedTime = DateFormat.format("HHmm", finalDate).toString()
                    binding.selectedTimeDisplayTv.text = formattedTime + " hrs"
                },
                finalDate.get(Calendar.HOUR_OF_DAY),
                finalDate.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun extractFileTitlesFromPdfRecyclerView(recyclerView: RecyclerView): List<FileTitle> {
        val adapter = recyclerView.adapter as PdfDescAdapter
        return adapter.fileTitleList
    }

    private fun extractLinksFromRecyclerView(recyclerView: RecyclerView): List<FileTitle> {
        val adapter = recyclerView.adapter as LinksAdapter
        return adapter.links
    }

    private fun extractMilestonesFromRecyclerView(recyclerView: RecyclerView): List<MileStone> {
        val adapter = recyclerView.adapter as MilestoneAdapter
        return adapter.milestones
    }

    private fun extractPeopleFromRecyclerView(recyclerView: RecyclerView): MutableList<Person> {
        val adapter = recyclerView.adapter as OtherProfilesAdapter
        return adapter.peopleList
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterWorkspacePeople(newText ?: "")
        return true
    }

    fun filterWorkspacePeople(text: String) {
        val fullList = membersList + adminList

        val filteredList = if (text.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name.contains(text, ignoreCase = true) }
                .toMutableList()
        }

        listPeopleAll.clear()
        listPeopleAll.addAll(filteredList)
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {
            setContentView(R.layout.add_task_frag)
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
                        isHideable = false
                        peekHeight = (displayMetrics.heightPixels).toInt()
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}
