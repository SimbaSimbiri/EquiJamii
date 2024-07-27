package com.simbiri.equityjamii.adapters

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.WorkspaceMention
import com.simbiri.equityjamii.ui.main_activity.workspace_page.AddMentionFragment


class WorkspaceMentionAdapter(
    private val mentionList: MutableList<WorkspaceMention>,
    private val workspId: String
) : RecyclerView.Adapter<WorkspaceMentionAdapter.WorkspaceMentionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceMentionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_mentions_item, parent, false
        )
        return WorkspaceMentionViewHolder(view, workspId)
    }

    override fun onBindViewHolder(holder: WorkspaceMentionViewHolder, position: Int) {
        val mention = mentionList[position]
        holder.bind(mention)
    }

    override fun getItemCount(): Int {
        return mentionList.size
    }

    class WorkspaceMentionViewHolder(itemView: View, val worskpaceId: String) :
        RecyclerView.ViewHolder(itemView) {
        private val textTaskMentionView: TextView = itemView.findViewById(R.id.textTaskMentionView)
        private val editTask: ImageView = itemView.findViewById(R.id.editTask)
        private val imageProfile: ImageView = itemView.findViewById(R.id.imageMyProfile)
        private val textNameProfile: TextView = itemView.findViewById(R.id.textNameMyProfile)
        private var cardViewHolder: CardView = itemView.findViewById(R.id.cardViewMyProfile)

        fun bind(mention: WorkspaceMention) {
            adjustHolderSize()
            editTask.setOnClickListener {
                val frag = AddMentionFragment.newInstance(
                    this.worskpaceId,
                    mention.mentionId,
                    mention.recipientId
                )
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                frag.show(transaction, frag.tag)
            }

            AuthUtils.getCurrentPerson(mention.recipientId) { personRecipent ->
                personRecipent?.let {
                    Glide.with(itemView.context).load(it.profileUri).into(imageProfile)
                    textNameProfile.text = it.name
                }


                AuthUtils.getCurrentPerson(mention.appreciatorId) { personAppreciator ->
                    personAppreciator?.let {
                        textTaskMentionView.text =
                            "${it.name} ${mention.keyWordMention} ${personRecipent?.name} ${mention.mentionMainText}"

                    }
                }
            }
        }

        private fun adjustHolderSize() {
            val layoutParamsHolder = cardViewHolder.layoutParams
            val displayMetrics = DisplayMetrics()

            val windowManager = itemView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsHolder.width = (screenWidth / 3.5).toInt()
            layoutParamsHolder.height = (screenWidth / 3.5 + 50.0).toInt()

            cardViewHolder.layoutParams = layoutParamsHolder

        }

    }
}
