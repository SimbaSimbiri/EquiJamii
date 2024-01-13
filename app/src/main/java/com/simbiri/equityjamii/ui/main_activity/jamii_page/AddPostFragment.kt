package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.databinding.DialogAddPostBinding

class AddPostFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = AddPostFragment()
    }

    private lateinit var viewModel: AddPostViewModel
    private  var  _binding  :DialogAddPostBinding? = null
    private val binding get() = _binding
    private lateinit var  openPostPicker: ActivityResultLauncher<CropImageContractOptions>



    override fun onAttach(context: Context) {
        super.onAttach(context)
        openPostPicker = registerForActivityResult(CropImageContract()){ result ->
            if (result.isSuccessful){
                Glide.with(requireContext()).load(result.uriContent).into(binding!!.imagePostUpload)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  DialogAddPostBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root


        val cropPostContractOptions = CropImageContractOptions(
            null, CropImageOptions(true, true, CropImageView.CropShape.RECTANGLE, cropCornerRadius = 8.0F,
                cropMenuCropButtonTitle = "Done", showCropLabel = true, activityTitle = "CROP IMAGE", activityBackgroundColor = requireContext().resources.getColor(R.color.black),
                toolbarColor = requireContext().resources.getColor(R.color.black), progressBarColor = requireContext().resources.getColor(R.color.karbBackgrndtint),
                guidelines = CropImageView.Guidelines.OFF, fixAspectRatio = true)
        )

        binding!!.dismissFrag.setOnClickListener {
            dismiss()
        }
        binding!!.addPicturePost.setOnClickListener {
            openPostPicker.launch(cropPostContractOptions)
        }

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_add_post)
        dialog.setCanceledOnTouchOutside(false)


        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet!!.layoutParams.height = metrics.heightPixels
            bottomSheet.requestLayout()

            bottomSheet.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.peekHeight =  metrics.heightPixels
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddPostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}