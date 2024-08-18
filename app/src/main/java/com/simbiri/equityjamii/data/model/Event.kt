package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

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
