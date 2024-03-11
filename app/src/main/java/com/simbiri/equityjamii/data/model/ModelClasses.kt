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

data class Post(val caption : String, val image : String?, val time : String, val userId: String, val person: Person, val likes : Int, var liked : Boolean)

data class TimeSlot(val time : String)

data class  Social (val about : String, val linkedin : String, val insta : String, val faceb : String, val webs: String) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
    ) {
    }

    constructor() : this("","","","","")


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(about)
        parcel.writeString(linkedin)
        parcel.writeString(insta)
        parcel.writeString(faceb)
        parcel.writeString(webs)
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


data class Person(val name: String,  val designation: String, val branch: String,  val profileUri: String, val backGUri: String, val city : String, val country: String, val social: Social) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readParcelable(Social::class.java.classLoader) ?: Social("", "","","","")
    ) {
    }

    constructor() : this("", "", "","","","","", Social("","","","",""))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(designation)
        parcel.writeString(branch)
        parcel.writeString(profileUri)
        parcel.writeString(backGUri)
        parcel.writeString(city)
        parcel.writeString(country)
        parcel.writeParcelable(social, flags)

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


data class Video(
    val title: String,
    val thumbnailUrl: String,
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

