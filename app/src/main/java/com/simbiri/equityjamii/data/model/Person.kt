package com.simbiri.equityjamii.data.model

import android.os.Parcel
import android.os.Parcelable

data class Person(
    var userId: String,
    val name: String,
    val designation: String,
    val branch: String,
    var profileUri: String,
    var backGUri: String,
    val city: String,
    val country: String,
    val social: Social,
    val leader: Boolean,
    val network: Network = Network(mutableListOf(), mutableListOf()),
    val verified: Boolean,
    val role: String,
    val newsTags: MutableList<String> = mutableListOf(),
    val workspaces : MutableList<String> = mutableListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Social::class.java.classLoader) ?: Social("", "", "", "", "", ""),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Network::class.java.classLoader) ?: Network(
            mutableListOf(),
            mutableListOf()
        ),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: ArrayList<String>(),
        parcel.createStringArrayList() ?: ArrayList<String>()
    ) {
    }

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        Social("", "", "", "", "", ""), false,
        Network(mutableListOf(), mutableListOf()), false, "", mutableListOf(), mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(designation)
        parcel.writeString(branch)
        parcel.writeString(profileUri)
        parcel.writeString(backGUri)
        parcel.writeString(city)
        parcel.writeString(country)
        parcel.writeParcelable(social, flags)
        parcel.writeByte(if (leader) 1 else 0)
        parcel.writeParcelable(network, flags)
        parcel.writeByte(if (verified) 1 else 0)
        parcel.writeString(role)
        parcel.writeStringList(newsTags)
        parcel.writeStringList(workspaces)

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