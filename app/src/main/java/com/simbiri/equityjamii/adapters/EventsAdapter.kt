package com.simbiri.equityjamii.adapters

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.constants.EVENT_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.ui.main_activity.jamii_page.AddEventsDialog

class EventsAdapter(
    private val context: Context,
    private var eventsList: MutableList<Event>,
    private val editable: Boolean = false
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = AuthUtils.getCurrentUserId()

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var eventNameText: TextView = itemView.findViewById(R.id.eventsNameText)
        var eventDescription: TextView = itemView.findViewById(R.id.eventsDescriptionExp)
        var eventDateTime: TextView = itemView.findViewById(R.id.eventDateTimeTypeText)
        var eventImage: ImageView = itemView.findViewById(R.id.eventsThumbNail)
        var editEvent: ImageView = itemView.findViewById(R.id.editEvent)
        var deleteEvent: ImageView = itemView.findViewById(R.id.deleteEvent)
        var registerEvent: ImageView = itemView.findViewById(R.id.regUnregForEvent)
        var eventLink: ImageView = itemView.findViewById(R.id.eventLocationLink)

        private var currentEvent: Event? = null
        private var isDescriptionExpanded = false
        private val MAX_CHAR_COLLAPSED = 90

        fun bind(event: Event) {
            currentEvent = event
            eventNameText.text = event.title
            setTextsToggled(event.description, isDescriptionExpanded)
            eventDateTime.text = event.dateTime.toString()

            if (event.eventType.contentEquals("virtual", true)) {
                eventLink.setImageResource(R.drawable.virt_link)
            } else {
                eventLink.setImageResource(R.drawable.location_link)
            }

            Glide.with(context).load(event.imageUrl).into(eventImage)

            if (editable) {
                editEvent.visibility = View.VISIBLE
                registerEvent.visibility = View.GONE
                deleteEvent.visibility = View.VISIBLE

                editEvent.setOnClickListener {
                    val editEventFrag = AddEventsDialog.newInstance(currentEvent)
                    val transaction =
                        (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    editEventFrag.show(transaction, editEventFrag.tag)
                }

                deleteEvent.setOnClickListener {
                    firestore.collection(EVENTS_C0LLECTION).document(currentEvent!!.documentId!!)
                        .delete()
                }
            }

            registerEvent.setOnClickListener {
                toggleRegistration(event.documentId)
            }
        }

        private fun setTextsToggled(description: String?, isExpanded: Boolean) {
            val spannable = SpannableStringBuilder(description)
            if (description != null) {
                if (description.length > MAX_CHAR_COLLAPSED) {
                    if (!isExpanded) {
                        spannable.delete(MAX_CHAR_COLLAPSED, description.length)
                        spannable.append(" ...read more")
                    } else {
                        spannable.append(" ...read less")
                    }

                    spannable.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            toggleDescriptionExpansion()
                        }
                    }, spannable.length - 12, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    eventDescription.text = spannable
                    eventDescription.movementMethod = LinkMovementMethod.getInstance()
                } else {
                    eventDescription.text = description
                }
            }
        }

        private fun toggleDescriptionExpansion() {
            isDescriptionExpanded = !isDescriptionExpanded
            setTextsToggled(currentEvent?.description, isDescriptionExpanded)
        }


        private fun toggleRegistration(eventId: String?) {
            val eventRef = firestore.collection(EVENTS_C0LLECTION).document(eventId!!)
            val registrationRef = eventRef.collection(EVENT_SUB_COLLECTION)

            registrationRef.document(userId!!).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    registrationRef.document(userId).delete()
                } else {
                    registrationRef.document(userId).set(emptyMap<String, Any>())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapters_events_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventsList[position])
    }

    override fun getItemCount(): Int = eventsList.size
}
