package com.simbiri.equityjamii.ui.main_activity.jamii_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.objects.UserNetworkUtils
import kotlinx.coroutines.launch

class LikesViewModel : ViewModel()  {
    private val _likesPeopleList = MutableLiveData<List<Person>>()
    val likesPeopleList : LiveData<List<Person>> = _likesPeopleList

    fun fetchLikesList(likedUserIds : MutableList<String>?){
        viewModelScope.launch {
            _likesPeopleList.value = UserNetworkUtils.narrowDownUsers(likedUserIds)
        }
    }
}