package com.simbiri.equityjamii.data.objects

import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.data.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserNetworkUtils {

    private var personalID: String? = null

    init {
        personalID = AuthUtils.getCurrentUserId()
    }


    private val ioDispatcher = Dispatchers.IO
    suspend fun narrowDownUsers(existingIds: MutableList<String>?): List<Person> {
        val narrowedUsers: MutableList<Person> = mutableListOf()

        if (!existingIds.isNullOrEmpty()) {
            val collection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
            val taskResult = withContext(ioDispatcher) { collection.get().await() }
            taskResult.forEach {
                val userResult = it.toObject(Person::class.java)
                if (existingIds.contains(userResult.userId)) {
                    narrowedUsers.add(userResult)
                }
            }
        }

        return narrowedUsers
    }

    suspend fun following(followingList: MutableList<String>?): List<Person> {
        return narrowDownUsers(followingList)
    }

    suspend fun followers(followerList: MutableList<String>?): List<Person> {
        return narrowDownUsers(followerList)
    }

    suspend fun followingFollowers(followingList: MutableList<String>?): List<Person> {
        val followingIDSet: MutableSet<String> = mutableSetOf()

        val followingUsers = narrowDownUsers(followingList)

        followingUsers.forEach { user ->
            val userFollowerList: List<Person> =
                narrowDownUsers(user.network.followerList).filter { person ->
                    !person.userId.contentEquals(
                        personalID
                    )
                }

            userFollowerList.forEach { person ->
                followingIDSet.add(person.userId)
            }

        }

        return narrowDownUsers(followingIDSet.toMutableList())
    }

    suspend fun recommendFollowing(
        followingList: MutableList<String>?, followerList: MutableList<String>?
    ): List<Person> {
        val recommendListFollow: MutableList<Person> = mutableListOf()

        val followingFollowers = followingFollowers(followingList).filter { person ->
            !person.userId.contentEquals(
                personalID
            )
        }

        followingFollowers.forEach { person ->

            if (followingList?.contains(person.userId) == false) {
                recommendListFollow.add(person)
            }

        }

        return recommendListFollow.filter { person ->
            !person.userId.contentEquals(
                personalID
            )
        }
    }
}
