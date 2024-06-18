package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.AddOfficialDialog
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.DialogDocumentsFragment


class OfficialAdapter(var context: Context, var officialNewsList: MutableList<NewsText>, var editable : Boolean = false) : RecyclerView.Adapter<OfficialAdapter.OfficialAdapterViewHolder>(){

    inner class OfficialAdapterViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        private var positionItem = 1
        private var currentOfficialItem : NewsText? = null

        private var textHeadlineOfficial : TextView = itemView.findViewById(R.id.titleOfficialItem)
        private var textPreviewOfficial : TextView = itemView.findViewById(R.id.previewOfficialItem)
        private var editNewsImageView: ImageView = itemView.findViewById(R.id.editNews)
        private var viewAttachmentsTv : TextView =  itemView.findViewById(R.id.viewAttachmentsText)
        private var viewAttachmentsImg : ImageView =  itemView.findViewById(R.id.viewAttachmentsImage)


        fun setDatatoItem( officialNewsInstance: NewsText,  position: Int ) {
            this.positionItem = position
            this.currentOfficialItem = officialNewsInstance

            textHeadlineOfficial.text = officialNewsInstance.title
            textPreviewOfficial.text = officialNewsInstance.allNews

            viewAttachmentsImg.setOnClickListener {
                displayAttachments(currentOfficialItem!!.fileTitleList)

            }

            viewAttachmentsTv.setOnClickListener {
                displayAttachments(currentOfficialItem!!.fileTitleList)
            }

        }

        private fun displayAttachments(fileTitleList: MutableList<FileTitle>) {
            val dialogDocumentsFragment =  DialogDocumentsFragment.newInstance(ArrayList(fileTitleList))
            val transaction = (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            dialogDocumentsFragment.show(transaction, dialogDocumentsFragment.tag)

        }

        fun editNews(editable: Boolean) {
            if (editable) {
                editNewsImageView.visibility = View.VISIBLE
                editNewsImageView.setOnClickListener {
                    val editNewsFrag = AddOfficialDialog.newInstance(this.currentOfficialItem)
                    val transaction =
                        (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    editNewsFrag.show(transaction, editNewsFrag.tag)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfficialAdapterViewHolder {
        val view =  LayoutInflater.from(context).inflate(R.layout.adapters_official_item, parent,false)

        return  OfficialAdapterViewHolder(view)    }

    override fun onBindViewHolder(officialNewsholder: OfficialAdapterViewHolder, position: Int) {

        val officialNewsInstance : NewsText = officialNewsList[position]
        officialNewsholder.setDatatoItem(officialNewsInstance, position)
        officialNewsholder.editNews(editable)
    }

    override fun getItemCount(): Int  = officialNewsList.size

}