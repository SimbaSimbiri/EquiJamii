package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

data class FileTitle(var fileUri: String, var fileTitle: String, var position: Int, var ownerId: String?) :
    Parcelable {
    constructor() : this("", "", 0,"")

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileUri)
        parcel.writeString(fileTitle)
        parcel.writeInt(position)
        parcel.writeString(ownerId)
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
