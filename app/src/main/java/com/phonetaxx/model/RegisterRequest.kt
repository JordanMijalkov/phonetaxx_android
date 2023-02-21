package com.phonetaxx.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class RegisterRequest {
    @SerializedName("customer_id")
    @Expose
    private var customerId: String? = null

    @SerializedName("username")
    @Expose
    private var username: String? = null

    @SerializedName("country_code")
    @Expose
    private var countryCode: String? = null

    @SerializedName("mobile_no")
    @Expose
    private var mobileNo: String? = null

    @SerializedName("email")
    @Expose
    private var email: String? = null

    @SerializedName("password")
    @Expose
    private var password: String? = null

    @SerializedName("device_type")
    @Expose
    private var deviceType: String? = null

    @SerializedName("firebase_token")
    @Expose
    private var firebaseToken: String? = null

    @SerializedName("device_id")
    @Expose
    private var deviceId: String? = null

    constructor(
        customerId: String?,
        username: String?,
        countryCode: String?,
        mobileNo: String?,
        email: String?,
        password: String?,
        deviceType: String?,
        firebaseToken: String?,
        deviceId: String?
    ) {
        this.customerId = customerId
        this.username = username
        this.countryCode = countryCode
        this.mobileNo = mobileNo
        this.email = email
        this.password = password
        this.deviceType = deviceType
        this.firebaseToken = firebaseToken
        this.deviceId = deviceId
    }


    fun getCustomerId(): String? {
        return customerId
    }

    fun setCustomerId(customerId: String?) {
        this.customerId = customerId
    }

    fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String?) {
        this.username = username
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getMobileNo(): String? {
        return mobileNo
    }

    fun setMobileNo(mobileNo: String?) {
        this.mobileNo = mobileNo
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getDeviceType(): String? {
        return deviceType
    }

    fun setDeviceType(deviceType: String?) {
        this.deviceType = deviceType
    }

    fun getFirebaseToken(): String? {
        return firebaseToken
    }

    fun setFirebaseToken(firebaseToken: String?) {
        this.firebaseToken = firebaseToken
    }

    fun getDeviceId(): String? {
        return deviceId
    }

    fun setDeviceId(deviceId: String?) {
        this.deviceId = deviceId
    }

}