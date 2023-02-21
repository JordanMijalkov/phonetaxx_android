package com.phonetaxx.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.phonetaxx.AppClass
import com.phonetaxx.utils.PreferenceHelper
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (PreferenceHelper.getInstance().isLogin!! && PreferenceHelper.getInstance().getProfileData()!!.pushNotification == 1) {
            val calender = Calendar.getInstance()
            if (calender.get(Calendar.HOUR_OF_DAY) >= 20 && calender.get(Calendar.MINUTE) >= 0) {
//                val mIntent = Intent(AppClass.getInstance(), NotificationService::class.java)
//                NotificationService.enqueueWork(AppClass.getInstance(), mIntent)
            }
        }
        AppClass.getInstance().setAlarmForNotification()
    }
}