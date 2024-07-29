package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_DOCUMENTS_STORE
import com.simbiri.equityjamii.data.model.FileTitle
import com.simbiri.equityjamii.data.model.MileStone
import com.simbiri.equityjamii.data.model.Task
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddTaskViewModel : ViewModel() {
    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val workspCollection = firebaseFirestore.collection(WORKSPACE_COLLECTION)

    fun loadTask(workspId: String, taskId: String) {
        viewModelScope.launch {
            val workspDoc = workspCollection.document(workspId)
            val taskDoc = workspDoc.collection(TASK_SUB_COLLECTION).document(taskId).get().await()
            _task.value = taskDoc.toObject(Task::class.java)
        }
    }

    suspend fun saveTaskToFirebase(
        taskOwnerId: String?,
        taskTitle: String,
        taskDescription: String,
        listAssignees: MutableList<String>,
        prelistAttachments: MutableList<FileTitle>,
        postAttachments: MutableList<FileTitle>,
        listMilestones: MutableList<MileStone>,
        listLinks: MutableList<FileTitle>,
        workspId: String, finalDueDate: Timestamp,
        taskId: String, isComplete: Boolean, isNew: Boolean
    ) {
        if (listMilestones.isEmpty()) {
            listMilestones.add(MileStone("Mark as complete", "task end date", false, false))
        }

        val workspDoc = workspCollection.document(workspId)
        val prelist = uploadDocumentsAndGetFileTitles(prelistAttachments)

        val hashMention = HashMap<String, Any?>()
        hashMention["title"] = taskTitle
        hashMention["assignorId"] = taskOwnerId
        hashMention["taskDescription"] = taskDescription
        hashMention["taskId"] = taskId
        hashMention["assigneeListIds"] = listAssignees
        hashMention["preAttachments"] = prelist.map { it.toHashMap() }
        hashMention["postAttachments"] = postAttachments.map { it.toHashMap() }
        hashMention["importantLinks"] = listLinks.map { it.toHashMap() }
        hashMention["milestonesTask"] = listMilestones.map { it.toHash() }
        hashMention["isComplete"] = isComplete
        hashMention["finalDueDate"] = finalDueDate

        if (isNew) {
            workspDoc.collection(TASK_SUB_COLLECTION).document(taskId).set(hashMention).await()
        } else {
            workspDoc.collection(TASK_SUB_COLLECTION).document(taskId).update(hashMention).await()
        }
    }

    private suspend fun uploadDocumentsAndGetFileTitles(documents: List<FileTitle>): List<FileTitle> {
        val storageRef = FirebaseStorage.getInstance().reference
        val uploadedFileTitles = mutableListOf<FileTitle>()

        for (document in documents) {
            if (document.fileUri.contains("https")) {
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

    private fun FileTitle.toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "fileUri" to this.fileUri,
            "fileTitle" to this.fileTitle,
            "position" to 0
        )
    }

    private fun MileStone.toHash(): HashMap<String, Any?> {
        return hashMapOf(
            "titleMilestone" to this.titleMilestone,
            "timeDueString" to this.timeDueString,
            "complete" to this.complete,
            "inProgress" to this.inProgress
        )
    }
}
