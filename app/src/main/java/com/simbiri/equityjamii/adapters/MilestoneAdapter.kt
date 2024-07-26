package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.MileStone

class MilestoneAdapter(
    private val context: Context,
    var milestones: MutableList<MileStone>,
    private val canDelete: Boolean
) : RecyclerView.Adapter<MilestoneAdapter.MilestoneViewHolder>() {

    inner class MilestoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTaskMentionView: TextView = itemView.findViewById(R.id.textTaskMentionView)
        val dueMilestoneTv: TextView = itemView.findViewById(R.id.dueMilestoneTv)
        val deleteMilestoneIcon: ImageView = itemView.findViewById(R.id.deleteMilestoneIcon)
        val checkBoxInProgress: CheckBox = itemView.findViewById(R.id.checkBoxInProgress)
        val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkBoxComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.adapters_milestone_item, parent, false)
        return MilestoneViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MilestoneViewHolder, position: Int) {
        val milestone = milestones[position]

        holder.textTaskMentionView.text = milestone.titleMilestone
        holder.dueMilestoneTv.text = milestone.timeDueString
        holder.checkBoxInProgress.isChecked = milestone.inProgress
        holder.checkBoxComplete.isChecked = milestone.complete

        holder.checkBoxInProgress.setOnCheckedChangeListener { _, isChecked ->
            milestone.inProgress = isChecked
        }

        holder.checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            milestone.complete = isChecked
        }

        if (canDelete) {
            holder.deleteMilestoneIcon.visibility = View.VISIBLE
            holder.checkBoxComplete.visibility = View.GONE
            holder.checkBoxInProgress.visibility = View.GONE

            holder.deleteMilestoneIcon.setOnClickListener {
                milestones.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            }
        } else {
            holder.deleteMilestoneIcon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return milestones.size
    }
}