package com.simbiri.equityjamii.ui.main_activity.workspace_page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.TASK_SUB_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import kotlinx.coroutines.launch

class ViewTaskViewModel : ViewModel() {
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    fun updateTaskFields(workspaceId: String, taskId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            val taskRef = firebaseFirestore.collection(WORKSPACE_COLLECTION)
                .document(workspaceId)
                .collection(TASK_SUB_COLLECTION)
                .document(taskId)

            taskRef.update(updates).addOnSuccessListener {
                Log.d("ViewTaskViewModel", "Task successfully updated.")
            }.addOnFailureListener { e ->
                Log.e("ViewTaskViewModel", "Error updating task", e)
            }
        }
    }

}
