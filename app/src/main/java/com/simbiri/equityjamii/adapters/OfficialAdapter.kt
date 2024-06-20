package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.AddOfficialDialog
import com.simbiri.equityjamii.ui.main_activity.news_page.official_coms.DialogDocumentsFragment
import java.text.SimpleDateFormat
import java.util.Locale


class OfficialAdapter(
    var context: Context,
    var officialNewsList: MutableList<NewsText>,
    var editable: Boolean = false
) : RecyclerView.Adapter<OfficialAdapter.OfficialAdapterViewHolder>() {

    inner class OfficialAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var positionItem = 1
        private var currentOfficialItem: NewsText? = null

        private var textHeadlineOfficial: TextView = itemView.findViewById(R.id.titleOfficialItem)
        private var textPreviewOfficial: TextView = itemView.findViewById(R.id.previewOfficialItem)
        private var editNewsImageView: ImageView = itemView.findViewById(R.id.editNews)
        private var viewAttachmentsTv: TextView = itemView.findViewById(R.id.viewAttachmentsText)
        private var viewAttachmentsImg: ImageView = itemView.findViewById(R.id.viewAttachmentsImage)
        private var publisherDateTv: TextView = itemView.findViewById(R.id.datePublisherTv)
        private var isContentExpanded = false
        private val MAX_CHAR_COLLAPSED = 400

        fun setDatatoItem(officialNewsInstance: NewsText, position: Int) {
            this.positionItem = position
            this.currentOfficialItem = officialNewsInstance

            /*
                        setTextsToggled(currentOfficialItem!!.allNews, isDescriptionExpanded)
            */

            textHeadlineOfficial.text = officialNewsInstance.title
            textPreviewOfficial.text = officialNewsInstance.allNews

            publisherDateTv.text =
                "disseminated ${displayDate(currentOfficialItem?.time)} by ${currentOfficialItem?.author}"

            viewAttachmentsImg.setOnClickListener {
                displayAttachments(currentOfficialItem!!.fileTitleList)

            }

            viewAttachmentsTv.setOnClickListener {
                displayAttachments(currentOfficialItem!!.fileTitleList)
            }

        }

        private fun displayDate(timestamp: Timestamp?): String? {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            return try {
                val date = timestamp?.toDate()
                val currentDate = java.util.Date()

                val timeDifference = currentDate.time - date!!.time
                val daysDifference = timeDifference / (1000 * 60 * 60 * 24)

                when {
                    daysDifference >= 1 -> dateFormat.format(date)
                    timeDifference >= 60 * 60 * 1000 -> "${timeDifference / (60 * 60 * 1000)}h ago"
                    timeDifference >= (60 * 1000).toLong() -> "${timeDifference / (60 * 1000)}m ago"
                    else -> "just now"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Couldn't display date"
            }
        }


        private fun displayAttachments(fileTitleList: MutableList<FileTitle>) {
            val dialogDocumentsFragment =
                DialogDocumentsFragment.newInstance(ArrayList(fileTitleList))
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
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
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_official_item, parent, false)

        return OfficialAdapterViewHolder(view)
    }

    override fun onBindViewHolder(officialNewsholder: OfficialAdapterViewHolder, position: Int) {

        val officialNewsInstance: NewsText = officialNewsList[position]
        officialNewsholder.setDatatoItem(officialNewsInstance, position)
        officialNewsholder.editNews(editable)
    }

    override fun getItemCount(): Int = officialNewsList.size

}