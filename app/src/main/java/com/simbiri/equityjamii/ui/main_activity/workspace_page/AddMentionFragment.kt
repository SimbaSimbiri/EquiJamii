package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.OtherProfilesAdapter
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_MENTIONS_SUB_COLLECTIONS
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.WorkspaceMention
import com.simbiri.equityjamii.databinding.AddMentionFragBinding
import kotlinx.coroutines.launch

class AddMentionFragment : BottomSheetDialogFragment(), SearchView.OnQueryTextListener {

    companion object {
        private const val ARGS_WORKSP_ID = "workspid"
        private const val ARGS_MENTION_ID = "mentionId"
        private const val ARGS_RECIPIENT_ID = "appreciatorId"
        fun newInstance(
            workspId: String?,
            mentionId: String?,
            recipientId: String?
        ): AddMentionFragment {
            val frag = AddMentionFragment()
            val bundle = Bundle()
            bundle.putString(ARGS_WORKSP_ID, workspId)
            bundle.putString(ARGS_MENTION_ID, mentionId)
            bundle.putString(ARGS_RECIPIENT_ID, recipientId)

            frag.arguments = bundle

            return frag
        }
    }

    private val viewModel: AddMentionViewModel by viewModels()
    private val viewModelWorkspace = AddWorkspaceDialogViewModel()
    private var workspId: String? = null
    private var mentionId: String? = null
    private val listPeople = mutableListOf<Person>()
    private var mentionPerson: Person? = null
    private lateinit var mentionCur: WorkspaceMention
    private lateinit var binding: AddMentionFragBinding
    private val firebaseStorage = FirebaseFirestore.getInstance()
    private val workspCollection = firebaseStorage.collection(WORKSPACE_COLLECTION)
    private var adminsList = mutableListOf<Person>()
    private var membersList = mutableListOf<Person>()
    private var listMentioned = mutableListOf<Person>()
    private var currentPerson: Person? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.apply {

            setContentView(R.layout.add_mention_frag)
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

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    private fun observeViewModel() {

        viewModelWorkspace.workspace.observe(viewLifecycleOwner) {

            viewModelWorkspace.membersList.observe(viewLifecycleOwner) { members ->
                membersList.addAll(members)
                listPeople.addAll(members)
                Log.i("listmemmbers", listPeople.toString())

                binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()
            }


            viewModelWorkspace.adminsList.observe(viewLifecycleOwner) { admins ->
                adminsList.addAll(admins)
                listPeople.addAll(admins)
                Log.i("listadmins", listPeople.toString())

                binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()
            }


        }

        viewModel.mention.observe(viewLifecycleOwner) { mention ->
            mentionCur = mention
            populateUI(mention)
        }


    }

    private fun populateUI(mention: WorkspaceMention) {
        binding.apply {
            keyWordInput.setText(mention.keyWordMention)
            mentionMainTextInput.setText(mention.mentionMainText)

            AuthUtils.getCurrentPerson(mention.recipientId) { person ->
                mentionPerson = person
                listMentioned.add(person!!)

            }

        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMentionFragBinding.inflate(layoutInflater)
        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()) { person ->
            currentPerson = person
        }

        binding.apply {

            mentionAssigneesRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mentionAssigneesRecyclerView.adapter =
                OtherProfilesAdapter(requireContext(), listMentioned, canDelete = true)

            searchPeopleRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            searchPeopleRecyclerView.adapter = OtherProfilesAdapter(
                requireContext(),
                listPeople,
                editingMention = true
            ) { person, flag ->
                if (flag == 3) {
                    listMentioned.clear()
                    listMentioned.add(person)
                    mentionAssigneesRecyclerView.adapter!!.notifyDataSetChanged()
                    updateMentionPreview()
                }
            }

            lifecycleScope.launch {

                arguments.let {
                    workspId = it?.getString(ARGS_WORKSP_ID)
                    mentionId = it?.getString(ARGS_MENTION_ID)
                    workspId?.let { it1 ->
                        mentionId?.let { it2 ->
                            viewModel.retrieveMention(
                                it1,
                                it2
                            )
                        }
                        viewModelWorkspace.loadWorkspace(it1)
                    }
                    observeViewModel()

                }
            }

            saveMention.setOnClickListener {
                mentionPerson = listMentioned.firstOrNull()
                saveMentionFirebase()
            }

        }
        binding.searchViewAll.setOnQueryTextListener(this)
        setupTextChangedListeners()


        return binding.root
    }

    private fun setupTextChangedListeners() {
        binding.apply {
            keyWordInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateMentionPreview()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            mentionMainTextInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateMentionPreview()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }

    private fun updateMentionPreview() {

        binding.apply {
            mentionActualTv.text =
                "${currentPerson?.name ?: ""} ${keyWordInput.text.toString()} ${listMentioned.firstOrNull()?.name ?: ""} ${mentionMainTextInput.text.toString()}"
        }

    }

    private fun saveMentionFirebase() {

        binding.apply {
            contentLoadingProgressBar.visibility = View.VISIBLE
            val keyword = keyWordInput.text.toString()
            val mainText = mentionMainTextInput.text.toString()
            val recipientId = mentionPerson?.userId
            val appreciatorId = AuthUtils.getCurrentUserId()

            val workspDoc = workspCollection.document(workspId!!)

            if (keyword.isNotEmpty() && mainText.isNotEmpty() && recipientId != null) {
                lifecycleScope.launch {
                    if (mentionId == null) {
                        mentionId =
                            workspDoc.collection(WORKSPACE_MENTIONS_SUB_COLLECTIONS).document().id

                        viewModel.saveMentionToFirebase(
                            keyword,
                            mainText,
                            recipientId,
                            appreciatorId, workspId!!, mentionId!!, true
                        )
                        Toast.makeText(
                            requireContext(),
                            "Registering mention",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.saveMentionToFirebase(
                            keyword,
                            mainText,
                            recipientId,
                            appreciatorId, workspId!!, mentionId!!, false
                        )
                        Toast.makeText(requireContext(), "Updating mention", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.invokeOnCompletion {
                    contentLoadingProgressBar.visibility = View.INVISIBLE
                    dismiss()

                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please include all fields or select teammate",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.contentLoadingProgressBar.visibility = View.INVISIBLE
            }


        }
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

        listPeople.clear()
        listPeople.addAll(filteredList)
        Log.i("list after filtered", listPeople.toString())

        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

    }
}