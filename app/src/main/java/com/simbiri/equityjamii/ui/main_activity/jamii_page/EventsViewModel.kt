package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.constants.EVENT_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EventsViewModel : ViewModel() {
    private val _eventList = MutableLiveData<MutableList<Event>>()
    val eventList: LiveData<MutableList<Event>> = _eventList

    private val _registeredEventsList = MutableLiveData<MutableList<Event>>()
    val registeredEventsList: LiveData<MutableList<Event>> = _registeredEventsList

    private val firestoreCollection = FirebaseFirestore.getInstance().collection(EVENTS_C0LLECTION)

    init {
        viewModelScope.launch {
            fetchEvents()
        }
    }

    suspend fun fetchEvents() {
        val ioDispatcher = Dispatchers.IO

        if (FirebaseAuth.getInstance().currentUser != null) {

            val taskResult = withContext(ioDispatcher) { firestoreCollection.get().await() }

            _eventList.value =
                taskResult.toObjects(Event::class.java).sortedBy { event -> event.dateTime }
                    .reversed().filter { it.dateTime!! > Timestamp.now() }.toMutableList()

            eventList.value?.forEach { checkIfregistered(it, AuthUtils.getCurrentUserId()) }


        }
    }

    private fun checkIfregistered(event: Event, userId: String?) {

        firestoreCollection.document(event.documentId!!).collection(EVENT_SUB_COLLECTION)
            .document(userId!!).get().addOnSuccessListener { taskSnapShot ->
                if (taskSnapShot.exists()) {
                    val currentList = _registeredEventsList.value ?: mutableListOf()
                    currentList.add(event)
                    _registeredEventsList.value = currentList
                }
            }
    }

}