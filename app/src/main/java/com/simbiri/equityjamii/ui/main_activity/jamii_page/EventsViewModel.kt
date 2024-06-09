package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EventsViewModel : ViewModel() {
    private val _eventList = MutableLiveData<MutableList<Event>>()
    val eventList: LiveData<MutableList<Event>> = _eventList

    init {
        viewModelScope.launch {
            fetchEvents()
        }
    }

    suspend fun fetchEvents() {
        val ioDispatcher = Dispatchers.IO

        if (FirebaseAuth.getInstance().currentUser != null) {

            val collection = FirebaseFirestore.getInstance().collection(EVENTS_C0LLECTION)
            val taskResult = withContext(ioDispatcher) { collection.get().await() }

            _eventList.value =
                taskResult.toObjects(Event::class.java).sortedBy { event -> event.dateTime }
                    .reversed().toMutableList()

        }
    }

}