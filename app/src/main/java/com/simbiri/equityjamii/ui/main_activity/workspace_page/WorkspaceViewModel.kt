package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
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

    init {
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
            val currentUserId = AuthUtils.getCurrentUserId()!!

            val invitedWorkspaces = withContext(Dispatchers.IO) {
                val workspaces = firestore.collection(WORKSPACE_COLLECTION).get().await()
                    .toObjects(Workspace::class.java)
                val invitedWorkspacesList = mutableListOf<Workspace>()
                for (workspace in workspaces) {
                    val workspaceId = workspace.workspaceId ?: continue

                    val invitedMembers = firestore.collection(WORKSPACE_COLLECTION)
                        .document(workspaceId)
                        .collection(WORKSP_MEMBERS_SUB_COLLECTION)
                        .document(currentUserId)
                        .get()
                        .await()

                    val invitedAdmins = firestore.collection(WORKSPACE_COLLECTION)
                        .document(workspaceId)
                        .collection(WORKSP_ADMINS_SUB_COLLECTION)
                        .document(currentUserId)
                        .get()
                        .await()

                    if (invitedMembers.exists() || invitedAdmins.exists() || workspace.ownerId.contentEquals(
                            currentUserId
                        )
                    ) {
                        invitedWorkspacesList.add(workspace)
                    }
                }
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