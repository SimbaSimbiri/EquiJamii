package com.example.app.adapters

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
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.ui.main_activity.workspace_page.AddTaskFragment

class TaskAdapter(val taskList: MutableList<Task>, val workspaceId: String) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_tasks_item, parent, false)
        return TaskViewHolder(view, workspaceId)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    class TaskViewHolder(itemView: View, val workspaceId: String) :
        RecyclerView.ViewHolder(itemView) {
        private val textTaskMentionView: TextView = itemView.findViewById(R.id.textTaskMentionView)
        private val progressMilestoneTv: TextView = itemView.findViewById(R.id.progressMilestoneTv)
        private val progressTask: LinearProgressIndicator = itemView.findViewById(R.id.progressTask)
        private val editTask: ImageView = itemView.findViewById(R.id.editTask)
        private val imageMyProfile: ImageView = itemView.findViewById(R.id.imageMyProfile)
        private val textNameProfile: TextView = itemView.findViewById(R.id.textNameMyProfile)
        private var cardViewHolder: CardView = itemView.findViewById(R.id.cardViewMyProfile)

        fun bind(task: Task) {
            adjustHolderSize()
            textTaskMentionView.text = task.title
            progressMilestoneTv.text =
                "${task.milestonesTask.count { it.complete }}/ ${task.milestonesTask.size} milestones"
            progressTask.progress =
                task.milestonesTask.count { it.complete } * 100 / task.milestonesTask.size

            editTask.setOnClickListener {
                val frag = AddTaskFragment.newInstance(workspaceId, task.taskId)
                val transaction = (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                frag.show(transaction, frag.tag)
            }

            val currentUserId = AuthUtils.getCurrentUserId()
            val assigneeIdToShow = if (task.assigneeListIds.contains(currentUserId)) {
                currentUserId
            } else {
                task.assigneeListIds.firstOrNull()
            }

            if (assigneeIdToShow != null) {
                AuthUtils.getCurrentPerson(assigneeIdToShow) { person ->
                    person?.let {
                        Glide.with(itemView.context)
                            .load(it.profileUri)
                            .placeholder(R.drawable.account_box)
                            .into(imageMyProfile)
                        textNameProfile.text = it.name

                    }
                }
            } else {
                imageMyProfile.setImageResource(R.drawable.account_box)
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
