package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
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

        val workspDoc = workspCollection.document(workspId)

        val hashMention = HashMap<String, Any?>()
        hashMention["title"] = taskTitle
        hashMention["assignorId"] = taskOwnerId
        hashMention["taskDescription"] = taskDescription
        hashMention["taskId"] = taskId
        hashMention["assigneeListIds"] = listAssignees
        hashMention["preAttachments"] = prelistAttachments
        hashMention["postAttachments"] = postAttachments
        hashMention["importantLinks"] = listLinks
        hashMention["milestonesTask"] = listMilestones
        hashMention["isComplete"] = isComplete
        hashMention["finalDueDate"] = finalDueDate

        if (isNew) {
            workspDoc.collection(TASK_SUB_COLLECTION).document(taskId).set(hashMention).await()
        } else {
            workspDoc.collection(TASK_SUB_COLLECTION).document(taskId).update(hashMention).await()
        }
    }

}
