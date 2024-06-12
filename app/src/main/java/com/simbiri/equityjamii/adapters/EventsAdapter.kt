package com.simbiri.equityjamii.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.constants.EVENT_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.ui.main_activity.jamii_page.AddEventsDialog
import com.simbiri.equityjamii.ui.main_activity.jamii_page.LikesDialogFragment
import com.simbiri.equityjamii.ui.main_activity.people_page.PersonInfoFragment
import java.text.SimpleDateFormat
import java.util.Locale

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

        var numParticipants: TextView = itemView.findViewById(R.id.numParticipantsText)
        var imageParticipants: ImageView = itemView.findViewById(R.id.eventParticipantsImage)
        var eventParticText: TextView = itemView.findViewById(R.id.eventParticipantsTexts)
        var eventOrganizerTv: TextView = itemView.findViewById(R.id.eventOrganizerTv)
        private var currentParticipantsCount = 0

        private var currentEvent: Event? = null
        private var isDescriptionExpanded = false
        private val MAX_CHAR_COLLAPSED = 90

        fun bind(event: Event, position: Int) {
            eventOrganizer(event)
            currentEvent = event
            eventNameText.text = event.title
            setTextsToggled(event.description, isDescriptionExpanded)
            checkIfRegistered(event)
            numParticipants(event)

            val dateFormat = SimpleDateFormat("MMM dd, yyyy @ hh:mm a", Locale.getDefault())
            eventDateTime.text = "Date & time: ${dateFormat.format(event.dateTime!!.toDate())}"

            if (event.eventType.contentEquals("virtual", true)) {
                eventLink.setImageResource(R.drawable.virt_link)
            } else {
                eventLink.setImageResource(R.drawable.location_link)
            }

            eventLink.setOnClickListener {
                val url = event.location

                if (url.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "No valid event link found!", Toast.LENGTH_SHORT).show()
                }

            }

            Glide.with(context).load(event.imageUrl).fitCenter().into(eventImage)

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
                    eventsList.remove(event)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "Deleted event", Toast.LENGTH_SHORT).show()

                }
            }


            registerEvent.setOnClickListener {
                toggleRegistration(event.documentId)
            }

            eventParticText.setOnClickListener {
                displayParticipants(event)
            }

            numParticipants.setOnClickListener {
                displayParticipants(event)

            }

            imageParticipants.setOnClickListener {
                displayParticipants(event)

            }
        }

        private fun eventOrganizer(event: Event) {
            AuthUtils.getCurrentPerson(event.userId) { organizerPerson ->
                eventOrganizerTv.text = "Event organized by: ${organizerPerson!!.name}"
                eventOrganizerTv.setOnClickListener {
                    showOrganizer(organizerPerson)
                }
                eventDateTime.setOnClickListener {
                    showOrganizer(organizerPerson)
                }
            }


        }

        private  fun showOrganizer(person: Person){
            val organizerFragDetail = PersonInfoFragment.newInstance(person)
            val transaction =
                (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            organizerFragDetail.show(transaction, organizerFragDetail.tag)
        }

        private fun displayParticipants(event: Event) {
            firestore.collection(EVENTS_C0LLECTION).document(event.documentId!!).collection(
                EVENT_SUB_COLLECTION
            ).get().addOnSuccessListener { result ->

                val listIds = ArrayList<String>()
                result.forEach { queryDocumentSnapshot ->
                    listIds.add(queryDocumentSnapshot.id)
                }

                val eventParticiPantsFrag = LikesDialogFragment.newInstance(listIds)
                val transaction =
                    (itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                eventParticiPantsFrag.show(transaction, eventParticiPantsFrag.tag)

            }
        }

        fun checkIfRegistered(event: Event) {
            firestore.collection(EVENTS_C0LLECTION).document(event.documentId!!).collection(
                EVENT_SUB_COLLECTION
            ).document(userId!!).get().addOnSuccessListener {
                if (it.exists()) {
                    registerEvent.setImageResource(R.drawable.register_event)
                } else {
                    registerEvent.setImageResource(R.drawable.event_pending)
                }
            }
        }


        fun numParticipants(event: Event) {
            var count = 0
            firestore.collection(EVENTS_C0LLECTION).document(event.documentId!!).collection(
                EVENT_SUB_COLLECTION
            ).get().addOnSuccessListener {
                count = it.count()
                currentParticipantsCount = count
                this.numParticipants.text = count.toString()
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

                    spannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                toggleDescriptionExpansion()
                            }
                        },
                        spannable.length - 12,
                        spannable.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

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
                    Toast.makeText(context, "Unregistered from event", Toast.LENGTH_SHORT)
                        .show()
                    this.registerEvent.setImageResource(R.drawable.event_pending)
                    if (currentParticipantsCount > 0) {
                        currentParticipantsCount--
                    }
                    this.numParticipants.text = currentParticipantsCount.toString()
                } else {
                    registrationRef.document(userId).set(emptyMap<String, Any>())
                    Toast.makeText(context, "Registered for event", Toast.LENGTH_SHORT).show()
                    this.registerEvent.setImageResource(R.drawable.register_event)
                    currentParticipantsCount++
                    this.numParticipants.text = currentParticipantsCount.toString()

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
        holder.bind(eventsList[position], position)
    }

    override fun getItemCount(): Int = eventsList.size
}
