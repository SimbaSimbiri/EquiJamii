package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_INVITED_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMIN_INVITED_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddWorkspaceDialogViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection(USERS_COLLECTION)
    val dispatchersIO = Dispatchers.IO
    val currentId = AuthUtils.getCurrentUserId()

    private val _workspace = MutableLiveData<Workspace>()
    val workspace: LiveData<Workspace> get() = _workspace

    private val _adminsList = MutableLiveData<MutableList<Person>>()
    val adminsList: LiveData<MutableList<Person>> get() = _adminsList

    private val _membersList = MutableLiveData<MutableList<Person>>()
    val membersList: LiveData<MutableList<Person>> get() = _membersList

    private val _invitedMembersList = MutableLiveData<MutableList<Person>>()
    val invitedMembersList: LiveData<MutableList<Person>> get() = _invitedMembersList

    private val _invitedAdminsList = MutableLiveData<MutableList<Person>>()
    val invitedAdminsList: LiveData<MutableList<Person>> get() = _invitedAdminsList

    private val _searchList = MutableLiveData<MutableList<Person>>()
    val searchList: LiveData<MutableList<Person>> get() = _searchList

    private val _fullList = MutableLiveData<MutableList<Person>>()
    val fullList: LiveData<MutableList<Person>> get() = _fullList

    fun loadWorkspace(workspaceId: String) {
        viewModelScope.launch {
            val workspaceInstance = withContext(dispatchersIO) {
                firestore.collection(WORKSPACE_COLLECTION).document(workspaceId).get().await()
            }.toObject(Workspace::class.java)
            _workspace.postValue(workspaceInstance)
            loadWorkspaceSubCollections(workspaceId)
        }
    }

    private fun loadWorkspaceSubCollections(workspaceId: String) {
        viewModelScope.launch {
            loadAdmins(workspaceId)
            loadMembers(workspaceId)
            loadInvitedMembers(workspaceId)
            loadInvitedAdmins(workspaceId)
        }
    }

    private suspend fun loadAdmins(workspaceId: String) {
        val admins = withContext(dispatchersIO) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(WORKSP_ADMINS_SUB_COLLECTION)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Person::class.java) }
                .toMutableList()
        }
        _adminsList.postValue(admins)
    }

    private suspend fun loadMembers(workspaceId: String) {
        val members = withContext(dispatchersIO) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(WORKSP_MEMBERS_SUB_COLLECTION)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Person::class.java) }
                .toMutableList()
        }
        _membersList.postValue(members)
    }

    private suspend fun loadInvitedMembers(workspaceId: String) {
        val invitedMembers = withContext(dispatchersIO) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(WORKSP_INVITED_SUB_COLLECTION)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Person::class.java) }
                .toMutableList()
        }
        _invitedMembersList.postValue(invitedMembers)
    }

    private suspend fun loadInvitedAdmins(workspaceId: String) {
        val invitedAdmins = withContext(dispatchersIO) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(WORKSP_ADMIN_INVITED_SUB_COLLECTION)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Person::class.java) }
                .toMutableList()
        }
        _invitedAdminsList.postValue(invitedAdmins)
    }

    fun fetchAllPeople() {
        viewModelScope.launch {
            val allPeople = withContext(dispatchersIO) {
                userCollection.get().await().toObjects(Person::class.java).toMutableList()
            }
            _fullList.postValue(allPeople)
            _searchList.postValue(allPeople)
        }
    }

    fun filterPeople(text: String) {
        val filteredList = if (text.isEmpty()) {
            _fullList.value ?: mutableListOf()
        } else {
            _fullList.value?.filter { it.name.contains(text, ignoreCase = true) }?.toMutableList() ?: mutableListOf()
        }
        _searchList.postValue(filteredList)
    }
}
