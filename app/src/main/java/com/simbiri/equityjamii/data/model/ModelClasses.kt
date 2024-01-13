package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

data class NewsText(val image: String, val title: String, val allNews : String) : Parcelable {

    constructor() : this("", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString() ?: "",
        parcel.readString()?:""

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

data class OfficialNews (val headline : String, val officialPreviewText: String)

data class Person(val imageId: Int, val name: String, val designation: String)

data class Post (val image : String, val caption : String, val date : String, val likes : Int, val user : User, val postId : String , var liked : Boolean)

data class TimeSlot(val time : String)

data class User(val  profileImage: String, val backGroundImage : String, val name : String, val designation : String, val branch : String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: ""
    ) {
    }

    constructor() : this("", "", "","","")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profileImage)
        parcel.writeString(backGroundImage)
        parcel.writeString(name)
        parcel.writeString(designation)
        parcel.writeString(branch)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

data class Video(
    val title: String,
    val videoUrl: String,
    val videoId : String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: ""
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

