package com.phonetaxx.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.phonetaxx.AppClass
import com.phonetaxx.R
import com.phonetaxx.firebase.model.UsersDbModel

class PreferenceHelper private constructor() {

    private val AUTH_TOKEN = "AUTH_TOKEN"
    private val FCM_TOKEN = "FCM_TOKEN"
    private val NOTIFICATION_SOUND = "NOTIFICATION_SOUND"
    private val LAST_SYNC_CALL_TIMESTAMP = "LAST_SYNC_CALL_TIMESTAMP"
    private val LAST_DAILY_NOTIFICATION_DATE = "LAST_DAILY_NOTIFICATION_DATE"
    private val LAST_WEEKLY_NOTIFICATION_WEEK = "LAST_WEEKLY_NOTIFICATION_WEEK"
    private val LAST_MONTHLY_NOTIFICATION_MONTH = "LAST_MONTHLY_NOTIFICATION_MONTH"
    private val TOTAL_SCREEN_TIME_IN_SECOND = "TOTAL_SCREEN_TIME_IN_SECOND"
    private val IS_FCM_TOKEN_UPDATED = "IS_FCM_TOKEN_UPDATED"
    private val PROFILE_DATA = "PROFILE_DATA"
    private val IS_LOGIN = "IS_LOGIN"
    private val IS_REMEMBER_ME = "IS_REMEMBER_ME"

    private val CALL_READ = "CALL_READ"


    private val mPrefs: SharedPreferences

    init {
        val application = AppClass.getInstance()
        mPrefs = application.getSharedPreferences("phonetaxx_pref", Context.MODE_PRIVATE)
    }

    companion object {
        private var instance: PreferenceHelper? = null

        @JvmStatic
        fun getInstance(): PreferenceHelper {
            if (instance == null) {
                instance = PreferenceHelper()
            }
            return instance as PreferenceHelper
        }
    }

    var authToken: String?
        get() = mPrefs.getString(AUTH_TOKEN, "")
        set(token) = mPrefs.edit().putString(AUTH_TOKEN, token).apply()

    var fcmToken: String?
        get() = mPrefs.getString(FCM_TOKEN, "")
        set(token) = mPrefs.edit().putString(FCM_TOKEN, token).apply()

    var last_daily_notification_date: String?
        get() = mPrefs.getString(LAST_DAILY_NOTIFICATION_DATE, "")
        set(token) = mPrefs.edit().putString(LAST_DAILY_NOTIFICATION_DATE, token).apply()

    var last_weekly_notification_week: String?
        get() = mPrefs.getString(LAST_WEEKLY_NOTIFICATION_WEEK, "")
        set(token) = mPrefs.edit().putString(LAST_WEEKLY_NOTIFICATION_WEEK, token).apply()

    var notification_sound: Int?
        get() = mPrefs.getInt(NOTIFICATION_SOUND, R.raw.click)
        set(token) = mPrefs.edit().putInt(NOTIFICATION_SOUND, token!!).apply()

    var callRead: Int?
        get() = mPrefs.getInt(CALL_READ, 0)
        set(token) = mPrefs.edit().putInt(CALL_READ, token!!).apply()

    var last_monthly_notification_month: String?
        get() = mPrefs.getString(LAST_MONTHLY_NOTIFICATION_MONTH, "")
        set(token) = mPrefs.edit().putString(LAST_MONTHLY_NOTIFICATION_MONTH, token).apply()

    var lastSyncCallTimeStamp: Long?
        get() = mPrefs.getLong(LAST_SYNC_CALL_TIMESTAMP, 0)
        set(timestamp) = mPrefs.edit().putLong(LAST_SYNC_CALL_TIMESTAMP, timestamp!!).apply()

    var totalScreenTimeInSecond: Long?
        get() = mPrefs.getLong(TOTAL_SCREEN_TIME_IN_SECOND, 0)
        set(timestamp) = mPrefs.edit().putLong(TOTAL_SCREEN_TIME_IN_SECOND, timestamp!!).apply()

    var fcmTokenUpdate: Boolean?
        get() = mPrefs.getBoolean(IS_FCM_TOKEN_UPDATED, false)
        set(isUpdated) = mPrefs.edit().putBoolean(IS_FCM_TOKEN_UPDATED, isUpdated!!).apply()

    var isLogin: Boolean?
        get() = mPrefs.getBoolean(IS_LOGIN, false)
        set(isLogin) = mPrefs.edit().putBoolean(IS_LOGIN, isLogin!!).apply()

    var isRememberMe: Boolean?
        get() = mPrefs.getBoolean(IS_REMEMBER_ME, false)
        set(isShown) = mPrefs.edit().putBoolean(IS_REMEMBER_ME, isShown!!).apply()


    fun setProfileData(profileResponse: UsersDbModel) {
        val data = Gson().toJson(profileResponse)
        if (!TextUtils.isEmpty(data)) {
            mPrefs.edit().putString(PROFILE_DATA, data).apply()
        }
    }

    fun getProfileData(): UsersDbModel? {
        var profileResponse: UsersDbModel? = null
        val data = mPrefs.getString(PROFILE_DATA, "")
        if (!TextUtils.isEmpty(data)) {
            profileResponse = Gson().fromJson(data, UsersDbModel::class.java)
        } else {
            profileResponse = UsersDbModel()
        }
        return profileResponse
    }

    fun clearPreference() {
        mPrefs.edit().remove(PROFILE_DATA).apply()
        mPrefs.edit().remove(IS_LOGIN).apply()
        mPrefs.edit().remove(LAST_SYNC_CALL_TIMESTAMP).apply()
        mPrefs.edit().remove(CALL_READ).apply()

    }

    fun deleteAccountClearPreference() {
        mPrefs.edit().remove(PROFILE_DATA).apply()
        mPrefs.edit().remove(IS_LOGIN).apply()
        mPrefs.edit().remove(IS_REMEMBER_ME).apply()
        mPrefs.edit().remove(CALL_READ).apply()
        mPrefs.edit().remove(LAST_SYNC_CALL_TIMESTAMP).apply()
        clearPreference()
    }

}
