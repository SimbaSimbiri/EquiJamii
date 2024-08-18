package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Task(
    var title: String,
    var assignorId: String,
    var taskDescription: String,
    var taskId: String?,
    var assigneeListIds: MutableList<String>,
    var preAttachments: MutableList<FileTitle>,
    var postAttachments: MutableList<FileTitle>,
    var importantLinks: MutableList<FileTitle>, var postLinks : MutableList<FileTitle>,
    var milestonesTask: MutableList<MileStone>,
    var complete: Boolean,
    var finalDueDate: Timestamp? = null,
) : Parcelable {

    constructor() : this(
        "", "", "", null, mutableListOf(), mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(), mutableListOf(),false, null
    )

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList()!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(FileTitle.CREATOR)!!.toMutableList(),
        parcel.createTypedArrayList(MileStone.CREATOR)!!.toMutableList(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Timestamp::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(assignorId)
        parcel.writeString(taskDescription)
        parcel.writeString(taskId)
        parcel.writeParcelable(finalDueDate, flags)
        parcel.writeByte(if (complete) 1 else 0)
        parcel.writeTypedList(preAttachments)
        parcel.writeTypedList(importantLinks)
        parcel.writeTypedList(postLinks)
        parcel.writeTypedList(postAttachments)
        parcel.writeStringList(assigneeListIds)
        parcel.writeTypedList(milestonesTask)

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