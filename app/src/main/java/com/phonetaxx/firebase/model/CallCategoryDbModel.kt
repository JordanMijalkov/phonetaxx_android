package com.phonetaxx.firebase.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CallCategoryDbModel() : Parcelable {
    @PrimaryKey
    var uuId: String = ""

    @ColumnInfo
    var userUuid: String = ""

    @ColumnInfo
    var phoneName: String = ""

    @ColumnInfo
    var phoneNumber: String = ""

    @ColumnInfo
    var numberType: Int = 0 // 0 - normal , 1 - frequent

    @ColumnInfo
    var callCategory: String = "" // 0- uncategorized, 1- personal, 2 - business

    @ColumnInfo
    var phoneImage: String = ""

    @ColumnInfo
    var businessName: String = ""

    @ColumnInfo
    var einCode: String = ""

    @ColumnInfo
    var naicsCode: String = ""

    @ColumnInfo
    var malout: String = ""

    @ColumnInfo
    var location: String = ""


    constructor(parcel: Parcel) : this() {
        uuId = parcel.readString()!!
        userUuid = parcel.readString()!!
        phoneNumber = parcel.readString()!!
        numberType = parcel.readInt()
        callCategory = parcel.readString()!!
        phoneName = parcel.readString()!!
        phoneImage = parcel.readString()!!
        businessName = parcel.readString()!!
        einCode = parcel.readString()!!
        naicsCode = parcel.readString()!!
        malout = parcel.readString()!!
        location = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuId)
        parcel.writeString(userUuid)
        parcel.writeString(phoneNumber)
        parcel.writeInt(numberType)
        parcel.writeString(callCategory)
        parcel.writeString(phoneName)
        parcel.writeString(phoneImage)
        parcel.writeString(businessName)
        parcel.writeString(einCode)
        parcel.writeString(naicsCode)
        parcel.writeString(malout)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallCategoryDbModel> {
        override fun createFromParcel(parcel: Parcel): CallCategoryDbModel {
            return CallCategoryDbModel(parcel)
        }

        override fun newArray(size: Int): Array<CallCategoryDbModel?> {
            return arrayOfNulls(size)
        }
    }

}