package com.simbiri.equityjamii.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.DialogViewDocument

class PdfDescAdapter(
    private val context: Context,
    val fileTitleList: MutableList<FileTitle>, var editable: Boolean = false
) : RecyclerView.Adapter<PdfDescAdapter.PdfDescViewHolder>() {

    inner class PdfDescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textPdfName: TextView = itemView.findViewById(R.id.textPdfName)
        val deletePdf: ImageView = itemView.findViewById(R.id.deletePdf)

        fun displayPdf(pdf: FileTitle) {

            val viewCurDoc = DialogViewDocument.newInstance(pdf)
            val itemContext = itemView.context
            if (itemContext is AppCompatActivity) {
                val transaction = itemContext.supportFragmentManager.beginTransaction()
                viewCurDoc.show(transaction, viewCurDoc.tag)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfDescViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapters_pdf_item, parent, false)
        return PdfDescViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfDescViewHolder, position: Int) {
        val pdfDesc = fileTitleList[position]
        holder.textPdfName.text = pdfDesc.fileTitle

        holder.textPdfName.setOnClickListener {
            if (pdfDesc.fileUri.contains("https://")) {
                holder.displayPdf(pdfDesc)
            } else {
                Toast.makeText(
                    context,
                    "Can't view pdf before upload,\nkindly use local pdf viewer to access contents",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (editable) {
            holder.deletePdf.visibility = View.VISIBLE

            holder.deletePdf.setOnClickListener {
                deletePdfDesc(pdfDesc, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return fileTitleList.size
    }

    private fun deletePdfDesc(fileTitle: FileTitle, position: Int) {
        fileTitleList.remove(fileTitle)
        notifyItemRemoved(position)
    }

    fun addPdfDesc(fileTitle: FileTitle) {
        if (fileTitleList.size < 5) {
            fileTitleList.add(fileTitle)
            notifyItemInserted(fileTitleList.size - 1)
        }
    }
}
