package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.databinding.MyWorkspacePageBinding


class MyWorkspace : Fragment() {

    companion object {
        fun newInstance() = MyWorkspace()
    }

    private lateinit var viewModel: WorkspaceViewModel

    private lateinit var binding: MyWorkspacePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyWorkspacePageBinding.inflate(layoutInflater)


        AuthUtils.getCurrentPerson { currentPerson ->
            if (currentPerson == null) {
                Toast.makeText(
                    requireContext(),
                    "Set up profile to access EquiJamii features",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WorkspaceViewModel::class.java)
        // TODO: Use the ViewModel
    }


}

