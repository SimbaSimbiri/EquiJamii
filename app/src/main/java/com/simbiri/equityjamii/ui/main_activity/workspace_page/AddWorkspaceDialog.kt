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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.documentfile.provider.DocumentFile
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.LinksAdapter
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.DialogAddWorkspaceBinding
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

    private var currentWorkspace: Workspace? = null
    private lateinit var binding: DialogAddWorkspaceBinding
    private val viewModel: AddWorkspaceDialogViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null
    private var imageQuoteUri: Uri? = null
    private var workspaceId: String? = null
    private var membersList = mutableListOf<Person>()
    private var adminList = mutableListOf<Person>()
    private var invitedMembersList = mutableListOf<Person>()
    private var invitedAdminsList = mutableListOf<Person>()
    private var documentsList = mutableListOf<FileTitle>()
    private var linksList = mutableListOf<FileTitle>()
    private lateinit var pdfLauncher: ActivityResultLauncher<String>
    private var isLinkInputVisible = false
    private var currPerson: Person? = null

    var clickedThumbnail = false
    var clickedImQuote = false

    private val openImagePicker =
        registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                if (clickedThumbnail) {
                    imageUri = result.uriContent
                    Glide.with(requireContext()).load(imageUri).into(binding.imagePostUpload)
                    binding.imagePostUpload.visibility = View.VISIBLE
                } else if (clickedImQuote) {
                    imageQuoteUri = result.uriContent
                    Glide.with(requireContext()).load(imageQuoteUri).into(binding.imagePostQuote)
                    binding.imagePostQuote.visibility = View.VISIBLE
                    binding.motivationQuoteLayout.visibility = View.VISIBLE

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workspaceId = arguments?.getString(ARGS_WORKSP_INST)
        workspaceId?.let { viewModel.loadWorkspace(it) }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddWorkspaceBinding.inflate(inflater, container, false)
        setupUI()
        observeViewModel()

        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()) { person: Person? ->
            viewModel.fetchAllPeople(person!!)
            currPerson = person
        }


        pdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val filename = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }

            if (filename != null) {
                val fileTitle = FileTitle(uri.toString(), filename, 0, AuthUtils.getCurrentUserId())
                documentsList.add(fileTitle)
                binding.documentsRecyclerView.adapter!!.notifyDataSetChanged()
            }

        }

        binding.addPdfButton.setOnClickListener {
            pdfLauncher.launch("application/pdf")

        }

        binding.saveWorkspace.setOnClickListener {
            saveWorkspace()
        }

        binding.cancelWorkspace.setOnClickListener {
            dismiss()
        }

        binding.addLinkButton.setOnClickListener {
            toggleVisibility()
        }

        binding.confirmLinkButton.setOnClickListener {
            addLink()
        }

        return binding.root
    }

    private fun toggleVisibility() {
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

    private fun setupUI() {


        binding.addWorkspThumbNail.setOnClickListener {
            clickedThumbnail = true
            clickedImQuote = false

            val cropImageOptions = CropImageContractOptions(
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
                    aspectRatioX = 16,
                    aspectRatioY = 9,
                    fixAspectRatio = true
                )
            )
            openImagePicker.launch(cropImageOptions)
        }

        binding.addImageQuote.setOnClickListener {
            clickedThumbnail = false
            clickedImQuote = true

            val cropImageOptions = CropImageContractOptions(
                null, CropImageOptions(
                    true,
                    false,
                    CropImageView.CropShape.RECTANGLE,
                    cropCornerRadius = 8.0F,
                    cropMenuCropButtonTitle = "Done",
                    showCropLabel = true,
                    activityTitle = "Crop image quote",
                    activityBackgroundColor = requireContext().resources.getColor(R.color.black),
                    toolbarColor = requireContext().resources.getColor(R.color.black),
                    progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
                    guidelines = CropImageView.Guidelines.OFF,
                    aspectRatioX = 16,
                    aspectRatioY = 9,
                    fixAspectRatio = true
                )
            )
            openImagePicker.launch(cropImageOptions)

        }

        binding.invitedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.invitedAdminsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.searchPeopleRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.linksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.documentsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)



        binding.invitedAdminsRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            invitedAdminsList, workspaceId, canDelete = true, editingWksp = true
        )
        binding.invitedRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            invitedMembersList, workspaceId,
            canDelete = true, editingWksp = true
        )

        binding.linksRecyclerView.adapter = LinksAdapter(requireContext(), linksList, true)
        binding.documentsRecyclerView.adapter =
            PdfDescAdapter(requireContext(), documentsList, true)
        binding.searchViewAll.setOnQueryTextListener(this)
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

        binding.titleInput.text?.clear()
        binding.linkInput.text?.clear()
        toggleVisibility()
    }

    private fun observeViewModel() {
        viewModel.workspace.observe(viewLifecycleOwner) { workspace ->
            currentWorkspace = workspace
            workspace?.let { populateUI(it) }
        }

        viewModel.adminsList.observe(viewLifecycleOwner) { admins ->
            invitedAdminsList.addAll(admins)
            binding.invitedAdminsRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModel.membersList.observe(viewLifecycleOwner) { members ->
            invitedMembersList.addAll(members)
            binding.invitedRecyclerView.adapter!!.notifyDataSetChanged()
        }

        viewModel.links.observe(viewLifecycleOwner) { links ->
            linksList.addAll(links)
            binding.linksRecyclerView.adapter!!.notifyDataSetChanged()

        }
        viewModel.documents.observe(viewLifecycleOwner) { docs ->
            documentsList.addAll(docs)
            binding.documentsRecyclerView.adapter!!.notifyDataSetChanged()

        }

        viewModel.searchList.observe(viewLifecycleOwner) { searchList ->
            binding.searchPeopleRecyclerView.adapter = OtherProfilesAdapter(
                requireContext(),
                searchList,
                editingWksp = true,
                addingWkspAdmins = true,
            ) { person, flag ->
                if (invitedAdminsList.contains(person) || invitedMembersList.contains(person) ||
                    membersList.contains(person) || adminList.contains(person)
                ) {
                    Toast.makeText(
                        requireContext(),
                        "${person.name} already invited or present in workspace",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (flag == 1) {
                        invitedAdminsList.add(person)
                        binding.invitedAdminsRecyclerView.adapter!!.notifyDataSetChanged()
                        binding.invitedAdminsRecyclerView.scrollToPosition(invitedAdminsList.size - 1)
                    } else {
                        invitedMembersList.add(person)
                        binding.invitedRecyclerView.adapter!!.notifyDataSetChanged()
                        binding.invitedRecyclerView.scrollToPosition(invitedMembersList.size - 1)

                    }
                }

            }

        }
    }

    private fun populateUI(workspace: Workspace) {
        binding.nameWkspInput.setText(workspace.titleImage!!.fileTitle)
        binding.descriptionInput.setText(workspace.description)
        binding.loginCodeInput.setText(workspace.passCode.toString())

        binding.invitedAdminsRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            invitedAdminsList, workspace.workspaceId, canDelete = true, editingWksp = true
        )

        binding.invitedRecyclerView.adapter = OtherProfilesAdapter(
            requireContext(),
            invitedMembersList, workspace.workspaceId,
            canDelete = true, editingWksp = true
        )

        workspace.titleImage?.let {
            Glide.with(requireContext()).load(it.fileUri).into(binding.imagePostUpload)
            binding.imagePostUpload.visibility = View.VISIBLE
            imageUri = Uri.parse(it.fileUri)
        }

        workspace.imageQuote?.let {
            Glide.with(requireContext()).load(it.fileUri).into(binding.imagePostQuote)
            binding.imagePostQuote.visibility = View.VISIBLE
            binding.motivationQuoteLayout.visibility = View.VISIBLE
            imageQuoteUri = Uri.parse(it.fileUri)

            binding.motivationQuoteInput.setText(it.fileTitle)
        }
    }

    private fun saveWorkspace() {
        binding.contentLoadingProgressBar.visibility = View.VISIBLE

        val name = binding.nameWkspInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val passCode = binding.loginCodeInput.text.toString()
        val ownerId = AuthUtils.getCurrentUserId()!!
        val quote = binding.motivationQuoteInput.text.toString()

        if (name.isEmpty() || description.isEmpty() || passCode.isEmpty()) {
            Toast.makeText(requireContext(), "All text inputs must be filled", Toast.LENGTH_SHORT)
                .show()
            return
        }


        val links = extractLinksFromRecyclerView(binding.linksRecyclerView)
        val documents = extractFileTitlesFromPdfRecyclerView(binding.documentsRecyclerView)

        lifecycleScope.launch {
            if (workspaceId == null) {
                workspaceId = firestore.collection(WORKSPACE_COLLECTION).document().id

                viewModel.saveWorkspace(
                    workspaceId,
                    name,
                    description,
                    passCode,
                    ownerId,
                    links,
                    documents,
                    imageUri, imageQuoteUri, quote
                )
                Toast.makeText(requireContext(), "Registering new workspace", Toast.LENGTH_SHORT)
                    .show()
                saveSubcollections(workspaceId!!)

            } else {

                viewModel.saveWorkspace(
                    workspaceId,
                    name,
                    description,
                    passCode,
                    ownerId,
                    links,
                    documents,
                    imageUri, imageQuoteUri, quote, false
                )
                Toast.makeText(
                    requireContext(),
                    "Updating changes in workspace",
                    Toast.LENGTH_SHORT
                ).show()
                saveSubcollections(workspaceId!!)
            }


        }.invokeOnCompletion {
            binding.contentLoadingProgressBar.visibility = View.INVISIBLE
            dismiss()
        }
    }

    private suspend fun saveSubcollections(workspaceId: String) {
        val invitedMembers = extractPeopleFromRecyclerView(binding.invitedRecyclerView)
        val invitedAdmins: MutableList<Person> =
            extractPeopleFromRecyclerView(binding.invitedAdminsRecyclerView)

        invitedAdmins.add(currPerson!!)

        if (!invitedAdmins.map { it.userId }.contains(currentWorkspace?.ownerId)){
            Toast.makeText(
                requireContext(),
                "Workspace owner can't be removed",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        savePeopleSubcollection(workspaceId, WORKSP_MEMBERS_SUB_COLLECTION, invitedMembers)
        savePeopleSubcollection(workspaceId, WORKSP_ADMINS_SUB_COLLECTION, invitedAdmins)
    }

    private suspend fun savePeopleSubcollection(
        workspaceId: String,
        subcollection: String,
        people: MutableList<Person>
    ) {
        val subcollectionRef = firestore.collection(WORKSPACE_COLLECTION).document(workspaceId)
            .collection(subcollection)

        people.forEach {
            firestore.collection(USERS_COLLECTION).document(it.userId)
                .update("workspaces", FieldValue.arrayUnion(workspaceId))

        }

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

            setContentView(R.layout.dialog_add_workspace)
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
