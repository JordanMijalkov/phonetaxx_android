package com.phonetaxx.firebase.model

import android.os.Parcel
import android.os.Parcelable

class SubscriptionModel() : Parcelable, CommonModel() {

    var planType: String = ""
    var priceId: String = ""
    var subscriptionId: String = ""
    var invoiceId: String = ""


    constructor(parcel: Parcel) : this() {
        planType = parcel.readString()!!
        priceId = parcel.readString()!!
        subscriptionId = parcel.readString()!!
        invoiceId = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(planType)
        parcel.writeString(priceId)
        parcel.writeString(subscriptionId)
        parcel.writeString(invoiceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionModel> {
        override fun createFromParcel(parcel: Parcel): SubscriptionModel {
            return SubscriptionModel(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionModel?> {
            return arrayOfNulls(size)
        }
    }

}