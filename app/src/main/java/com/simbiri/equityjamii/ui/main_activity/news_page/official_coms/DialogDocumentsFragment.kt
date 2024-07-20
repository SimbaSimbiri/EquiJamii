package com.simbiri.equityjamii.ui.main_activity.news_page.official_coms

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.adapters.PdfDescAdapter
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.databinding.DialogDocumentsBinding

class DialogDocumentsFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARGS_FILE_LIST = "file_list"
        fun newInstance(fileList: ArrayList<FileTitle>): DialogDocumentsFragment {
            val frag = DialogDocumentsFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARGS_FILE_LIST, fileList)

            frag.arguments = args


            return frag
        }
    }

    private val viewModel: DialogDocumentsViewModel by viewModels()
    private lateinit var binding: DialogDocumentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDocumentsBinding.inflate(layoutInflater)

        val fileLists = arguments?.getParcelableArrayList<FileTitle>(ARGS_FILE_LIST)

        fileLists.let { listFiles ->
            if (listFiles != null) {
                listFiles.sortBy { fileTitle -> fileTitle.position}
                binding.apply {

                    documentsRecyclerView.apply {
                        adapter = PdfDescAdapter(requireContext(), listFiles.toMutableList())
                        layoutManager = LinearLayoutManager(context)
                        adapter!!.notifyDataSetChanged()
                    }

                    Handler().postDelayed({

                        if (listFiles.count() == 1) {
                            val viewHolder =
                                documentsRecyclerView.findViewHolderForAdapterPosition(0) as? PdfDescAdapter.PdfDescViewHolder
                            viewHolder?.displayPdf(listFiles.first())
                        }

                    }, 500)

                }

            }

        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_documents)
        dialog.setCanceledOnTouchOutside(true)

        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.isDraggable = true
                behavior.isHideable = true
            }
        }

        return dialog
    }

}