package com.simbiri.equityjamii.data.model

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.R
import com.simbiri.equityjamii.constants.USERS_COLLECTION
import com.simbiri.equityjamii.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AvailableSlots {

    private val availableTimeList = arrayOf(
        "9:00am",
        "9:30am",
        "10:00am",
        "10:30am",
        "11:00am",
        "11:30am",
        "12:00pm",
        "2:00pm",
        "2:30pm",
        "3:00pm",
        "3:30pm",
        "4:00pm",
        "4:30am"

    )
    var timeSlotToday: ArrayList<TimeSlot>? = null
        get() {
            if (field != null) return field
            field = ArrayList()

            for (timeslot in availableTimeList) {
                val timeAvailable = TimeSlot(timeslot)

                field!!.add(timeAvailable)
            }

            return field

        }
}

object SocialMedia {

    val instaGPic = R.drawable.insta

    val faceBPic = R.drawable.facebook

    val linkedIPic = R.drawable.linkedin

    val webSPic = R.drawable.web_iconsvg

    val x_pic = R.drawable.x_social_media_black_icon

}

object AuthUtils {
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun getCurrentPerson(userId: String?, callback: (Person?) -> Unit) {
        val userCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
        if (userId != null) {
            userCollection.document(userId).get()
                .addOnCompleteListener { snapShotRetrieveTask ->
                    if (snapShotRetrieveTask.isSuccessful) {
                        val person = snapShotRetrieveTask.result.toObject(Person::class.java)
                        callback(person)
                    } else {
                        callback(null)
                    }
                }
        } else {
            callback(null)
        }
    }

}

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

        return recommendListFollow
    }
}



object YoutubeKeyProvider {

    fun keyProvider(context: Context, intKey: Int): String {
        return (if (intKey == 0) {
            context.resources.getString(R.string.apikey)
        } else {
            context.resources.getString(R.string.channelID)
        }).toString()

    }
}

object YouTubeVids {

    private val ioDispatcher = Dispatchers.IO

    suspend fun getYoutubeVideo(context: Context, videoId: String): Video? {
        val apiKey = YoutubeKeyProvider.keyProvider(context, 0)

        return withContext(ioDispatcher) {
            try {
                val response = RetrofitClient.instance.getYoutubeVideoDetails(
                    videoId = videoId,
                    apiKey = apiKey,
                    part = "snippet"
                )
                val items = response.items ?: return@withContext null
                val firstItem = items.firstOrNull() ?: return@withContext null

                val snippet = firstItem.snippet
                Video(snippet.title, snippet.thumbnails.high.url, firstItem.id.videoId?:"null")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun YoutubeVideos(context: Context, eventType: String): List<Video> {
        val apiKey = YoutubeKeyProvider.keyProvider(context, 0)
        val channelId = YoutubeKeyProvider.keyProvider(context, 1)

        val params = mapOf(
            "limit" to "20", "eventType" to eventType, "type" to "video", "page" to "1"
        )

        return withContext(ioDispatcher) {
            try {
                val response = RetrofitClient.instance.getYoutubeVideos(
                    apiKey = apiKey,
                    channelId = channelId,
                    part = "snippet,id",
                    order = "date",
                    maxResults = 20,
                    params = params
                )

                response.items.mapNotNull {
                    val videoId = it.id.videoId ?: return@mapNotNull null
                    val snippet = it.snippet
                    val title = snippet.title
                    val imageUrl = snippet.thumbnails.high.url
                    Video(title, imageUrl, videoId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }

        }
    }


}

