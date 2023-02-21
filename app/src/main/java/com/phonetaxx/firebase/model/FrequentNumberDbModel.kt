package com.phonetaxx.firebase.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phonetaxx.utils.DateTimeHelper

@Entity
class FrequentNumberDbModel() : Parcelable {
    @PrimaryKey
    var uuId: String = ""
    @ColumnInfo
    var userUuid: String = ""
    @ColumnInfo
    var phoneNumber: String = ""
    @ColumnInfo
    var callCategory: String = "" //// 0- uncategorized, 1- personal, 2 - business
    @ColumnInfo
    var relation: String = "" //// 0- uncategorized, 1- personal, 2 - business
    @ColumnInfo
    var profilePic: String = "" //
    @ColumnInfo
    var createdAt: Long = DateTimeHelper.getInstance().currentTimeStamp;

    constructor(parcel: Parcel) : this() {
        uuId = parcel.readString()!!
        userUuid = parcel.readString()!!
        phoneNumber = parcel.readString()!!
        callCategory = parcel.readString()!!
        relation = parcel.readString()!!
        profilePic = parcel.readString()!!
        createdAt = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuId)
        parcel.writeString(userUuid)
        parcel.writeString(phoneNumber)
        parcel.writeString(callCategory)
        parcel.writeString(relation)
        parcel.writeString(profilePic)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FrequentNumberDbModel> {
        override fun createFromParcel(parcel: Parcel): FrequentNumberDbModel {
            return FrequentNumberDbModel(parcel)
        }

        override fun newArray(size: Int): Array<FrequentNumberDbModel?> {
            return arrayOfNulls(size)
        }
    }

}