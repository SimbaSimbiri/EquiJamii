package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

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