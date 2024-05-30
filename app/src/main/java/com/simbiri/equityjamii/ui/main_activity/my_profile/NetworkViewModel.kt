package com.simbiri.equityjamii.ui.main_activity.my_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simbiri.equityjamii.data.model.Network
import com.simbiri.equityjamii.data.model.Person
import com.simbiri.equityjamii.data.model.UserNetworkUtils
import kotlinx.coroutines.launch

class NetworkViewModel : ViewModel() {

    private val _followingList = MutableLiveData<List<Person>>()
    val followingList: LiveData<List<Person>> = _followingList

    private val _followersList = MutableLiveData<List<Person>>()
    val followersList: LiveData<List<Person>> = _followersList

    private val _recommendedList = MutableLiveData<List<Person>>()
    val recommendedList: LiveData<List<Person>> = _recommendedList

    private var network: Network? = null

    fun setNetwork(network: Network) {
        this.network = network
        fetchFollowingList()
        fetchFollowersList()
        fetchRecommendedList()
    }


    private fun fetchFollowingList() {
        viewModelScope.launch {
            val followingIds = network?.followingList
            _followingList.value = UserNetworkUtils.following(followingIds)
        }
    }

    private fun fetchFollowersList() {
        viewModelScope.launch {
            val followerIds = network?.followerList
            _followersList.value = UserNetworkUtils.followers(followerIds)
        }
    }

    private fun fetchRecommendedList() {
        viewModelScope.launch {
            val followingIds = network?.followingList
            val followerIds = network?.followerList
            _recommendedList.value = UserNetworkUtils.recommendFollowing(followingIds, followerIds)
        }
    }
}
