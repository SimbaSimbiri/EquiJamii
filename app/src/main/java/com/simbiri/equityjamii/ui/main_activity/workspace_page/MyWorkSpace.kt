package com.simbiri.equityjamii.ui.main_activity.workspace_page

import WorkspaceAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.MyWorkspacePageBinding

class MyWorkspace : Fragment() {

    companion object {
        fun newInstance() = MyWorkspace()
    }

    private val viewModel: WorkspaceViewModel by viewModels()
    private lateinit var binding: MyWorkspacePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyWorkspacePageBinding.inflate(layoutInflater)

        AuthUtils.getCurrentPerson(AuthUtils.getCurrentUserId()!!) { currentPerson ->
            if (currentPerson == null) {
                Toast.makeText(
                    requireContext(),
                    "Set up profile to access EquiJamii features",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.textKaribu.text = "Karibu kazi ${currentPerson.name}"

                setupUI()
                observeViewModel()
            }
        }


        return binding.root
    }

    private fun setupUI() {
        binding.contentLoadingProgressBar.visibility =  View.VISIBLE
        binding.myWorkspacesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.createWorkspacesCard.setOnClickListener {
            val addWorkspaceDialog = AddWorkspaceDialog()
            addWorkspaceDialog.show(parentFragmentManager, addWorkspaceDialog.tag)
        }
    }

    private fun observeViewModel() {
        viewModel.workspaces.observe(viewLifecycleOwner) { workspaces ->

        }

        viewModel.invitedWorkspaces.observe(viewLifecycleOwner) { invitedWorkspaces ->
            binding.myWorkspacesRecyclerView.adapter =
                WorkspaceAdapter(requireContext(), invitedWorkspaces)
            binding.contentLoadingProgressBar.visibility =  View.INVISIBLE

        }
    }
}
