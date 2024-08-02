package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_MENTIONS_SUB_COLLECTIONS
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.Task
import com.simbiri.equityjamii.data.model.WorkspaceMention

class ViewWorkspViewModel(private val workspaceId: String?) : ViewModel() {

    private val _mytasks = MutableLiveData<List<Task>>()
    val mytasks: LiveData<List<Task>> = _mytasks

    private val _myDelegatedtasks = MutableLiveData<List<Task>>()
    val myDelegatedtasks: LiveData<List<Task>> = _myDelegatedtasks

    private val _mentions = MutableLiveData<List<WorkspaceMention>>()
    val mentions: LiveData<List<WorkspaceMention>> = _mentions

    private val firestore = FirebaseFirestore.getInstance()
    private val workspaceRef = firestore.collection(WORKSPACE_COLLECTION).document(workspaceId ?: "")

    private var taskListenerRegistration: ListenerRegistration? = null
    private var mentionListenerRegistration: ListenerRegistration? = null

    init {
        setupTaskListeners()
        setupMentionListeners()
    }

    private fun setupTaskListeners() {
        workspaceId?.let {
            taskListenerRegistration = workspaceRef.collection(TASK_SUB_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Handle error, e.g., log to console or show user error message
                        return@addSnapshotListener
                    }
                    val tasks = snapshot?.toObjects(Task::class.java)
                    _mytasks.postValue(tasks?.filter { it.assigneeListIds.contains(AuthUtils.getCurrentUserId()) })
                    _myDelegatedtasks.postValue(tasks?.filter { it.assignorId == AuthUtils.getCurrentUserId() })
                }
        }
    }

    private fun setupMentionListeners() {
        workspaceId?.let {
            mentionListenerRegistration = workspaceRef.collection(WORKSPACE_MENTIONS_SUB_COLLECTIONS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val mentions = snapshot?.toObjects(WorkspaceMention::class.java)
                    _mentions.postValue(mentions?.sortedByDescending { it.timeMentioned })
                }
        }
    }

    fun removeListeners() {
        taskListenerRegistration?.remove()
        mentionListenerRegistration?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        removeListeners()
    }
}
