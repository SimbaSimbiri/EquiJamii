package com.simbiri.equityjamii.adapters

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_MENTIONS_SUB_COLLECTIONS
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.WorkspaceMention
import com.simbiri.equityjamii.ui.main_activity.workspace_page.AddMentionFragment


class WorkspaceMentionAdapter( private val context: Context,
    private val mentionList: MutableList<WorkspaceMention>,
    private val workspaceId: String, private val canDelete : Boolean = false
) : RecyclerView.Adapter<WorkspaceMentionAdapter.WorkspaceMentionViewHolder>() {

    private val workspaceCollection = FirebaseFirestore.getInstance().collection(
        WORKSPACE_COLLECTION
    )
    private val workspaceMentionCollection = workspaceCollection.document(workspaceId).collection(
        WORKSPACE_MENTIONS_SUB_COLLECTIONS
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceMentionViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.adapter_mentions_item, parent, false
        )
        return WorkspaceMentionViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkspaceMentionViewHolder, position: Int) {
        val mention = mentionList[position]
        holder.bind(mention,position)
    }

    override fun getItemCount(): Int {
        return mentionList.size
    }

    inner class WorkspaceMentionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textMentionView: TextView = itemView.findViewById(R.id.textTaskMentionView)
        private val editMention: ImageView = itemView.findViewById(R.id.editTask)
        private val imageProfile: ImageView = itemView.findViewById(R.id.imageMyProfile)
        private var cardViewHolder: CardView = itemView.findViewById(R.id.cardViewMyProfile)
        private var mentionDelete : ImageView = itemView.findViewById(R.id.deleteMentionIcon)
        private fun removeMentionFromSpace(mention: WorkspaceMention) {

            val mentionDoc = workspaceMentionCollection.document(mention.mentionId!!)
            mentionDoc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        mentionDoc.delete()
                    } else {
                        Toast.makeText(
                            itemView.context,
                            "Error removing mention from worksp",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

        fun bind(mention: WorkspaceMention, position: Int) {

            if (canDelete || AuthUtils.getCurrentUserId().contentEquals(mention.appreciatorId)){
                mentionDelete.visibility =  View.VISIBLE
                editMention.visibility = View.VISIBLE

                mentionDelete.setOnClickListener {
                    removeMentionFromSpace(mention)
                    mentionList.remove(mention)
                    notifyItemRemoved(position)
                }
                editMention.setOnClickListener {
                    val frag = AddMentionFragment.newInstance(
                        workspaceId,
                        mention.mentionId,
                        mention.recipientId
                    )
                    val transaction =
                        (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    frag.show(transaction, frag.tag)
                }
            }

            adjustHolderSize()

            AuthUtils.getCurrentPerson(mention.recipientId) { personRecipent ->
                personRecipent?.let {
                    Glide.with(itemView.context).load(it.profileUri).into(imageProfile)
                }


                AuthUtils.getCurrentPerson(mention.appreciatorId) { personAppreciator ->
                    personAppreciator?.let {
                        textMentionView.text =
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
            layoutParamsHolder.width = (screenWidth / 5.5).toInt()
            layoutParamsHolder.height = (screenWidth / 5.5).toInt()

            cardViewHolder.layoutParams = layoutParamsHolder

        }

    }
}
