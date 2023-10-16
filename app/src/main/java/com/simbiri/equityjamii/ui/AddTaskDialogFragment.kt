package com.simbiri.equityjamii.ui

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R


class AddTaskDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = AddTaskDialogFragment()
    }

    private lateinit var viewModel: AddTaskDialogViewModel
    private lateinit var addTitleText: EditText
    private lateinit var addDescriptionText: EditText
    private lateinit var addNameText: EditText
    private lateinit var buttonBranchDone: Button
    private lateinit var buttonPersonalDone: Button
    private lateinit var titleTask: String
    private lateinit var descriptionTask: String
    private lateinit var nameAssign: String


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setContentView(R.layout.add_task_dialog_fragment)
        dialog.setCanceledOnTouchOutside(true)

        dialog.setOnShowListener { dialogInterface ->

            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.peekHeight = 700
                behavior.isDraggable = true
                behavior.isHideable = true

            }
        }


        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_task_dialog_fragment, container, false)



        addTitleText = view.findViewById(R.id.editTextTitle)
        addDescriptionText = view.findViewById(R.id.editTextDescription)
        addNameText = view.findViewById(R.id.editTextAssignedTo)

        buttonBranchDone = view.findViewById(R.id.branchButtonDialogTask)
        buttonPersonalDone = view.findViewById(R.id.personalButtonDialogTask)

        buttonBranchDone.setOnClickListener {

            titleTask = addTitleText.text.toString()
            listTitleBranch.add(titleTask)

            descriptionTask = addDescriptionText.text.toString()
            listDescriptionBranch.add(descriptionTask)

            nameAssign = addNameText.text.toString()
            listAssignedToBranch.add(nameAssign)

            dismiss()

        }

        buttonPersonalDone.setOnClickListener {

            titleTask = addTitleText.text.toString()
            listTitlePersonal.add(titleTask)

            descriptionTask = addDescriptionText.text.toString()
            listDescriptionPersonal.add(descriptionTask)

            nameAssign = addNameText.text.toString()
            listAssignedToPersonal.add(nameAssign)

            dismiss()


        }


        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTaskDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
