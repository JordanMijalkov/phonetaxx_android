package com.phonetaxx

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import com.phonetaxx.receiver.AlarmReceiver
import com.phonetaxx.utils.DateTimeHelper
import com.stripe.android.PaymentConfiguration

//import com.stripe.android.PaymentConfiguration

class AppClass : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
        PaymentConfiguration.init(
            applicationContext,
            BuildConfig.STRIPE_PUBLISHABLE_KEY
        )
    }

    companion
    object {
        private var instance: AppClass? = null

        @JvmStatic
        fun getInstance(): AppClass {
            return instance as AppClass
        }
    }

    val TAG: String = "AppClass"
    fun setAlarmForNotification() {
        Log.v(
            TAG,
            "setAlarmForNotification : " + DateTimeHelper.getInstance().getLastDayDateOfTheWeek(System.currentTimeMillis())
        )
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 105, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val nextTime = (1000 * 60 * 1).toLong()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(System.currentTimeMillis() + nextTime, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextTime, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nextTime, pendingIntent)
        }

    }

}
