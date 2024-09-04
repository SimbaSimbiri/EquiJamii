package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.app.adapters.TaskAdapter
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.WorkspaceMentionAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.DialogViewWorkspBinding
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.DialogDocumentsFragment

class ViewWorkspFragment : Fragment(),
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

    private var currentWorksp: Workspace? = null
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


    private fun observeViewModelView() {
        viewModelView.mentions.observe(viewLifecycleOwner) { mentions ->
            if (mentions.isEmpty()) binding.workspaceMentionsTv.visibility = View.GONE
            else binding.workspaceMentionsTv.visibility = View.VISIBLE

            binding.workspMentionsRecyclerView.adapter =
                WorkspaceMentionAdapter(requireContext(), mentions.toMutableList(), workspaceId!!)
            binding.workspMentionsRecyclerView.adapter!!.notifyDataSetChanged()
        }

        viewModelView.mytasks.observe(viewLifecycleOwner) { myTasks ->
            if (myTasks.isEmpty()) binding.myTasksTv.visibility = View.GONE
            else binding.myTasksTv.visibility = View.VISIBLE

            binding.myTasksRecyclerView.adapter =
                TaskAdapter(requireContext(), myTasks.toMutableList(), workspaceId!!, false)
            binding.myTasksRecyclerView.adapter!!.notifyDataSetChanged()
        }

        viewModelView.myDelegatedtasks.observe(viewLifecycleOwner) { myDelegatedTasks ->
            if (myDelegatedTasks.isEmpty()) binding.myDelegatedTasksTv.visibility = View.GONE
            else binding.myDelegatedTasksTv.visibility = View.VISIBLE

            binding.myDelegatedTasksRecyclerView.adapter =
                TaskAdapter(requireContext(), myDelegatedTasks.toMutableList(), workspaceId!!, true)
            binding.myDelegatedTasksRecyclerView.adapter!!.notifyDataSetChanged()
        }


    }

    private fun observeViewModelAdd() {
        viewModel.workspace.observe(viewLifecycleOwner) { workspace ->
            currentWorksp = workspace
            workspace?.let {
                populateHeader(it)
            }
            Handler().postDelayed({
                workspace?.let { populateUI(it) }
            }, 700)

        }

        viewModel.links.observe(viewLifecycleOwner) { links ->
            if (links.isNotEmpty()) {
                linksList.addAll(links.reversed())
                binding.linksRecyclerView.adapter!!.notifyDataSetChanged()
            }

        }
        viewModel.documents.observe(viewLifecycleOwner) { docs ->
            binding.linksTextView.visibility = View.VISIBLE
            binding.viewAttachmentsImage.visibility = View.VISIBLE

            binding.linksTextView.setOnClickListener {
                showDocs(docs)
            }

            binding.viewAttachmentsImage.setOnClickListener {
                showDocs(docs)
            }

            documentsList.addAll(docs)


        }

        if (adminsList.isEmpty()) {
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
        }



        if (membersList.isEmpty()) {
            viewModel.membersList.observe(viewLifecycleOwner) { people ->
                membersList.addAll(people)
                workspaceList.addAll(people)
                binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

            }
        }


    }

    private fun populateHeader(worksp: Workspace) {
        worksp.imageQuote?.let {
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
    }

    private fun showDocs(docs: MutableList<FileTitle>?) {
        if (docs?.isEmpty() == true) {
            Toast.makeText(requireContext(), "No documents available", Toast.LENGTH_SHORT).show()
            return
        }
        val frag =
            DialogDocumentsFragment.newInstance(ArrayList(docs!!.sortedBy { it.fileTitle }))
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        frag.show(transaction, frag.tag)
    }

    private fun populateUI(workspace: Workspace) {

        binding.textTitle.text = workspace.titleImage?.fileTitle
        binding.addMention.setOnClickListener {
            if (adminsList.map { it.userId }
                    .contains(AuthUtils.getCurrentUserId()) || membersList.map { it.userId }
                    .contains(AuthUtils.getCurrentUserId())) {

                val frag = AddMentionFragment.newInstance(workspace.workspaceId!!, null, null)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()

                frag.show(transaction, frag.tag)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Only workspace members and admin can create mentions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.leaveWorkspaceImg.setOnClickListener {
            if (adminsList.map { it.userId }
                    .contains(AuthUtils.getCurrentUserId()) || membersList.map { it.userId }
                    .contains(AuthUtils.getCurrentUserId())) {
                alertLeaveWorksp()
            }
        }

    }

    private fun alertLeaveWorksp() {
        AlertDialog.Builder(requireContext())
            .setTitle("Leave ${currentWorksp?.titleImage?.fileTitle}?")
            .setMessage("If you leave, you will have to request admin to add you again")
            .setPositiveButton(
                "Confirm exit"
            ) { dialog, _ ->
                leaveWorkSpace()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun leaveWorkSpace() {
        val firestore = FirebaseFirestore.getInstance()
        val workspaceCollection = firestore.collection(
            WORKSPACE_COLLECTION
        )

        if (adminsList.map { it.userId }.contains(AuthUtils.getCurrentUserId()!!)) {
            if (adminsList.size == 1) {

                if (membersList.isEmpty()) {
                    workspaceCollection.document(workspaceId!!).delete()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Workspace ownership to be relinquished to existing member",
                        Toast.LENGTH_LONG
                    ).show()
                    workspaceCollection.document(workspaceId!!)
                        .update("ownerId", membersList.random().userId)
                }

            } else {

                if (currentWorksp!!.ownerId.contentEquals(AuthUtils.getCurrentUserId())) {
                    val currentAdmins =
                        adminsList.filter { !it.userId.contentEquals(currentWorksp!!.ownerId) }
                    workspaceCollection.document(workspaceId!!)
                        .update("ownerId", currentAdmins.random().userId)
                    Toast.makeText(
                        requireContext(),
                        "Workspace ownership to be relinquished to existing admin",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }


        workspaceId?.let { workspId ->

            val adminsCollection = workspaceCollection.document(workspId).collection(
                WORKSP_ADMINS_SUB_COLLECTION
            )

            val membersCollection = workspaceCollection.document(workspId).collection(
                WORKSP_MEMBERS_SUB_COLLECTION
            )

            firestore.collection(USERS_COLLECTION).document(AuthUtils.getCurrentUserId()!!)
                .update("workspaces", FieldValue.arrayRemove(workspId))

            membersCollection.document(AuthUtils.getCurrentUserId()!!).delete()
            adminsCollection.document(AuthUtils.getCurrentUserId()!!).delete()

        }

        Toast.makeText(
            requireContext(),
            "You left ${currentWorksp?.titleImage?.fileTitle} workspace",
            Toast.LENGTH_LONG
        ).show()

        dismiss()
    }

    private fun dismiss() {
        val action = ViewWorkspFragmentDirections.actionOpenMain()
        findNavController().navigate(action)
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
            workspaceList.sortedBy { it.name }.toMutableList(),
            editingWksp = false,
            addingWkspAdmins = false,
        )

        binding.linksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
            if (adminsList.map { it.userId }
                    .contains(AuthUtils.getCurrentUserId()) || membersList.map { it.userId }
                    .contains(AuthUtils.getCurrentUserId())) {
                val frag = AddTaskFragment.newInstance(workspaceId!!, null, null)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                frag.show(transaction, frag.tag)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Only workspace members and admin can create tasks",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        val fullList = adminsList + membersList

        val filteredList = if (text.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name.contains(text, ignoreCase = true) }
                .toSet()
        }

        workspaceList.clear()
        workspaceList.addAll(filteredList)
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

    }
}