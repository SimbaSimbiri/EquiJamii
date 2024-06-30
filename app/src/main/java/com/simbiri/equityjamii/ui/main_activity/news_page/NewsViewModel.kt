package com.simbiri.equityjamii.ui.main_activity.news_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.NEWS_COLLECTION
import com.simbiri.equityjamii.constants.NEWS_TAGS_COLLECTION
import com.simbiri.equityjamii.data.model.AuthUtils
import com.simbiri.equityjamii.data.model.NewsText
import com.simbiri.equityjamii.data.model.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewsViewModel : ViewModel() {
    private val _newsList = MutableLiveData<List<NewsText>>()
    val newsList: LiveData<List<NewsText>> = _newsList

    private val _selectedTagList = MutableLiveData<List<Tag>>()
    val selectedTagList: LiveData<List<Tag>> = _selectedTagList

    private val _allTagList = MutableLiveData<List<Tag>>()
    val allTagList: LiveData<List<Tag>> = _allTagList



    init {
        viewModelScope.launch {
            fetchNews()
            fetchTags()
        }
    }

    private fun fetchTags() {
        val firebaseCollection = FirebaseFirestore.getInstance().collection(
            NEWS_TAGS_COLLECTION
        )

        val userId = AuthUtils.getCurrentUserId()

        if (FirebaseAuth.getInstance().currentUser != null) {

            AuthUtils.getCurrentPerson(userId) { person ->
                val listTags = mutableListOf<Tag>()
                person?.newsTags?.forEach {
                    listTags.add(Tag(it, true))
                }
                _selectedTagList.value = listTags

                firebaseCollection.get().addOnSuccessListener { taskSnapShot ->
                    val listTagsAll = mutableListOf<Tag>()
                    taskSnapShot.documents.forEach {

                        if (person?.newsTags?.contains(it.id) == true) {
                            listTagsAll.add(Tag(it.id, true))
                        } else {
                            listTagsAll.add(Tag(it.id, false))
                        }
                    }
                    _allTagList.value = listTagsAll
                }
            }

        }

    }

    suspend fun fetchNews() {
        val ioDispatcher = Dispatchers.IO

        if (FirebaseAuth.getInstance().currentUser != null) {

            val collection = FirebaseFirestore.getInstance().collection(NEWS_COLLECTION)
            val taskResult = withContext(ioDispatcher) { collection.get().await() }

            _newsList.value =
                taskResult.toObjects(NewsText::class.java).sortedBy { newsText -> newsText.time }.reversed()

        }
    }

}