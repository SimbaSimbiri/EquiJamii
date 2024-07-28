package com.simbiri.equityjamii.adapters

import android.content.Context
import android.net.Uri
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
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.ui.main_activity.people_page.PersonInfoFragment

class OtherProfilesAdapter(
    var context: Context,
    var peopleList: MutableList<Person>,
    var canDelete: Boolean = false,
    var editingWksp: Boolean = false,
    var addingWkspAdmins: Boolean = false,
    var editingTask: Boolean = false,
    var editingMention : Boolean = false,
    private val onInviteClick: (person: Person, flag : Int) -> Unit = { _, _ -> }

) :
    RecyclerView.Adapter<OtherProfilesAdapter.OtherProfViewHolder>() {

    inner class OtherProfViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview),
        View.OnClickListener {

        private var positionItem = 1
        private var currentPerson: Person? = null
        private var isInviteVisible: Boolean = false

        private var cardViewHolder: CardView = itemView.findViewById(R.id.cardViewOtherProfiles)
        private var profilePicImageView: ImageView = itemView.findViewById(R.id.imageOtherProfiles)
        private var namePersonTextView: TextView = itemView.findViewById(R.id.textNameOtherProfiles)
        private var deleteIconView: ImageView = itemView.findViewById(R.id.deleteUserIcon)
        var cardInviteMember: CardView = itemView.findViewById(R.id.cardInviteMember)
        var cardInviteAdmin: CardView = itemView.findViewById(R.id.cardInviteAdmin)
        var cardAddMention: CardView = itemView.findViewById(R.id.cardAddMention)
        var cardAddTask: CardView = itemView.findViewById(R.id.cardAddTask)


        fun setOnClickListeners() {
            itemView.setOnClickListener(this@OtherProfViewHolder)
        }

        override fun onClick(v: View?) {
            if (editingTask) {
                cardAddTask.visibility =  View.VISIBLE
                isInviteVisible = !isInviteVisible

                if (isInviteVisible){
                    cardAddTask.visibility = View.VISIBLE
                }else{
                    cardAddTask.visibility = View.INVISIBLE
                }

            } else if (editingWksp || addingWkspAdmins) {

                isInviteVisible = !isInviteVisible

                if (isInviteVisible){
                    cardInviteAdmin.visibility = View.VISIBLE
                    cardInviteMember.visibility = View.VISIBLE
                }else{
                    cardInviteAdmin.visibility = View.INVISIBLE
                    cardInviteMember.visibility = View.INVISIBLE
                }


            } else if (editingMention){
                isInviteVisible = !isInviteVisible

                if (isInviteVisible){
                    cardAddMention.visibility = View.VISIBLE
                } else{
                    cardAddMention.visibility = View.VISIBLE
                }
            }
            else {
                val personDialogFrag = PersonInfoFragment.newInstance(currentPerson!!)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                personDialogFrag.show(transaction, personDialogFrag.tag)

            }

        }


        fun setDatatoItem(personInstance: Person, position: Int) {

            this.positionItem = position
            this.currentPerson = personInstance
            adjustHolderSize()

            if (canDelete) {
                deleteIconView.visibility = View.VISIBLE

                deleteIconView.setOnClickListener {
                    peopleList.remove(personInstance)
                    notifyItemRemoved(position)
                    /*if (editingTask) {
                        removeMemberFromTask()
                    }
                    if (editingWksp) {
                        removeMemberFromWorkSpace()
                    }

                    if (editingMention){
                        removeMentionFromSpace()
                    }*/
                }
            }

            cardInviteAdmin.setOnClickListener {
                onInviteClick(personInstance, 1)
                cardInviteAdmin.visibility = View.INVISIBLE
                cardInviteMember.visibility = View.INVISIBLE

            }

            cardInviteMember.setOnClickListener {
                onInviteClick(personInstance,2)
                cardInviteAdmin.visibility = View.INVISIBLE
                cardInviteMember.visibility = View.INVISIBLE

            }

            cardAddMention.setOnClickListener {
                onInviteClick(personInstance, 3)
                cardAddMention.visibility = View.INVISIBLE
            }

            cardAddTask.setOnClickListener {
                onInviteClick(personInstance, 4)
                cardAddTask.visibility = View.INVISIBLE
            }

            Glide.with(itemView)
                .load(Uri.parse(currentPerson!!.profileUri))
                .fitCenter().into(profilePicImageView)

            namePersonTextView.text = currentPerson!!.name
        }

/*
        private fun removeMentionFromSpace() {

            val memberTaskDoc = workspaceMentionCollection.document(currentPerson!!.userId)
            memberTaskDoc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        memberTaskDoc.delete()
                    } else {
                        Toast.makeText(
                            context,
                            "Error removing user from task",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }


        private fun removeMemberFromWorkSpace() {

            val currWkspSubCollection = workspaceCollection.document(taskWorkspId!!).collection(
                WORKSP_MEMBERS_SUB_COLLECTION
            )
            val memberTaskDoc = currWkspSubCollection.document(currentPerson!!.userId)

            memberTaskDoc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        memberTaskDoc.delete()
                    } else {
                        Toast.makeText(
                            context,
                            "Error removing user from Workspace",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }

        private fun removeMemberFromTask() {

            val currTaskSubCollection = taskCollection.document(taskWorkspId!!).collection(
                TASK_ASSIGNEES_SUB_COLLECTION
            )

            val memberTaskDoc = currTaskSubCollection.document(currentPerson!!.userId)
            memberTaskDoc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        memberTaskDoc.delete()
                    } else {
                        Toast.makeText(
                            context,
                            "Error removing user from task",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
*/

        private fun adjustHolderSize() {
            val layoutParamsHolder = cardViewHolder.layoutParams
            val displayMetrics = DisplayMetrics()

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsHolder.width = (screenWidth / 3.5).toInt()
            layoutParamsHolder.height = (screenWidth / 3.5 + 50.0).toInt()

            cardViewHolder.layoutParams = layoutParamsHolder

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherProfViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.adapter_similar_profiles_item, parent, false)

        return OtherProfViewHolder(view)
    }

    override fun onBindViewHolder(otherProfHolder: OtherProfViewHolder, position: Int) {
        val personInstance = peopleList[position]

        otherProfHolder.setDatatoItem(personInstance, position)
        otherProfHolder.setOnClickListeners()



    }

    override fun getItemCount(): Int = peopleList.size

}