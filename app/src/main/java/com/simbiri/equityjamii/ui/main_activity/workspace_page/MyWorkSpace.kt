package com.simbiri.equityjamii.ui.main_activity.workspace_page

import WorkspaceAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.MyWorkspacePageBinding

class MyWorkspace : Fragment() {

    companion object {
        fun newInstance() = MyWorkspace()
    }

    private val viewModel: WorkspaceViewModel by viewModels()
    private lateinit var binding: MyWorkspacePageBinding
    private lateinit var workspaceAdapter: WorkspaceAdapter
    private var filteredWorkspaceList = mutableListOf<Workspace>()
    private var workspaceList = mutableListOf<Workspace>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyWorkspacePageBinding.inflate(layoutInflater)

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        binding.contentLoadingProgressBar.visibility = View.VISIBLE

        workspaceAdapter = WorkspaceAdapter(requireContext(), filteredWorkspaceList)
        binding.myWorkspacesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.myWorkspacesRecyclerView.adapter = workspaceAdapter

        binding.createWorkspacesCard.setOnClickListener {
            val addWorkspaceDialog = AddWorkspaceDialog()
            addWorkspaceDialog.show(parentFragmentManager, addWorkspaceDialog.tag)
        }

        binding.searchViewWorkspaces.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText ?: "")
                return true
            }
        })
    }
    private fun filter(text: String) {
        val filteredList = if (text.isEmpty()) {
            workspaceList
        } else {
            workspaceList.filter { it.titleImage!!.fileTitle.contains(text, ignoreCase = true) }
        }

        filteredWorkspaceList.clear()
        filteredWorkspaceList.addAll(filteredList)
        workspaceAdapter.notifyDataSetChanged()
    }
    private fun observeViewModel() {
        viewModel.workspaces.observe(viewLifecycleOwner) { workspaces ->

        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        viewModel.invitedWorkspaces.observe(viewLifecycleOwner) { invitedWorkspaces ->
            if (invitedWorkspaces.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "No invited or created workspace found",
                    Toast.LENGTH_SHORT
                ).show()
            }
            workspaceList.clear()
            workspaceList.addAll(invitedWorkspaces)
            filter("")
            binding.contentLoadingProgressBar.visibility = View.INVISIBLE

        }
    }
}
