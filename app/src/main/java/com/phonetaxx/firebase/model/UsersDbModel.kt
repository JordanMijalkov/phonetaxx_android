package com.phonetaxx.firebase.model

import android.os.Parcel
import android.os.Parcelable

class UsersDbModel() : Parcelable, CommonModel() {

    var uuId: String = ""
    var fullName: String = ""
    var email: String = ""
    var profileUrl: String = ""
    var loginType: String = ""
    var password: String = ""
    var countryCode: String = ""
    var phoneNumber: String = ""
    var lastSyncTime: Long = 0
    var mothlyBillAmount: Double = 0.00
    var callDetection: Int = 0
    var emailNotification: Int = 0
    var pushNotification: Int = 0
    var socialId: String = ""
    var businessScreenTime: Int = 0
    var Subscription: String? = ""
    var PlanStartDate: String? = ""
    var PlanExpiryDate: String? = ""
    var stripeCustomerId: String? = ""
    var businessName: String? = ""
    var eincode: String? = ""
    var location: String? = ""
    var naicscode: String? = ""
    var supervisoremail: String? = ""


    constructor(parcel: Parcel) : this() {
        uuId = parcel.readString()!!
        fullName = parcel.readString()!!
        email = parcel.readString()!!
        profileUrl = parcel.readString()!!
        loginType = parcel.readString()!!
        password = parcel.readString()!!
        countryCode = parcel.readString()!!
        phoneNumber = parcel.readString()!!
        lastSyncTime = parcel.readLong()
        mothlyBillAmount = parcel.readDouble()
        callDetection = parcel.readInt()
        emailNotification = parcel.readInt()
        pushNotification = parcel.readInt()
        socialId = parcel.readString()!!
        businessScreenTime = parcel.readInt()
        stripeCustomerId = parcel.readString()!!
        Subscription = parcel.readString()!!
        PlanStartDate = parcel.readString()!!
        PlanExpiryDate = parcel.readString()!!
        businessName = parcel.readString()!!
        eincode = parcel.readString()!!
        location = parcel.readString()!!
        naicscode = parcel.readString()!!
        supervisoremail = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        super.writeToParcel(parcel, flags)
        parcel.writeString(uuId)
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(profileUrl)
        parcel.writeString(loginType)
        parcel.writeString(password)
        parcel.writeString(countryCode)
        parcel.writeString(phoneNumber)
        parcel.writeLong(lastSyncTime)
        parcel.writeDouble(mothlyBillAmount)
        parcel.writeInt(callDetection)
        parcel.writeInt(emailNotification)
        parcel.writeInt(pushNotification)
        parcel.writeString(socialId)
        parcel.writeInt(businessScreenTime)
        parcel.writeString(stripeCustomerId)
        parcel.writeString(Subscription)
        parcel.writeString(PlanStartDate)
        parcel.writeString(PlanExpiryDate)
        parcel.writeString(businessName)
        parcel.writeString(eincode)
        parcel.writeString(location)
        parcel.writeString(naicscode)
        parcel.writeString(supervisoremail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UsersDbModel> {
        override fun createFromParcel(parcel: Parcel): UsersDbModel {
            return UsersDbModel(parcel)
        }

        override fun newArray(size: Int): Array<UsersDbModel?> {
            return arrayOfNulls(size)
        }
    }

}