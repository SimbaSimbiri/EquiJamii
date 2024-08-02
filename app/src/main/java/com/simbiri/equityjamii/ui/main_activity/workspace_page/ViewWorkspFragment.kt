package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.app.adapters.TaskAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.WorkspaceMentionAdapter
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.DialogViewWorkspBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.DialogDocumentsFragment

class ViewWorkspFragment : BottomSheetDialogFragment(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener {

    companion object {
        private const val ARGS_WORKSP_ID = "WorskpId"
        fun newInstance(workspaceId: String): ViewWorkspFragment {
            val frag = ViewWorkspFragment()
            val bundle = Bundle()
            bundle.putString(ARGS_WORKSP_ID, workspaceId)
            frag.arguments = bundle

            return frag
        }
    }

    private var workspaceId: String? = null
    private val viewModel = AddWorkspaceDialogViewModel()
    private lateinit var viewModelView: ViewWorkspViewModel
    private lateinit var binding: DialogViewWorkspBinding
    private var documentsList = mutableListOf<FileTitle>()
    private var linksList = mutableListOf<FileTitle>()
    private var workspaceList = mutableListOf<Person>()
    private var adminsList = mutableListOf<Person>()
    private var membersList = mutableListOf<Person>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workspaceId = arguments?.getString(ARGS_WORKSP_ID)
        workspaceId?.let { viewModel.loadWorkspace(it) }
        viewModelView = ViewWorkspViewModel(workspaceId)


    }

    private fun refreshFragment() {

        requireActivity().supportFragmentManager.beginTransaction().detach(this).attach(this)
            .commit()
        binding.swipeRefresh.isRefreshing = false

    }

    private fun observeViewModelView() {
        viewModelView.mentions.observe(viewLifecycleOwner) { mentions ->
            if (mentions.isEmpty()) binding.workspaceMentionsTv.visibility = View.GONE
            binding.workspMentionsRecyclerView.adapter =
                WorkspaceMentionAdapter( requireContext(), mentions, workspaceId!!)
            binding.workspMentionsRecyclerView.adapter!!.notifyDataSetChanged()
        }

        viewModelView.mytasks.observe(viewLifecycleOwner) { myTasks ->
            if (myTasks.isEmpty()) binding.myTasksTv.visibility = View.GONE

            binding.myTasksRecyclerView.adapter =
                TaskAdapter(requireContext(), myTasks, workspaceId!!, false)
            binding.myTasksRecyclerView.adapter!!.notifyDataSetChanged()
        }

        viewModelView.myDelegatedtasks.observe(viewLifecycleOwner) { myDelegatedTasks ->
            if (myDelegatedTasks.isEmpty()) binding.myDelegatedTasksTv.visibility = View.GONE

            binding.myDelegatedTasksRecyclerView.adapter =
                TaskAdapter(requireContext(), myDelegatedTasks, workspaceId!!, true)
            binding.myDelegatedTasksRecyclerView.adapter!!.notifyDataSetChanged()
        }


    }

    private fun observeViewModelAdd() {
        viewModel.workspace.observe(viewLifecycleOwner) { workspace ->
            workspace?.let { populateUI(it) }
        }

        viewModel.links.observe(viewLifecycleOwner) { links ->
            if (links.isNotEmpty()) {
                binding.linksTextView.visibility = View.VISIBLE
                binding.viewLinksImage.visibility = View.VISIBLE
            }
            linksList.addAll(links)
            binding.linksRecyclerView.adapter!!.notifyDataSetChanged()

        }
        viewModel.documents.observe(viewLifecycleOwner) { docs ->
            if (docs.isNotEmpty()) {
                binding.docsTextView.visibility = View.VISIBLE
                binding.viewAttachmentsImage.visibility = View.VISIBLE

                binding.docsTextView.setOnClickListener {
                    val frag = DialogDocumentsFragment.newInstance(ArrayList(docs))
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()

                    frag.show(transaction, frag.tag)
                }

                binding.viewAttachmentsImage.setOnClickListener {
                    val frag = DialogDocumentsFragment.newInstance(ArrayList(docs))
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()

                    frag.show(transaction, frag.tag)
                }
            }

            documentsList.addAll(docs)


        }

        viewModel.adminsList.observe(viewLifecycleOwner) { people ->

            if (people.any { person -> person.userId.contentEquals(AuthUtils.getCurrentUserId()) }) {
                binding.editWorkspace.visibility = View.VISIBLE
                binding.editWorkspace.setOnClickListener {
                    binding.progressBar.visibility = View.VISIBLE
                    val addWorkspaceDialog = AddWorkspaceDialog.newInstance(workspaceId!!)
                    val transaction =
                        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    addWorkspaceDialog.show(transaction, addWorkspaceDialog.tag)

                    Handler().postDelayed({
                        binding.progressBar.visibility = View.GONE
                    }, 3000)
                }
            }

            adminsList.addAll(people)
            workspaceList.addAll(people)
            binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModel.membersList.observe(viewLifecycleOwner) { people ->
            membersList.addAll(people)
            workspaceList.addAll(people)
            binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

        }

    }

    private fun populateUI(workspace: Workspace) {

        workspace.imageQuote?.let {
            binding.imagePostQuote.visibility = View.VISIBLE
            binding.cardPostQuote.visibility = View.VISIBLE

            val layoutParams = binding.cardPostQuote.layoutParams
            val displayMetrics = DisplayMetrics()
            val windowManager =
                requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenHeight = displayMetrics.heightPixels
            layoutParams.height = (screenHeight / 3.5).toInt()
            layoutParams.width = layoutParams.width

            binding.cardPostQuote.layoutParams = layoutParams


            Glide.with(requireContext()).load(it.fileUri).fitCenter().into(binding.imagePostQuote)

            binding.textQuote.visibility = View.VISIBLE

            binding.textQuote.text = it.fileTitle

        }

        binding.textTitle.text = workspace.titleImage?.fileTitle
        binding.addMention.setOnClickListener {
            val frag = AddMentionFragment.newInstance(workspace.workspaceId!!, null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            frag.show(transaction, frag.tag)
        }
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {

            setContentView(R.layout.dialog_view_worksp)
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogViewWorkspBinding.inflate(layoutInflater)
        observeViewModelAdd()
        observeViewModelView()

        binding.searchPeopleRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            workspaceList,
            editingWksp = false,
            addingWkspAdmins = false,
        )
        binding.swipeRefresh.setOnRefreshListener {
            refreshFragment()
        }
        binding.linksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        /*      binding.documentsRecyclerView.layoutManager =
                  LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)*/
        binding.searchPeopleRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.myTasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.myDelegatedTasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.workspMentionsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        binding.linksRecyclerView.adapter = LinksAdapter(requireContext(), linksList, false)

        binding.createTasksCard.setOnClickListener {
            val frag = AddTaskFragment.newInstance(workspaceId!!, null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            frag.show(transaction, frag.tag)
        }

        binding.searchViewAll.setOnQueryTextListener(this)

        return binding.root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterWorkspacePeople(newText ?: "")
        return true
    }

    fun filterWorkspacePeople(text: String) {
        val fullList = membersList + adminsList

        val filteredList = if (text.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name.contains(text, ignoreCase = true) }
                .toMutableList()
        }

        workspaceList.clear()
        workspaceList.addAll(filteredList)
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

    }
}