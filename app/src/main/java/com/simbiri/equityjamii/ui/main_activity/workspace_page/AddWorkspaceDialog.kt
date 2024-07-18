package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMIN_INVITED_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_INVITED_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.AddWorkspaceDialogBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddWorkspaceDialog : BottomSheetDialogFragment(), SearchView.OnQueryTextListener {

    companion object {
        private const val ARGS_WORKSP_INST = "WorksP"
        fun newInstance(workspaceId: String): AddWorkspaceDialog {
            val frag = AddWorkspaceDialog()
            val bundle = Bundle()
            bundle.putString(ARGS_WORKSP_INST, workspaceId)
            frag.arguments = bundle
            return frag
        }
    }

    private lateinit var binding: AddWorkspaceDialogBinding
    private val viewModel: AddWorkspaceDialogViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null
    private var workspaceId: String? = null

    private val openImagePicker = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            Glide.with(requireContext()).load(imageUri).into(binding.imagePostUpload)
            binding.imagePostUpload.visibility = View.VISIBLE
        }
    }

    private val cropImageOptions = CropImageContractOptions(
        null, CropImageOptions(
            true,
            false,
            CropImageView.CropShape.RECTANGLE,
            cropCornerRadius = 8.0F,
            cropMenuCropButtonTitle = "Done",
            showCropLabel = true,
            activityTitle = "Crop workspace image",
            activityBackgroundColor = requireContext().resources.getColor(R.color.black),
            toolbarColor = requireContext().resources.getColor(R.color.black),
            progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
            guidelines = CropImageView.Guidelines.OFF,
            aspectRatioX = 1,
            aspectRatioY = 1,
            fixAspectRatio = false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workspaceId = arguments?.getString(ARGS_WORKSP_INST)
        workspaceId?.let { viewModel.loadWorkspace(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddWorkspaceDialogBinding.inflate(inflater, container, false)
        setupUI()
        observeViewModel()
        viewModel.fetchAllPeople()

        binding.saveWorkspace.setOnClickListener {
            saveWorkspace()
        }

        binding.cancelWorkspace.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun setupUI() {
        binding.addWorkspThumbNail.setOnClickListener {
            openImagePicker.launch(cropImageOptions)
        }

        binding.adminsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.membersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.invitedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchPeopleRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.searchViewAll.setOnQueryTextListener(this)
    }

    private fun observeViewModel() {
        viewModel.workspace.observe(viewLifecycleOwner) { workspace ->
            workspace?.let { populateUI(it) }
        }

        viewModel.adminsList.observe(viewLifecycleOwner) { admins ->
            binding.adminsRecyclerView.adapter = OtherProfilesAdapter(requireContext(), admins)
        }

        viewModel.membersList.observe(viewLifecycleOwner) { members ->
            binding.membersRecyclerView.adapter = OtherProfilesAdapter(requireContext(), members)
        }

        viewModel.invitedMembersList.observe(viewLifecycleOwner) { invitedMembers ->
            binding.invitedRecyclerView.adapter = OtherProfilesAdapter(requireContext(), invitedMembers)
        }

        viewModel.invitedAdminsList.observe(viewLifecycleOwner) { invitedAdmins ->
            // Setup adapter for invited admins (if there's another RecyclerView for it)
        }

        viewModel.searchList.observe(viewLifecycleOwner) { searchList ->
            binding.searchPeopleRecyclerView.adapter = OtherProfilesAdapter(requireContext(), searchList)
        }
    }

    private fun populateUI(workspace: Workspace) {
        binding.nameWkspInput.setText(workspace.about)
        binding.descriptionInput.setText(workspace.description)
        binding.loginCodeInput.setText(workspace.passCode.toString())

        workspace.titleImage?.fileUri?.let {
            Glide.with(requireContext()).load(it).into(binding.imagePostUpload)
            binding.imagePostUpload.visibility = View.VISIBLE
        }
    }

    private fun saveWorkspace() {
        val name = binding.nameWkspInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val passCode = binding.loginCodeInput.text.toString().toIntOrNull() ?: 0
        val ownerId = AuthUtils.getCurrentUserId()!!

        val links = extractLinksFromRecyclerView(binding.linksRecyclerView)
        val documents = extractFileTitlesFromPdfRecyclerView(binding.documentsRecyclerView)

        lifecycleScope.launch {
            val workspaceId = if (workspaceId == null) {
                firestore.collection(WORKSPACE_COLLECTION).document().id
            } else {
                workspaceId!!
            }

            viewModel.saveWorkspace(
                workspaceId,
                name,
                description,
                passCode,
                ownerId,
                links,
                documents,
                imageUri
            )

            saveSubcollections(workspaceId)
        }
    }

    private suspend fun saveSubcollections(workspaceId: String) {
        val admins = extractPeopleFromRecyclerView(binding.adminsRecyclerView)
        val members = extractPeopleFromRecyclerView(binding.membersRecyclerView)
        val invitedMembers = extractPeopleFromRecyclerView(binding.invitedRecyclerView)
        val invitedAdmins = listOf<Person>()

        savePeopleSubcollection(workspaceId, WORKSP_ADMINS_SUB_COLLECTION, admins)
        savePeopleSubcollection(workspaceId, WORKSP_MEMBERS_SUB_COLLECTION, members)
        savePeopleSubcollection(workspaceId, WORKSP_INVITED_SUB_COLLECTION, invitedMembers)
        savePeopleSubcollection(workspaceId, WORKSP_ADMIN_INVITED_SUB_COLLECTION, invitedAdmins)
    }

    private suspend fun savePeopleSubcollection(workspaceId: String, subcollection: String, people: List<Person>) {
        val subcollectionRef = firestore.collection(WORKSPACE_COLLECTION).document(workspaceId).collection(subcollection)

        people.forEach { person ->
            subcollectionRef.document(person.userId).set(emptyMap<String, Any>()).await()
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

    private fun extractPeopleFromRecyclerView(recyclerView: RecyclerView): List<Person> {
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


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {

            setContentView(R.layout.add_workspace_dialog)
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
}
