package com.simbiri.equityjamii.ui.main_activity.news_page.live_youtube

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simbiri.equityjamii.data.model.Video
import com.simbiri.equityjamii.data.model.YouTubeVids
import kotlinx.coroutines.launch

class LiveVideosViewModel : ViewModel() {

    private val _liveList = MutableLiveData<List<Video>>()
    val liveList: LiveData<List<Video>> = _liveList

    private val _upcomingList = MutableLiveData<List<Video>>()
    val upcomingList: LiveData<List<Video>> = _upcomingList


    private val _completedList = MutableLiveData<List<Video>>()
    val completedList: LiveData<List<Video>> = _completedList


    suspend fun fetchVideos(context: Context) {
        _liveList.value = YouTubeVids.YoutubeVideos(context, "live")
        _upcomingList.value = YouTubeVids.YoutubeVideos(context, "upcoming")
        _completedList.value = YouTubeVids.YoutubeVideos(context, "completed")
    }

}

