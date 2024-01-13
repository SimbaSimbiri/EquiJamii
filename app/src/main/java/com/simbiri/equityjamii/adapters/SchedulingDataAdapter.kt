package com.simbiri.equityjamii.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.data.model.TimeSlot


class SchedulingDataAdapter(var context: Context, var timeSlotList : ArrayList<TimeSlot>): RecyclerView.Adapter<SchedulingDataAdapter.SchedulingTimeViewHolder>(){

    inner class SchedulingTimeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var positionItem : Int = 1
        private var currentTimeSlot : TimeSlot? = null

        private var timeTextView: TextView = itemView.findViewById(R.id.timeSlotTextview)

        fun setDatatoItem(timeSlot: TimeSlot, position: Int){
            this.positionItem = position
            this.currentTimeSlot = timeSlot

            timeTextView.text = timeSlot.time
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulingTimeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapters_scheduling_item, parent, false)

        return SchedulingTimeViewHolder(view)
    }

    override fun onBindViewHolder(schedulingViewHolder: SchedulingTimeViewHolder, position: Int) {
        val newTimeInstance = timeSlotList[position]

        schedulingViewHolder.setDatatoItem(newTimeInstance, position)
    }

    override fun getItemCount():Int  = timeSlotList.size

}