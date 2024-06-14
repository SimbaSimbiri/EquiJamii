package com.simbiri.equityjamii.ui.main_activity.people_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PeopleViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection(USERS_COLLECTION)
    val dispatchersIO = Dispatchers.IO
    val currentId = AuthUtils.getCurrentUserId()

    private val _jamaaList = MutableLiveData<MutableList<Person>>()
    val jamaaList: LiveData<MutableList<Person>> = _jamaaList

    private val _leadersList = MutableLiveData<MutableList<Person>>()
    val leadersList: LiveData<MutableList<Person>> = _leadersList

    init {
        viewModelScope.launch {
            fetchAll()
        }

    }

    suspend fun fetchAll() {

        val taskResult = withContext(dispatchersIO) { userCollection.get().await() }
        val allPeople = taskResult.toObjects(Person::class.java)

        _leadersList.value = allPeople.filter { person -> person.leader }.toMutableList()
        _jamaaList.value = allPeople.filter { person -> !person.userId.contentEquals(currentId) }
            .filter { person -> !person.leader }.toMutableList()

    }
}