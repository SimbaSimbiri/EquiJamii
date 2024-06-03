package com.simbiri.equityjamii.ui.main_activity.news_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.NEWS_COLLECTION
import com.simbiri.equityjamii.data.model.NewsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewsViewModel : ViewModel() {
    private val _newsList = MutableLiveData<List<NewsText>>()
    val newsList: LiveData<List<NewsText>> = _newsList

    init {
        viewModelScope.launch {
            fetchNews()
        }
    }

    suspend fun fetchNews() {
        val ioDispatcher = Dispatchers.IO

        val collection = FirebaseFirestore.getInstance().collection(NEWS_COLLECTION)
        val taskResult = withContext(ioDispatcher) { collection.get().await() }
        _newsList.value = taskResult.toObjects(NewsText::class.java).reversed()


    }

}