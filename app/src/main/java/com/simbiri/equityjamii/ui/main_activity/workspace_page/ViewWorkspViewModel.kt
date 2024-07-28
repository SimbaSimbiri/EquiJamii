package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_MENTIONS_SUB_COLLECTIONS
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.data.model.WorkspaceMention
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ViewWorkspViewModel(val workspaceId: String?) : ViewModel() {

    private val _mytasks  = MutableLiveData<MutableList<Task>>()
    val mytasks : LiveData<MutableList<Task>> = _mytasks

    private val _myDelegatedtasks  = MutableLiveData<MutableList<Task>>()
    val myDelegatedtasks : LiveData<MutableList<Task>> = _myDelegatedtasks

    private val _mentions  = MutableLiveData<MutableList<WorkspaceMention>>()
    val mentions : LiveData<MutableList<WorkspaceMention>> = _mentions

    private val workspaceCollection =  FirebaseFirestore.getInstance().collection(
        WORKSPACE_COLLECTION)


    init {
        viewModelScope.launch {
            workspaceId?.let {
                fetchTasks()
                fetchMentions()
            }
        }
    }

    suspend fun fetchMentions() {
        if (workspaceId != null){
            val mentionCollection = workspaceCollection.document(workspaceId).collection(
                WORKSPACE_MENTIONS_SUB_COLLECTIONS)

            val taskFirebaseResult = mentionCollection.get().await()

            val mentionsRes = taskFirebaseResult.toObjects(WorkspaceMention::class.java)
            _mentions.value = mentionsRes.sortedByDescending { it.timeMentioned }.toMutableList()


        }

    }

    suspend fun fetchTasks() {

        if (workspaceId !=null){
            val taskCollection = workspaceCollection.document(workspaceId).collection(
                TASK_SUB_COLLECTION)

            val taskFirebaseResult = taskCollection.get().await()

            val allTasks = taskFirebaseResult.toObjects(Task::class.java)

            val myTasksRes = allTasks.filter { task: Task? -> task?.assigneeListIds?.contains(AuthUtils.getCurrentUserId()) == true }
            _mytasks.value = myTasksRes.sortedBy { it.finalDueDate }.toMutableList()

            val delegatedTasks = allTasks.filter { task: Task? -> task?.assignorId.contentEquals(AuthUtils.getCurrentUserId()) }
            _myDelegatedtasks.value = delegatedTasks.sortedBy { it.finalDueDate }.toMutableList()

        }
    }
}