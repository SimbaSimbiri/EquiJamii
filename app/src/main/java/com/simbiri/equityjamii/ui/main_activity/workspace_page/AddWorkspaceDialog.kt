package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.databinding.AddWorkspaceDialogBinding

class AddWorkspaceDialog : Fragment() {

    companion object {
        private const val ARGS_WORKSP_INST = "WorksP"
        fun newInstance(workspaceId: String): AddWorkspaceDialog{
            val frag = AddWorkspaceDialog()
            val bundle = Bundle()

            bundle.putString(ARGS_WORKSP_INST, workspaceId)
            frag.arguments = bundle

            return frag
        }
    }

    private val viewModel = AddWorkspaceDialogViewModel()
    private lateinit var binding : AddWorkspaceDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddWorkspaceDialogBinding.inflate(layoutInflater)

        return binding.root
    }
}