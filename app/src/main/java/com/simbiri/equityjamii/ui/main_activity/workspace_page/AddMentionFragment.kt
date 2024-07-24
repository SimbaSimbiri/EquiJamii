package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
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
import com.simbiri.equityjamii.constants.WORKSPACE_MENTIONS
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.WorkspaceMention
import com.simbiri.equityjamii.databinding.AddMentionFragBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            workspId = it.workspaceId

            viewModelWorkspace.membersList.observe(viewLifecycleOwner) { members ->
                membersList.addAll(members)
                listPeople.addAll(members)
                binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()
            }


            viewModelWorkspace.adminsList.observe(viewLifecycleOwner) { admins ->
                adminsList.addAll(admins)
                listPeople.addAll(admins)
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
            nameTaskInput.setText(mention.keyWordMention)
            mentionMainTextInput.setText(mention.mentionMainText)

            AuthUtils.getCurrentPerson(mention.recipientId) { person ->
                mentionPerson = person
                val listMentioned = mutableListOf(person!!)
                mentionAssigneesRecyclerView.adapter =
                    OtherProfilesAdapter(requireContext(), listMentioned, canDelete = true)

            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMentionFragBinding.inflate(layoutInflater)

        binding.apply {

            mentionAssigneesRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            searchPeopleRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            searchPeopleRecyclerView.adapter = OtherProfilesAdapter(
                requireContext(),
                listPeople,
                editingMention = true
            ) { person, flag ->
                if (flag == 3) {
                    mentionPerson = person
                    mentionAssigneesRecyclerView.adapter!!.notifyItemChanged(0, person)
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
                    }
                }
            }

            saveMention.setOnClickListener {
                contentLoadingProgressBar.visibility = View.VISIBLE
                val keyword = nameTaskInput.text.toString()
                val mainText = mentionMainTextInput.text.toString()
                val recipientId = mentionPerson?.userId
                val appreciatorId = AuthUtils.getCurrentUserId()

                val workspDoc = workspCollection.document(workspId!!)

                if (keyword.isNotEmpty() || mainText.isNotEmpty()) {
                    lifecycleScope.launch {
                        if (mentionId == null) {
                            mentionId = workspDoc.collection(WORKSPACE_MENTIONS).document().id

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

                    }
                }

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
        val fullList = membersList + adminsList

        val filteredList = if (text.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name.contains(text, ignoreCase = true) }
                .toMutableList()
        }

        listPeople.clear()
        listPeople.addAll(filteredList)
        binding.searchPeopleRecyclerView.adapter!!.notifyDataSetChanged()

    }
}