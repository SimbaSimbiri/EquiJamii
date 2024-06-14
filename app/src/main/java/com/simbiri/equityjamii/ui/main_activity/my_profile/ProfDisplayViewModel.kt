package com.simbiri.equityjamii.ui.main_activity.my_profile

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

class ProfDisplayViewModel : ViewModel() {

    val userCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
    val currentId = AuthUtils.getCurrentUserId()

    private val _myProfile = MutableLiveData<Person?>()
    val myProf: LiveData<Person?> = _myProfile

    init {
        viewModelScope.launch {
            fetchPerson()
        }
    }

    suspend fun fetchPerson() {
        val ioDispatchers = Dispatchers.IO

        val userSnapshot =
            withContext(ioDispatchers) { userCollection.document(currentId!!).get().await() }
        _myProfile.value = userSnapshot.toObject(Person::class.java)

    }


}