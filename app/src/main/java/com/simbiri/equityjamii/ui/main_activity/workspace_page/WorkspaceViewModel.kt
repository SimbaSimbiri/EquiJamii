package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_INVITED_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMIN_INVITED_SUB_COLLECTION
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

    fun fetchWorkspaces() {
        viewModelScope.launch {
            val workspaces = withContext(Dispatchers.IO) {
                firestore.collection(WORKSPACE_COLLECTION)
                    .get()
                    .await()
                    .toObjects(Workspace::class.java)
            }
            _workspaces.postValue(workspaces)
        }
    }

    fun fetchInvitedWorkspaces() {
        viewModelScope.launch {
            val currentUserId = AuthUtils.getCurrentUserId() ?: return@launch
            val invitedWorkspaces = withContext(Dispatchers.IO) {
                val workspaces = firestore.collection(WORKSPACE_COLLECTION).get().await().toObjects(Workspace::class.java)
                val invitedWorkspacesList = mutableListOf<Workspace>()
                for (workspace in workspaces) {
                    val workspaceId = workspace.workspaceId ?: continue

                    val invitedMembers = firestore.collection(WORKSPACE_COLLECTION)
                        .document(workspaceId)
                        .collection(WORKSP_INVITED_SUB_COLLECTION)
                        .document(currentUserId)
                        .get()
                        .await()

                    val invitedAdmins = firestore.collection(WORKSPACE_COLLECTION)
                        .document(workspaceId)
                        .collection(WORKSP_ADMIN_INVITED_SUB_COLLECTION)
                        .document(currentUserId)
                        .get()
                        .await()

                    if (invitedMembers.exists() || invitedAdmins.exists()) {
                        invitedWorkspacesList.add(workspace)
                    }
                }
                invitedWorkspacesList
            }
            _invitedWorkspaces.postValue(invitedWorkspaces)
        }
    }
}
