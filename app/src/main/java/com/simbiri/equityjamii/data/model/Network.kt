package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

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