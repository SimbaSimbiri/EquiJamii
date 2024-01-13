package com.simbiri.equityjamii.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R


class TaskDataAdapter(
    var context: Context,
    var listTitleTask: ArrayList<String>,
    var listDescription: ArrayList<String>,
    var listAssignedName: ArrayList<String>
) :
    RecyclerView.Adapter<TaskDataAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var positionItemTask: Int = 0

        private var titleText: TextView = itemView.findViewById(R.id.taskText)
        private var descriptionText: TextView = itemView.findViewById(R.id.descriptionTaskText)
        private var assignedNameText: TextView = itemView.findViewById(R.id.assignedToText)

        fun setTasktoItem(position: Int) {

            this.positionItemTask = position

            titleText.text = listTitleTask[positionItemTask]
            descriptionText.text = listDescription[positionItemTask]
            assignedNameText.text = listAssignedName[positionItemTask]

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapters_tasks_item, parent, false)

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(taskViewHolder: TaskViewHolder, position: Int) {
        taskViewHolder.setTasktoItem(position)
    }

    override fun getItemCount(): Int {

        return this.listTitleTask.size

    }
}
