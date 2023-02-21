package com.phonetaxx.firebase.model

import android.os.Parcel
import android.os.Parcelable
import com.phonetaxx.utils.DateTimeHelper

open class CommonModel() : Parcelable {
    var createdAt: Long = DateTimeHelper.getInstance().currentTimeStamp;
    var isDeleted: String = "0";

    constructor(parcel: Parcel) : this() {
        createdAt = parcel.readLong()
        isDeleted = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(createdAt)
        parcel.writeString(isDeleted)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommonModel> {
        override fun createFromParcel(parcel: Parcel): CommonModel {
            return CommonModel(parcel)
        }

        override fun newArray(size: Int): Array<CommonModel?> {
            return arrayOfNulls(size)
        }
    }

}