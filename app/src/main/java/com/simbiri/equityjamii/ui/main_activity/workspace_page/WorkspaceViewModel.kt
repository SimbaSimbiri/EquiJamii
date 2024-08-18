package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.data.objects.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WorkspaceViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _workspaces = MutableLiveData<List<Workspace>>()
    val workspaces: LiveData<List<Workspace>> get() = _workspaces

    private val _invitedWorkspaces = MutableLiveData<List<Workspace>>()
    val invitedWorkspaces: LiveData<List<Workspace>> get() = _invitedWorkspaces

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    val currentUserId = AuthUtils.getCurrentUserId()!!
    var person: Person? = null

    init {
        AuthUtils.getCurrentPerson(currentUserId) {
            person = it
        }

        viewModelScope.launch {
            try {
                fetchWorkspaces()
                fetchInvitedWorkspaces()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    suspend fun fetchWorkspaces() {
        try {
            val workspaces = withContext(Dispatchers.IO) {
                firestore.collection(WORKSPACE_COLLECTION)
                    .get()
                    .await()
                    .toObjects(Workspace::class.java)
            }
            _workspaces.postValue(workspaces)
        } catch (e: FirebaseFirestoreException) {
            throw Exception("Failed to fetch workspaces: ${e.message}")
        }
    }

    suspend fun fetchInvitedWorkspaces() {
        try {
            val invitedWorkspaces = withContext(Dispatchers.IO) {
                val invitedWorkspacesList = mutableListOf<Workspace>()
                val workspaces = firestore.collection(WORKSPACE_COLLECTION).get().await()
                    .toObjects(Workspace::class.java)
                    .filter { workspace -> person?.workspaces?.contains(workspace.workspaceId) == true }

                invitedWorkspacesList.addAll(workspaces)
                invitedWorkspacesList
            }
            _invitedWorkspaces.value = invitedWorkspaces


        } catch (e: FirebaseFirestoreException) {
            throw Exception("Failed to fetch invited workspaces: ${e.message}")
        }
    }

    private fun handleError(e: Exception) {
        if (e is FirebaseFirestoreException) {
            _error.postValue("Firestore error: ${e.message}")
        } else {
            _error.postValue("An error occurred: ${e.message}")
        }
    }
}