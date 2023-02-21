package com.phonetaxx.firebase.model

import android.os.Parcel
import android.os.Parcelable

class CallLogsDbModel() : Parcelable, CommonModel() {
    var uuId: String = ""
    var userUuid: String = ""
    var phoneNumber: String = ""
    var name: String = ""
    var callType: String = ""// OUTGOING,INCOMING,MISSED
    var callDateTimeUTC: Long = 0
    var callDateTimeLocal: Long = 0
    var callDurationInSecond: String = ""
    var callCategory: String = "" //// 0- uncategorized, 1- personal, 2 - business
    var callDate: String = ""
    var callWeek: String = ""
    var callMonth: String = ""
    var callYear: String = ""

    constructor(parcel: Parcel) : this() {
        uuId = parcel.readString()!!
        userUuid = parcel.readString()!!

        phoneNumber = parcel.readString()!!
        name = parcel.readString()!!
        callType = parcel.readString()!!
        callDateTimeUTC = parcel.readLong()
        callDateTimeLocal = parcel.readLong()
        callDurationInSecond = parcel.readString()!!
        callCategory = parcel.readString()!!
        callDate = parcel.readString()!!
        callWeek = parcel.readString()!!
        callMonth = parcel.readString()!!
        callYear = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(uuId)
        parcel.writeString(userUuid)
        parcel.writeString(phoneNumber)
        parcel.writeString(name)
        parcel.writeString(callType)
        parcel.writeLong(callDateTimeUTC)
        parcel.writeLong(callDateTimeLocal)
        parcel.writeString(callDurationInSecond)
        parcel.writeString(callCategory)
        parcel.writeString(callDate)
        parcel.writeString(callWeek)
        parcel.writeString(callMonth)
        parcel.writeString(callYear)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallLogsDbModel> {
        override fun createFromParcel(parcel: Parcel): CallLogsDbModel {
            return CallLogsDbModel(parcel)
        }

        override fun newArray(size: Int): Array<CallLogsDbModel?> {
            return arrayOfNulls(size)
        }
    }
}