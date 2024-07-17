package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp


data class Workspace(
    var workspaceId: String? = null,
    var titleImage: FileTitle?,
    var todayImageQuote : FileTitle?,
    var about: String,
    var ownerId: String,
    var passCode: Int,
    var description: String,
    var importantLinks: MutableList<FileTitle>,
    var importantDocs: MutableList<FileTitle>
) : Parcelable {

    constructor() : this("", null,null, "", "",
        0, "", mutableListOf(), mutableListOf())
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(FileTitle::class.java.classLoader),
        parcel.readParcelable(FileTitle::class.java.classLoader),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(workspaceId)
        parcel.writeParcelable(titleImage, flags)
        parcel.writeParcelable(todayImageQuote, flags)
        parcel.writeString(about)
        parcel.writeString(ownerId)
        parcel.writeInt(passCode)
        parcel.writeString(description)
        parcel.writeTypedList(importantLinks)
        parcel.writeTypedList(importantDocs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Workspace> {
        override fun createFromParcel(parcel: Parcel): Workspace {
            return Workspace(parcel)
        }

        override fun newArray(size: Int): Array<Workspace?> {
            return arrayOfNulls(size)
        }
    }
}

data class Task(
    var title: String,
    var assignorId: String,
    var collabAssigneesListIds: MutableList<String>,
    var assigneeListIds: MutableList<String>,
    val priority: Int,
    var preAttachments: MutableList<FileTitle>,
    var postAttachments: MutableList<FileTitle>,
    var subTaskDue: MutableList<SubTaskDue>,
    var finalDueDate: Timestamp? = null,
    val complete : Boolean
) : Parcelable {

    constructor() : this(
        "", "", mutableListOf(), mutableListOf(), 0, mutableListOf(),
        mutableListOf(),mutableListOf(), null, false)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList()!!.toMutableList(),
        parcel.createStringArrayList()!!.toMutableList(),
        parcel.readInt(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(SubTaskDue.CREATOR)!!.toMutableList(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(assignorId)
        parcel.writeInt(priority)
        parcel.writeParcelable(finalDueDate, flags)
        parcel.writeByte(if (complete) 1 else 0)
        parcel.writeTypedList(preAttachments)
        parcel.writeTypedList(postAttachments)
        parcel.writeStringList(assigneeListIds)
        parcel.writeStringList(collabAssigneesListIds)
        parcel.writeTypedList(subTaskDue)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}

data class SubTaskDue(var title: String, var timeDue: Timestamp? = null, var complete: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeParcelable(timeDue, flags)
        parcel.writeByte(if (complete) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubTaskDue> {
        override fun createFromParcel(parcel: Parcel): SubTaskDue {
            return SubTaskDue(parcel)
        }

        override fun newArray(size: Int): Array<SubTaskDue?> {
            return arrayOfNulls(size)
        }
    }
}

data class FileTitle(var fileUri: String, var fileTitle: String, var position: Int) : Parcelable {
    constructor() : this("", "", 0)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileUri)
        parcel.writeString(fileTitle)
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileTitle> {
        override fun createFromParcel(parcel: Parcel): FileTitle {
            return FileTitle(parcel)
        }

        override fun newArray(size: Int): Array<FileTitle?> {
            return arrayOfNulls(size)
        }
    }
}

data class NewsText(
    var fileTitleList: MutableList<FileTitle>,
    var title: String,
    var allNews: String,
    var author: String,
    var newsName: String,
    var newsTag: String,
    var time: Timestamp?,
    var documentId: String? = null
) : Parcelable {

    constructor() : this(mutableListOf(), "", "", "", "",
        "", null, "")

    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString() ?: ""


    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(fileTitleList)
        dest.writeString(title)
        dest.writeString(allNews)
        dest.writeString(author)
        dest.writeString(newsName)
        dest.writeString(newsTag)
        dest.writeParcelable(time, flags)
        dest.writeString(documentId)

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


data class Post(
    val caption: String, val image: String?, val time: Timestamp?, val userId: String,
    val documentId: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    constructor() : this("", "", null, "", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(caption)
        parcel.writeString(image)
        parcel.writeParcelable(time, flags)
        parcel.writeString(userId)
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

data class Event(
    val title: String,
    val description: String,
    val location: String,
    val dateTime: Timestamp?,
    val imageUrl: String?,
    val userId: String,
    val eventType: String,
    val documentId: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()
    )

    constructor() : this("", "", "", null, "", "", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(location)
        parcel.writeParcelable(dateTime, flags)
        parcel.writeString(imageUrl)
        parcel.writeString(userId)
        parcel.writeString(eventType) // Write the event type to the parcel
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event = Event(parcel)
        override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
    }
}

data class Tag(
    val name: String,
    var isSelected: Boolean = false
)


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
    val followingList: MutableList<String>?,
    val followerList: MutableList<String>?,
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
    var userId: String,
    val name: String,
    val designation: String,
    val branch: String,
    var profileUri: String,
    var backGUri: String,
    val city: String,
    val country: String,
    val social: Social,
    val leader: Boolean,
    val network: Network = Network(mutableListOf(), mutableListOf()),
    val verified: Boolean,
    val role: String,
    val newsTags: MutableList<String> = mutableListOf()
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
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Network::class.java.classLoader) ?: Network(
            mutableListOf(),
            mutableListOf()
        ),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: ArrayList<String>()
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
        Social("", "", "", "", "", ""), false,
        Network(mutableListOf(), mutableListOf()), false, "", mutableListOf()
    )

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
        parcel.writeByte(if (leader) 1 else 0)
        parcel.writeParcelable(network, flags)
        parcel.writeByte(if (verified) 1 else 0)
        parcel.writeString(role)
        parcel.writeStringList(newsTags)

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

data class YouTubeResponse(val items: List<YouTubeItemList>)
data class YouTubeVideoResponse(val items: List<YouTubeItem>)
data class YouTubeItemList(val id: YouTubeVideoId, val snippet: YouTubeSnippet)
data class YouTubeItem(val id: String, val snippet: YouTubeSnippet)
data class YouTubeVideoId(val videoId: String?)
data class YouTubeSnippet(val title: String, val thumbnails: YouTubeThumbnails)
data class YouTubeThumbnails(val high: YouTubeThumbnail)
data class YouTubeThumbnail(val url: String)

