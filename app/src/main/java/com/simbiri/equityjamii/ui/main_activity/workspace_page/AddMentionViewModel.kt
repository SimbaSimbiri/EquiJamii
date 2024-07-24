package com.simbiri.equityjamii.ui.main_activity.workspace_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.WORKSPACE_COLLECTION
import com.simbiri.equityjamii.constants.WORKSPACE_MENTIONS
import com.simbiri.equityjamii.data.model.WorkspaceMention
import kotlinx.coroutines.tasks.await

class AddMentionViewModel : ViewModel() {

    private val _mention = MutableLiveData<WorkspaceMention>()
    val mention: LiveData<WorkspaceMention> = _mention

    private val firebaseStorage = FirebaseFirestore.getInstance()
    private val workspCollection = firebaseStorage.collection(WORKSPACE_COLLECTION)

    suspend fun retrieveMention(workspId: String, mentionId: String) {

        val workspDoc = workspCollection.document(workspId)
        val mentionDoc = workspDoc.collection(WORKSPACE_MENTIONS).document(mentionId).get().await()

        _mention.value = mentionDoc.toObject(WorkspaceMention::class.java)
    }

    suspend fun saveMentionToFirebase(
        keyword: String,
        mainText: String,
        recipientId: String?,
        appreciatorId: String?,
        workspId: String,
        mentionId: String, isNew: Boolean
    ) {

        val workspDoc = workspCollection.document(workspId)

        val hashMention = HashMap<String, Any?>()
        hashMention["keyWordMention"] = keyword
        hashMention["mentionMainText"] = mainText
        hashMention["recipientId"] = recipientId
        hashMention["appreciatorId"] = appreciatorId
        hashMention["timeMentioned"] = System.currentTimeMillis()
        hashMention["mentionId"] = mentionId

        if (isNew) {
            workspDoc.collection(WORKSPACE_MENTIONS).document(mentionId).set(hashMention).await()
        } else {
            workspDoc.collection(WORKSPACE_MENTIONS).document(mentionId).update(hashMention).await()
        }


    }


}