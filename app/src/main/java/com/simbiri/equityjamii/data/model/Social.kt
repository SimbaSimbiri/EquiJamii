package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

data class Social(
    val about: String,
    val linkedin: String,
    val insta: String,
    val faceb: String,
    val webs: String,
    val xAcc: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""

    ) {
    }

    constructor() : this("", "", "", "", "", "")


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(about)
        parcel.writeString(linkedin)
        parcel.writeString(insta)
        parcel.writeString(faceb)
        parcel.writeString(webs)
        parcel.writeString(xAcc)
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