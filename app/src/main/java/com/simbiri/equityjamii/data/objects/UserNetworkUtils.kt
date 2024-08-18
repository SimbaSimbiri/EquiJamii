package com.simbiri.equityjamii.data.objects

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserNetworkUtils {

    private var personalID: String? = null
    private val ioDispatcher = Dispatchers.IO

    init {
        personalID = AuthUtils.getCurrentUserId()
    }


    suspend fun narrowDownUsers(existingIds: MutableList<String>?): List<Person> {
        if (existingIds.isNullOrEmpty()) return emptyList()
        val idsCopy = existingIds.sorted().toList()

        val firestoreCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)

        return withContext(ioDispatcher) {
                val taskResult = firestoreCollection
                    .whereIn(FieldPath.documentId(), idsCopy)
                    .get()
                    .await()

                taskResult.documents.mapNotNull { it.toObject(Person::class.java) }

        }
    }

    suspend fun following(followingList: MutableList<String>?): List<Person> {
        return narrowDownUsers(followingList)
    }

    suspend fun followers(followerList: MutableList<String>?): List<Person> {
        return narrowDownUsers(followerList)
    }

    suspend fun followingFollowers(followingList: MutableList<String>?): List<Person> {
        if (followingList.isNullOrEmpty()) return emptyList()

        val followingUsers = narrowDownUsers(followingList)
        val followingIDSet = mutableSetOf<String>()

        coroutineScope {
            followingUsers.map { user ->
                async(ioDispatcher) {
                    val userFollowerList = narrowDownUsers(user.network.followerList)
                    followingIDSet.addAll(userFollowerList.filter { it.userId != personalID }
                        .map { it.userId })
                }
            }.awaitAll()
        }

        return narrowDownUsers(followingIDSet.toMutableList())
    }

    suspend fun recommendFollowing(
        followingList: MutableList<String>?, followerList: MutableList<String>?
    ): List<Person> {
        val followingFollowers =
            followingFollowers(followingList).filter { it.userId != personalID }
        val followingSet = followingList?.toSet() ?: emptySet()

        return followingFollowers.filter { !followingSet.contains(it.userId) }
    }
}
