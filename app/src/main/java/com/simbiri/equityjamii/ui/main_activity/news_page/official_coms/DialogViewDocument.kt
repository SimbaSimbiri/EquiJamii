package com.simbiri.equityjamii.ui.main_activity.news_page.official_coms

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.UnknownHostException

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
    private lateinit var downloadManager: DownloadManager
    private val PERMISSION_REQUEST_CODE = 1001
    private var pdfParceledMain: FileTitle? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogViewDocumentBinding.inflate(layoutInflater)
        downloadManager = requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        arguments?.getParcelable<FileTitle>(ARG_PDF_FILE).let { pdfParceled ->
            pdfParceledMain = pdfParceled
            binding.apply {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val inputStream = URL(pdfParceled?.fileUri).openStream()
                        withContext(Dispatchers.Main) {
                            pdfViewer.fromStream(inputStream).onRender { pages ->
                                if (pages >= 1) {
                                    progressBar.visibility = View.GONE
                                } else {
                                    showError("Corrupted file, can't open PDF")
                                    dismiss()
                                }
                            }.load()
                        }
                    } catch (e: UnknownHostException) {
                        withContext(Dispatchers.Main) {
                            showError("Network error. Please check your connection.")
                            dismiss()
                        }
                    } catch (e: IOException) {
                        withContext(Dispatchers.Main) {
                            showError("Error opening PDF file: ${e.message}")
                            dismiss()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            showError("An unexpected error occurred: ${e.message}")
                            dismiss()
                        }
                    }
                }
                downloadPdf.setOnClickListener {
                    if (checkAndReqPermision()) {
                        downloadPdf(pdfParceled?.fileUri, pdfParceled?.fileTitle)
                    }
                }
            }
        }
        return binding.root
    }

    private fun showError(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show()
    }

    private fun checkAndReqPermision(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val writeExternalStoragePermission = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val listPermissionsNeeded = mutableListOf<String>()

            if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (listPermissionsNeeded.isNotEmpty()) {
                requestPermissions(listPermissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadPdf(pdfParceledMain?.fileUri, pdfParceledMain?.fileTitle)
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun downloadPdf(fileUri: String?, fileTitle: String?) {
        try {
            val request = DownloadManager.Request(Uri.parse(fileUri))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(true)
                .setTitle(fileTitle)
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + fileTitle
                )

            downloadManager.enqueue(request)
            Toast.makeText(
                requireContext(),
                "File downloading, you will be notified once completed",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                e.message,
                Toast.LENGTH_LONG
            ).show()

        }
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