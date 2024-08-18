package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

data class Workspace(
    var workspaceId: String? = null,
    var titleImage: FileTitle?,
    var imageQuote: FileTitle?,
    var description: String,
    var ownerId: String,
    var passCode: String,
    var importantLinks: MutableList<FileTitle>,
    var importantDocs: MutableList<FileTitle>,
    var adminsListIds: MutableList<String>,
) : Parcelable {

    constructor() : this(
        "", null, null, "", "",
        "", mutableListOf(), mutableListOf(), mutableListOf()
    )

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(FileTitle::class.java.classLoader),
        parcel.readParcelable(FileTitle::class.java.classLoader),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(workspaceId)
        parcel.writeParcelable(titleImage, flags)
        parcel.writeParcelable(imageQuote, flags)
        parcel.writeString(ownerId)
        parcel.writeString(passCode)
        parcel.writeString(description)
        parcel.writeTypedList(importantLinks)
        parcel.writeTypedList(importantDocs)
        parcel.writeStringList(adminsListIds)
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