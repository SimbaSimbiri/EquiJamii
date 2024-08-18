package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

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

    constructor() : this(
        mutableListOf(), "", "", "", "",
        "", null, ""
    )

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