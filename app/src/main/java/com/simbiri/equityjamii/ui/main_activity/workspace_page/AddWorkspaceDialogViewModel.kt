package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_DOCUMENTS_STORE
import com.simbiri.equityjamii.constants.WORKSPACE_IMAGE_STORE
import com.simbiri.equityjamii.constants.WORKSP_MEMBERS_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSP_ADMINS_SUB_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.Workspace
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.UserNetworkUtils
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

    private val _searchList = MutableLiveData<MutableList<Person>>()
    val searchList: LiveData<MutableList<Person>> get() = _searchList

    private val _links = MutableLiveData<MutableList<FileTitle>>()
    val links: LiveData<MutableList<FileTitle>> = _links

    private val _documents = MutableLiveData<MutableList<FileTitle>>()
    val documents: LiveData<MutableList<FileTitle>> = _documents

    private val _fullList = MutableLiveData<MutableList<Person>>()
    val fullList: LiveData<MutableList<Person>> get() = _fullList

    fun loadWorkspace(workspaceId: String) {
        viewModelScope.launch {
            val currentDoc = withContext(dispatchersIO) {
                firestore.collection(WORKSPACE_COLLECTION).document(workspaceId).get().await()
            }
            val workspaceInstance = currentDoc.toObject(Workspace::class.java)
            _workspace.postValue(workspaceInstance!!)

            _documents.postValue(workspaceInstance.importantDocs)
            _links.postValue(workspaceInstance.importantLinks)

            loadWorkspaceSubCollections(workspaceId)
        }
    }

    private fun loadWorkspaceSubCollections(workspaceId: String) {
        viewModelScope.launch {
            loadAdmins(workspaceId)
            loadMembers(workspaceId)
        }
    }

    private suspend fun loadAdmins(workspaceId: String) {
        val listIds = mutableListOf<String>()
        val admins = withContext(dispatchersIO) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(WORKSP_ADMINS_SUB_COLLECTION)
                .get()
                .await()
                .documents.forEach { doc -> listIds.add(doc.id) }
            UserNetworkUtils.narrowDownUsers(listIds).toMutableList()

        }
        _adminsList.postValue(admins)
    }

    private suspend fun loadMembers(workspaceId: String) {
        val listIds = mutableListOf<String>()

        val members = withContext(dispatchersIO) {
            firestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(WORKSP_MEMBERS_SUB_COLLECTION)
                .get()
                .await()
                .documents.forEach { doc -> listIds.add(doc.id) }
            UserNetworkUtils.narrowDownUsers(listIds).toMutableList()

        }
        _membersList.postValue(members)
    }

    fun fetchAllPeople(person: Person) {
        viewModelScope.launch {

            val allPeoples: MutableList<Person> = mutableListOf()
            allPeoples.addAll(UserNetworkUtils.following(person.network.followingList))
            allPeoples.addAll(UserNetworkUtils.followers(person.network.followerList))

            val peopleSet = allPeoples.toSet()

            _fullList.postValue(peopleSet.toMutableList())
            _searchList.postValue(peopleSet.toMutableList())
        }
    }

    fun filterPeople(text: String) {
        val filteredList = if (text.isEmpty()) {
            _fullList.value ?: mutableListOf()
        } else {
            _fullList.value?.filter { it.name.contains(text, ignoreCase = true) }?.toMutableList()
                ?: mutableListOf()
        }
        _searchList.postValue(filteredList)
    }

    private suspend fun uploadDocumentsAndGetFileTitles(documents: List<FileTitle>): List<FileTitle> {
        val storageRef = FirebaseStorage.getInstance().reference
        val uploadedFileTitles = mutableListOf<FileTitle>()

        for (document in documents) {
            if (document.fileUri.contains("https")){
                uploadedFileTitles.add(document)
                continue
            }
            val fileRef = storageRef.child("$WORKSPACE_DOCUMENTS_STORE/${document.fileTitle}")
            val fileUri = Uri.parse(document.fileUri)
            fileRef.putFile(fileUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            uploadedFileTitles.add(
                FileTitle(
                    downloadUrl,
                    document.fileTitle,
                    documents.indexOf(document)
                )
            )
        }

        return uploadedFileTitles
    }

    suspend fun saveWorkspace(
        workspaceId: String?,
        titleWorkspace: String,
        description: String,
        passCode: String,
        ownerId: String,
        links: List<FileTitle>,
        documents: List<FileTitle>,
        imageUri: Uri?, isNew : Boolean = true
    ) {
        val uploadedDocuments = uploadDocumentsAndGetFileTitles(documents)

        val imageTitle = if (isNew){
            FileTitle("", titleWorkspace, 0).toHashMap()
        }
        else{
            FileTitle(imageUri.toString(), titleWorkspace,0).toHashMap()
        }

        val workspaceData = hashMapOf(
            "workspaceId" to workspaceId,
            "titleImage" to imageTitle,
            "description" to description,
            "passCode" to passCode,
            "ownerId" to ownerId,
            "importantLinks" to links.map { it.toHashMap() },
            "importantDocs" to uploadedDocuments.map { it.toHashMap() }
        )

        val newWorkspaceRef = firestore.collection(WORKSPACE_COLLECTION).document(workspaceId!!)
        if (isNew){
            newWorkspaceRef.set(workspaceData).await()
        }else{
            newWorkspaceRef.update(workspaceData).await()
        }
        uploadImageAndSaveWorkspace(newWorkspaceRef.id, imageUri, titleWorkspace)

    }

    private suspend fun uploadImageAndSaveWorkspace(
        workspaceId: String,
        imageUri: Uri?,
        title: String
    ) {
        if (imageUri != null) {

            if (imageUri.toString().contains("https")){
                return
            }
            val storageRef =
                FirebaseStorage.getInstance().reference.child("$WORKSPACE_IMAGE_STORE/$workspaceId")
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            val fileTitle = FileTitle(downloadUrl, title, 0)
            firestore.collection(WORKSPACE_COLLECTION).document(workspaceId)
                .update("titleImage", fileTitle.toHashMap()).await()
        }
    }

    private fun FileTitle.toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "fileUri" to this.fileUri,
            "fileTitle" to this.fileTitle,
            "position" to 0
        )
    }
}
