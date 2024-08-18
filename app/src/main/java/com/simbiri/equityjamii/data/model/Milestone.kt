package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

data class MileStone(
    var titleMilestone: String,
    var timeDueString: String,
    var complete: Boolean,
    var inProgress: Boolean
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()

    ) {
    }

    constructor() : this("", "", false, false)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titleMilestone)
        parcel.writeString(timeDueString)
        parcel.writeByte(if (complete) 1 else 0)
        parcel.writeByte(if (inProgress) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MileStone> {
        override fun createFromParcel(parcel: Parcel): MileStone {
            return MileStone(parcel)
        }

        override fun newArray(size: Int): Array<MileStone?> {
            return arrayOfNulls(size)
        }
    }
}