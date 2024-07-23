package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.UserNetworkUtils
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.DialogViewWorkspBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    private lateinit var binding: DialogViewWorkspBinding
    private var documentsList = mutableListOf<FileTitle>()
    private var linksList = mutableListOf<FileTitle>()
    private val dispatchersIO =  Dispatchers.IO
    private val firestore = FirebaseFirestore.getInstance()
    private var workspaceList = mutableListOf<Person>()
    private var adminsList = mutableListOf<Person>()
    private var membersList = mutableListOf<Person>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workspaceId = arguments?.getString(ARGS_WORKSP_ID)
        workspaceId?.let { viewModel.loadWorkspace(it) }
    }

    private fun observeViewModel() {
        viewModel.workspace.observe(viewLifecycleOwner) { workspace ->
            workspace?.let { populateUI(it) }
        }

        viewModel.links.observe(viewLifecycleOwner) { links ->
            linksList.addAll(links)
            binding.linksRecyclerView.adapter!!.notifyDataSetChanged()

        }
        viewModel.documents.observe(viewLifecycleOwner) { docs ->
            documentsList.addAll(docs)
            binding.documentsRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModel.adminsList.observe(viewLifecycleOwner){people->
            adminsList.addAll(people)
            workspaceList.addAll(people)
            binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModel.membersList.observe(viewLifecycleOwner){people->
            membersList.addAll(people)
            workspaceList.addAll(people)
            binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

        }

    }

    private fun populateUI(workspace: Workspace) {

        workspace.imageQuote?.fileUri?.let {
            Glide.with(requireContext()).load(it).into(binding.imagePostQuote)
            binding.imagePostQuote.visibility = View.VISIBLE
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogViewWorkspBinding.inflate(layoutInflater)
        observeViewModel()

        binding.searchPeopleRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            workspaceList,
            editingWksp = false,
            addingWkspAdmins = false,
        )
        binding.linksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.documentsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchPeopleRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.linksRecyclerView.adapter = LinksAdapter(requireContext(), linksList, false)
        binding.documentsRecyclerView.adapter =
            PdfDescAdapter(requireContext(), documentsList, false)

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

        val filteredList = if (text.isEmpty()) {
            membersList + adminsList
        } else {
            workspaceList.filter { it.name.contains(text, ignoreCase = true) }
                .toMutableList()
        }

        workspaceList.clear()
        workspaceList.addAll(filteredList)
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

    }
}