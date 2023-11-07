package com.simbiri.equityjamii.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simbiri.equityjamii.R

data class TimeSlot(val time : String)

object AvailableSlots{

    private val availableTimeList = arrayOf(
        "9:00am", "9:30am", "10:00am", "10:30am", "11:00am", "11:30am", "12:00pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm", "4:30am"

    )
    var timeSlotToday : ArrayList<TimeSlot>? =null
    get() {
        if (field != null)
            return field
        field =  ArrayList()

        for (timeslot in availableTimeList){
            val timeAvailable = TimeSlot(timeslot)

            field!!.add(timeAvailable)
        }

        return field

    }
}

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
        val view = LayoutInflater.from(context).inflate(R.layout.time_slots_items, parent, false)

        return SchedulingTimeViewHolder(view)
    }

    override fun onBindViewHolder(schedulingViewHolder: SchedulingTimeViewHolder, position: Int) {
        val newTimeInstance = timeSlotList[position]

        schedulingViewHolder.setDatatoItem(newTimeInstance, position)
    }

    override fun getItemCount():Int  = timeSlotList.size

}