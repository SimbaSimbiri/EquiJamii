package com.simbiri.equityjamii.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LiveVideosViewModel : ViewModel() {
    val videosLiveData: MutableLiveData<ArrayList<Video>> = MutableLiveData()

}

