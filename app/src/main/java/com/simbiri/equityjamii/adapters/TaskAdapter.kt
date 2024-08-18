package com.example.app.adapters

import android.content.Context
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.ui.main_activity.workspace_page.AddTaskFragment
import com.simbiri.equityjamii.ui.main_activity.workspace_page.ViewTaskFragment

class TaskAdapter(
    var context: Context,
    var taskList: MutableList<Task>,
    val workspaceId: String,
    val canEdit: Boolean
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapter_tasks_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task, position)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val workspaceDoc =
        firebaseFirestore.collection(WORKSPACE_COLLECTION).document(workspaceId)

    inner class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textTaskMentionView: TextView = itemView.findViewById(R.id.textTaskMentionView)
        private val progressMilestoneTv: TextView = itemView.findViewById(R.id.progressMilestoneTv)
        private val progressTask: LinearProgressIndicator = itemView.findViewById(R.id.progressTask)
        private val editTask: ImageView = itemView.findViewById(R.id.editTask)
        private val imageMyProfile: ImageView = itemView.findViewById(R.id.imageMyProfile)
        private var cardViewHolder: CardView = itemView.findViewById(R.id.cardViewMyProfile)
        private var taskAssignorTv: TextView = itemView.findViewById(R.id.assignorTaskTv)
        private var deleteTask: ImageView = itemView.findViewById(R.id.deleteTaskIcon)
        private var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)


        fun bind(task: Task, position: Int) {
            adjustHolderSize()
            textTaskMentionView.text = task.title
            textTaskMentionView.setOnClickListener {
                val frag = ViewTaskFragment.newInstance(task, workspaceId)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                frag.show(transaction, frag.tag)
            }
            progressMilestoneTv.text = if (task.milestonesTask.isNotEmpty()) {
                "${task.milestonesTask.count { it.complete }}/ ${task.milestonesTask.size} milestones"
            } else {
                ""
            }

            progressTask.progress = if (task.milestonesTask.isNotEmpty()) {
                if (task.complete) 100
                else task.milestonesTask.count { it.complete } * 100 / task.milestonesTask.size
            } else {
                0
            }

            if (canEdit) {
                editTask.visibility = View.VISIBLE
                deleteTask.visibility = View.VISIBLE

                editTask.setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    val frag = AddTaskFragment.newInstance(workspaceId, task.taskId, task)
                    val transaction =
                        (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    frag.show(transaction, frag.tag)
                    Handler().postDelayed({
                        progressBar.visibility = View.GONE
                    }, 2500)
                }

                deleteTask.setOnClickListener {
                    taskList.remove(task)
                    notifyItemRemoved(position)
                    workspaceDoc.collection(TASK_SUB_COLLECTION).document(task.taskId!!).delete()
                    Toast.makeText(
                        context,
                        "${task.title} task deleted permanently",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


            val currentUserId = AuthUtils.getCurrentUserId()
            val assigneeIdToShow = if (task.assigneeListIds.contains(currentUserId)) {
                currentUserId
            } else {
                task.assigneeListIds.firstOrNull()
            }

            if (task.assignorId.contentEquals(AuthUtils.getCurrentUserId())) {
                taskAssignorTv.text = "assigned by me"
            } else {
                AuthUtils.getCurrentPerson(task.assignorId) { person ->
                    taskAssignorTv.text = "assigned by ${person?.name}"
                }
            }

            if (assigneeIdToShow != null) {
                AuthUtils.getCurrentPerson(assigneeIdToShow) { person ->
                    person?.let {
                        Glide.with(itemView.context)
                            .load(it.profileUri)
                            .placeholder(R.drawable.account_box)
                            .into(imageMyProfile)

                    }
                }
            } else {
                imageMyProfile.setImageResource(R.drawable.account_box)
            }
        }

        private fun adjustHolderSize() {
            val layoutParamsHolder = cardViewHolder.layoutParams
            val displayMetrics = DisplayMetrics()

            val windowManager =
                itemView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            val screenWidth = displayMetrics.widthPixels
            layoutParamsHolder.width = (screenWidth / 5.5).toInt()
            layoutParamsHolder.height = (screenWidth / 5.5).toInt()

            cardViewHolder.layoutParams = layoutParamsHolder

        }


    }
}
