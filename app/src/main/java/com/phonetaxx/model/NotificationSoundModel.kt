package com.phonetaxx.model

import android.os.Parcel
import android.os.Parcelable

class NotificationSoundModel() : Parcelable {
    var name: String = ""
    var sound: Int = 0
    var isSelected: Boolean = false

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()!!
        sound = parcel.readInt()
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(sound)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationSoundModel> {
        override fun createFromParcel(parcel: Parcel): NotificationSoundModel {
            return NotificationSoundModel(parcel)
        }

        override fun newArray(size: Int): Array<NotificationSoundModel?> {
            return arrayOfNulls(size)
        }
    }

}