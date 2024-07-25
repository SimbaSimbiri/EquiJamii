package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.MilestoneAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.MileStone
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.databinding.AddTaskFragBinding
import kotlinx.coroutines.launch

class AddTaskFragment : BottomSheetDialogFragment(), SearchView.OnQueryTextListener {

    companion object {
        private const val ARGS_WORKSP_ID = "workspid"
        private const val ARGS_TASK_ID = "taskId"
        fun newInstance(
            workspId: String?,
            taskId: String?
        ): AddTaskFragment {
            val frag = AddTaskFragment()
            val bundle = Bundle()
            bundle.putString(ARGS_WORKSP_ID, workspId)
            bundle.putString(ARGS_TASK_ID, taskId)
            frag.arguments = bundle
            return frag
        }
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workspaceId = arguments?.getString(ARGS_WORKSP_ID)
        taskId = arguments?.getString(ARGS_TASK_ID)
        taskId?.let { viewModel.loadTask(workspaceId!!, it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddTaskFragBinding.inflate(layoutInflater)
        setupUI()
        observeViewModel()
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

        binding.addPdfButton.setOnClickListener {
            // Handle PDF addition
        }

        binding.saveTask.setOnClickListener {
            saveTask()
        }

        binding.cancelTask.setOnClickListener {
            dismiss()
        }

        binding.searchViewAll.setOnQueryTextListener(this)

        // Setup RecyclerViews
        binding.linksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.milestonesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchPeopleRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.taskAssigneesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.documentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.linksRecyclerView.adapter = LinksAdapter(requireContext(), linksList, true)
        binding.milestonesRecyclerView.adapter = MilestoneAdapter(requireContext(), milestonesList, true)
        binding.taskAssigneesRecyclerView.adapter = OtherProfilesAdapter(requireContext(), assigneesList, true)
        binding.documentsRecyclerView.adapter = PdfDescAdapter(requireContext(), documentsList, true)
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
        } else {
            binding.nameMilestoneLayout.visibility = View.GONE
            binding.dueDateMilestoneLayout.visibility = View.GONE
        }
    }

    private fun addLink() {
        val title = binding.titleInput.text.toString()
        val link = binding.linkInput.text.toString()

        if (title.isBlank() || link.isBlank()) {
            Toast.makeText(requireContext(), "Both title and link are required", Toast.LENGTH_SHORT).show()
            return
        }

        val fileTitle = FileTitle(link, title, 0)
        linksList.add(fileTitle)
        binding.linksRecyclerView.adapter?.notifyDataSetChanged()

        binding.titleInput.text?.clear()
        binding.linkInput.text?.clear()
        toggleLinkVisibility()
    }

    private fun saveTask() {
        val taskTitle = binding.nameTaskInput.text.toString()
        val taskDescription = binding.descriptionInput.text.toString()
        val finalDueDate = "" //TODO // Replace with actual date and time
        val taskOwnerId = "someOwnerId" // Replace with actual owner ID
        val isComplete = false

        if (taskTitle.isBlank() || taskDescription.isBlank()) {
            Toast.makeText(requireContext(), "Title and Description are required", Toast.LENGTH_SHORT).show()
            return
        }

        val milestones = extractMilestonesFromRecyclerView(binding.milestonesRecyclerView)
        val links = extractLinksFromRecyclerView(binding.linksRecyclerView)
        val assignees = extractPeopleFromRecyclerView(binding.taskAssigneesRecyclerView).map { it.userId }
        val documents = extractFileTitlesFromPdfRecyclerView(binding.documentsRecyclerView)

        lifecycleScope.launch {
            viewModel.saveTaskToFirebase(
                taskOwnerId,
                taskTitle,
                taskDescription,
                assignees.toMutableList(),
                documents.toMutableList(),
                documents.toMutableList(), // Pre and post attachments are the same for now
                milestones.toMutableList(),
                links.toMutableList(),
                workspaceId!!,
                finalDueDate,
                taskId ?: "",
                isComplete,
                taskId == null
            )
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let { populateUI(it) }
        }
    }

    private fun populateUI(task: Task) {
        binding.nameTaskInput.setText(task.title)
        binding.descriptionInput.setText(task.taskDescription)
        // Set other UI components based on the task data
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
        viewModel.filterPeople(newText ?: "")
        return true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {
            setContentView(R.layout.add_task_frag)
            setCanceledOnTouchOutside(true)

            val displayMetrics = DisplayMetrics()
            val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            setOnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                setupFullHeight(bottomSheet!!)
                bottomSheet.let {
                    val behavior = BottomSheetBehavior.from(bottomSheet)
                    behavior.apply {
                        isDraggable = true
                        isHideable = true
                        peekHeight = (displayMetrics.heightPixels * 0.85).toInt()
                        state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}
