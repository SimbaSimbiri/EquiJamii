package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_IMAGE_STORE
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.AddWorkspaceDialogBinding

class AddWorkspaceDialog : Fragment(), SearchView.OnQueryTextListener {

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
    private val storage = FirebaseStorage.getInstance()
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
            binding.searchPeopleRecyclerView.adapter = NetworkAdapter(requireContext(), searchList)
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
        val passCode = binding.loginCodeInput.text.toString().toIntOrNull()

        val workspace = Workspace(
            workspaceId = workspaceId,
            titleImage = null,
            todayImageQuote = null,
            about = name,
            ownerId = "currentUserId", // Replace with actual current user ID
            passCode = passCode ?: 0,
            description = description,
            importantLinks = mutableListOf(),
            importantDocs = mutableListOf(),
            adminsListIds = mutableListOf(),
            private = false
        )

        if (workspaceId == null) {
            firestore.collection(WORKSPACE_COLLECTION).add(workspace).addOnSuccessListener { documentReference ->
                val newWorkspaceId = documentReference.id
                updateWorkspaceId(newWorkspaceId)
                uploadImageAndSaveWorkspace(workspace, newWorkspaceId)
            }
        } else {
            firestore.collection(WORKSPACE_COLLECTION).document(workspaceId!!).set(workspace).addOnSuccessListener {
                uploadImageAndSaveWorkspace(workspace, workspaceId!!)
            }
        }
    }

    private fun updateWorkspaceId(documentId: String) {
        firestore.collection(WORKSPACE_COLLECTION).document(documentId)
            .update("workspaceId", documentId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Handle successful update
                } else {
                    // Handle failure
                }
            }
    }

    private fun uploadImageAndSaveWorkspace(workspace: Workspace, workspaceId: String) {
        imageUri?.let { uri ->
            val storageRef = storage.reference.child("$WORKSPACE_IMAGE_STORE/$workspaceId")
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    workspace.titleImage = FileTitle(downloadUri.toString(), "Workspace Image", 0)
                    firestore.collection(WORKSPACE_COLLECTION).document(workspaceId).set(workspace)
                }
            }
        } ?: firestore.collection(WORKSPACE_COLLECTION).document(workspaceId).set(workspace)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterPeople(newText ?: "")
        return true
    }
}
