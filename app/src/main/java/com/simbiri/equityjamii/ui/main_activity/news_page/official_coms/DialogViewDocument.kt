package com.simbiri.equityjamii.ui.main_activity.news_page.official_coms

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.databinding.DialogViewDocumentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class DialogViewDocument : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_PDF_FILE = "pdf_file"

        fun newInstance(pdf: FileTitle): DialogViewDocument {
            val frag = DialogViewDocument()
            val args = Bundle()
            args.putParcelable(ARG_PDF_FILE, pdf)
            frag.arguments = args

            return frag
        }
    }

    private val viewModel: DialogViewDocumentViewModel by viewModels()
    private lateinit var binding: DialogViewDocumentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogViewDocumentBinding.inflate(layoutInflater)

        arguments?.getParcelable<FileTitle>(ARG_PDF_FILE).let { pdfParceled ->
            binding.apply {
                lifecycleScope.launch(Dispatchers.IO) {
                    val inputStream = URL(pdfParceled?.fileUri).openStream()

                    withContext(Dispatchers.Main) {
                        pdfViewer.fromStream(inputStream).onRender { pages ->
                            if (pages >= 1) {
                                progressBar.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Corrupted file, can't open pdf",
                                    Toast.LENGTH_LONG
                                ).show()

                                dismiss()
                            }
                        }.load()
                    }
                }

                downloadPdf.setOnClickListener {

                }
            }
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialog_view_document)
        dialog.setCanceledOnTouchOutside(true)

        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = metrics.heightPixels * 8 / 10
                it.requestLayout()

                val behavior = BottomSheetBehavior.from(it)
                behavior.isDraggable = false
                behavior.isHideable = true
                behavior.peekHeight = metrics.heightPixels * 8 / 10
            }
        }

        return dialog
    }

}