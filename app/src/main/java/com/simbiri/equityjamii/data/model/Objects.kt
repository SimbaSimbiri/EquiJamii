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

    fun getCurrentPerson(callback: (Person?) -> Unit) {
        val userCollection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
        val currentUserId = getCurrentUserId()
        if (currentUserId != null) {
            userCollection.document(currentUserId).get()
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


object OfficialNewsTexts {

    private val headlineList = arrayOf(

        "Employees to receive increase in salaries",
        "New customer interaction guidelines",
        "Different retirement  plans to sign up for",
        "Introducing new official communication platform",
        "The Pamoja entrepreneurship loan  is now available for employees"

    )

    private val officialPreviewTextList = arrayOf(

        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. ",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla.  Etiam gravida, turpis nec pellentesque congue",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu.",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu. Mauris cursus, velit ac dapibus porttitor, velit elit sodales nunc, vitae euismod justo nisi at mauris. Nunc sit amet odio nec metus consectetur hendrerit.\n" + "\n" + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet euismod purus. Suspendisse potenti. Integer nec aliquet ipsum. Proin eu nisl vitae dui rutrum interdum. Suspendisse potenti. Ut scelerisque, odio ac facilisis feugiat, purus tellus placerat mauris, eget facilisis urna mauris id purus. Phasellus scelerisque odio eu ligula dapibus, vitae feugiat tellus facilisis. Aenean a venenatis lorem. Etiam vehicula, nisi a blandit fringilla, turpis nulla auctor odio, sit amet accumsan ex mi sit amet nulla. Aenean at eros luctus, feugiat lectus eu, tincidunt odio. Mauris semper elit at odio eleifend, at convallis lacus posuere. Suspendisse eu diam dui.\n" + "\n" + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec lacinia metus nec elit vulputate, vel ultrices dolor iaculis. Sed et ex in purus dignissim consequat. Nam id arcu eget erat elementum pulvinar. Maecenas id tortor et dui posuere mollis non eu justo. Donec rutrum nunc vel quam suscipit pellentesque. Cras ullamcorper feugiat fringilla. Suspendisse maximus justo ligula, id bibendum mauris ultrices vel. Sed malesuada auctor dui, id aliquet tellus pellentesque quis",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed aliquet sapien eget dui tincidunt, ac tincidunt mauris tincidunt. Etiam a odio bibendum, blandit odio vitae, rhoncus nulla. Nam luctus tortor vel nibh finibus, eu tempor nunc tempus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla facilisi. Integer condimentum eros ac tincidunt pellentesque. Etiam gravida, turpis nec pellentesque congue, eros felis vehicula eros, non sagittis turpis tellus nec neque. Vivamus mattis arcu justo, eu sollicitudin velit scelerisque eu."
    )

    var officialNewsList: ArrayList<OfficialNews>? = null
        get() {

            if (field != null) return field
            field = ArrayList()

            for (headlinePos in headlineList.indices) {
                val headline = headlineList[headlinePos]
                val previewText = officialPreviewTextList[headlinePos]
                val officialNews = OfficialNews(headline, previewText)

                field!!.add(officialNews)
            }
            return field
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

