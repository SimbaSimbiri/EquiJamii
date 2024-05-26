package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.simbiri.equityjamii.constants.USERS_COLLECTION

data class NewsText(val image: String, val title: String, val allNews: String) : Parcelable {

    constructor() : this("", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""

    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(image)
        dest.writeString(title)
        dest.writeString(allNews)
    }

    companion object CREATOR : Parcelable.Creator<NewsText> {
        override fun createFromParcel(parcel: Parcel): NewsText {
            return NewsText(parcel)
        }

        override fun newArray(size: Int): Array<NewsText?> {
            return arrayOfNulls(size)
        }
    }
}

data class OfficialNews(val headline: String, val officialPreviewText: String)

data class Post(
    val caption: String, val image: String?, val time: Timestamp?, val userId: String,
    var likes: Int, var liked: Boolean, val documentId: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    ) {
    }

    constructor() : this("", "", null, "", 0, false, "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(caption)
        parcel.writeString(image)
        parcel.writeParcelable(time, flags)
        parcel.writeString(userId)
        parcel.writeInt(likes)
        parcel.writeByte(if (liked) 1 else 0)
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }


}

data class Social(
    val about: String,
    val linkedin: String,
    val insta: String,
    val faceb: String,
    val webs: String,
    val xAcc: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""

    ) {
    }

    constructor() : this("", "", "", "", "", "")


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(about)
        parcel.writeString(linkedin)
        parcel.writeString(insta)
        parcel.writeString(faceb)
        parcel.writeString(webs)
        parcel.writeString(xAcc)
    }

    companion object CREATOR : Parcelable.Creator<Social> {
        override fun createFromParcel(parcel: Parcel): Social {
            return Social(parcel)
        }

        override fun newArray(size: Int): Array<Social?> {
            return arrayOfNulls(size)
        }
    }

}

data class Network(
    val followingList: MutableList<String>,
    val followerList: MutableList<String>,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList()!!.toMutableList(),
        parcel.createStringArrayList()!!.toMutableList()
    ) {
    }

    constructor() : this(mutableListOf(), mutableListOf())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(followingList)
        dest.writeList(followerList)
    }

    companion object CREATOR : Parcelable.Creator<Network> {
        override fun createFromParcel(parcel: Parcel): Network {
            return Network(parcel)
        }

        override fun newArray(size: Int): Array<Network?> {
            return arrayOfNulls(size)
        }
    }
}

data class Person(
    val userId: String,
    val name: String,
    val designation: String,
    val branch: String,
    var profileUri: String,
    var backGUri: String,
    val city: String,
    val country: String,
    val social: Social,
    val network: Network,
    val verified: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Social::class.java.classLoader) ?: Social("", "", "", "", "", ""),
        parcel.readParcelable(Network::class.java.classLoader) ?: Network(
            mutableListOf(),
            mutableListOf()
        ),
        parcel.readByte() != 0.toByte(),
    ) {
    }

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        Social("", "", "", "", "", ""),
        Network(mutableListOf(), mutableListOf()), false
    )

    fun narrowDownUsers(existingIds: MutableList<String>): List<Person> {

        var narrowedUsers: MutableList<Person> = mutableListOf()

        if (existingIds.isNotEmpty()) {

            val collection = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
            collection.get().addOnSuccessListener { result ->
                result.forEach { doc ->
                    val userResult = doc.toObject(Person::class.java)
                    if (existingIds.contains(userResult.userId)) {
                        narrowedUsers.add(userResult)
                    }
                }
            }
        }
        return narrowedUsers.toList()
    }

    fun following(followingList: MutableList<String>): List<Person> {

        return narrowDownUsers(followingList)
    }

    fun followers(followerList: MutableList<String>): List<Person> {
        return narrowDownUsers(followerList)
    }

    fun followingFollowers(followingList: MutableList<String>): List<Person> {
        var followerFollowingList: MutableList<Person> = mutableListOf()
        val followingUsers = narrowDownUsers(followingList)

        followingUsers.forEach { user ->
            val userFollowerList: List<Person> = narrowDownUsers(user.network.followerList)
            followerFollowingList.addAll(userFollowerList)
        }

        return followerFollowingList
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(designation)
        parcel.writeString(branch)
        parcel.writeString(profileUri)
        parcel.writeString(backGUri)
        parcel.writeString(city)
        parcel.writeString(country)
        parcel.writeParcelable(social, flags)
        parcel.writeParcelable(network, flags)
        parcel.writeByte(if (verified) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Person> {
        override fun createFromParcel(parcel: Parcel): Person {
            return Person(parcel)
        }

        override fun newArray(size: Int): Array<Person?> {
            return arrayOfNulls(size)
        }
    }

}

data class TimeSlot(val time: String)

data class Video(
    val title: String,
    val thumbnailUrl: String,
    val videoId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(videoId)
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}

