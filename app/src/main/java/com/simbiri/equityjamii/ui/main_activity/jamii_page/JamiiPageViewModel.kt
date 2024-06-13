package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.simbiri.equityjamii.constants.EVENTS_C0LLECTION
import com.simbiri.equityjamii.constants.POST_COLLECTION
import com.simbiri.equityjamii.data.model.Event
import com.simbiri.equityjamii.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class JamiiPageViewModel : ViewModel() {
    val firestore = FirebaseFirestore.getInstance()
    val postCollection = firestore.collection(POST_COLLECTION)

    private val _allPosts = MutableLiveData<MutableList<Post>>()
    val allPosts: LiveData<MutableList<Post>> = _allPosts


    init {
        viewModelScope.launch {
            allPostsFetch()
        }

    }

    suspend fun allPostsFetch() {
        val dispatchersIO = Dispatchers.IO

        if (FirebaseAuth.getInstance().currentUser != null) {

            val taskResult = withContext(dispatchersIO) { postCollection.get().await() }

            _allPosts.value =
                taskResult.toObjects(Post::class.java).sortedBy { post -> post.time }
                    .reversed().toMutableList()

        }
    }


}