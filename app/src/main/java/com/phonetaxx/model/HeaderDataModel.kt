package com.phonetaxx.model

import android.os.Parcel
import android.os.Parcelable

class HeaderDataModel() : Parcelable {
    var calls: String = ""
    var minuteTalked: String = ""
    var expense: String = ""

    constructor(parcel: Parcel) : this() {
        calls = parcel.readString()!!
        minuteTalked = parcel.readString()!!
        expense = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(calls)
        parcel.writeString(minuteTalked)
        parcel.writeString(expense)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeaderDataModel> {
        override fun createFromParcel(parcel: Parcel): HeaderDataModel {
            return HeaderDataModel(parcel)
        }

        override fun newArray(size: Int): Array<HeaderDataModel?> {
            return arrayOfNulls(size)
        }
    }

}