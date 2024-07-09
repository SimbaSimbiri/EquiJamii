package com.simbiri.equityjamii.ui.main_activity.jamii_page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.NetworkAdapter
import com.simbiri.equityjamii.databinding.DialogLikesBinding

class LikesDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_USER_IDS = "user_ids"
        fun newInstance(likedUserIds: ArrayList<String>): LikesDialogFragment {
            val frag = LikesDialogFragment()
            val args = Bundle()
            args.putStringArrayList(ARGS_USER_IDS, likedUserIds)

            frag.arguments = args
            return frag
        }

    }

    private lateinit var dialogLikesBinding: DialogLikesBinding
    private val viewModel: LikesViewModel = LikesViewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogLikesBinding = DialogLikesBinding.inflate(inflater, container, false)

        val listIds = arguments?.getStringArrayList(ARGS_USER_IDS)

        viewModel.fetchLikesList(listIds)

        dialogLikesBinding.let { binding ->

            binding.likesPeopleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            setUpObservers()

        }

        return dialogLikesBinding.root
    }

    private fun setUpObservers() {
        viewModel.likesPeopleList.observe(viewLifecycleOwner) { likedUsers ->

            val adapter = NetworkAdapter(requireContext(), likedUsers)
            dialogLikesBinding.likesPeopleRecyclerView.adapter = adapter
            dialogLikesBinding.likesPeopleRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }


    private fun setupHalfHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        layoutParams.height = displayMetrics.heightPixels * 4/7
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_likes)
        dialog.setCanceledOnTouchOutside(true)
        val displayMetrics = DisplayMetrics()
        val windowManager =
            requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            setupHalfHeight(bottomSheet!!)
            bottomSheet.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.isHideable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }


}