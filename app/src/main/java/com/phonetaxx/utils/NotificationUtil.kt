package com.phonetaxx.utils

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.phonetaxx.R
import java.util.*

class NotificationUtil {
    private var mContext: Context? = null
    private var mTitle: String? = null
    private var mDesc: String? = null
    private var notificationType: String? = null
    private var mNotifId: Int = 0
    private var mNotificationIntent: Intent? = null
    private var isAutoCancel = true
    private var pendingIntent: PendingIntent? = null
    private var taskStackBuilder: TaskStackBuilder? = null

    private val notificationIcon: Int
        get() {
            val whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (whiteIcon) R.drawable.ic_stat_notification else R.mipmap.ic_launcher
        }

    constructor(
        context: Context,
        notificationType: String,
        title: String,
        desc: String,
        notificationIntent: Intent,
        notifId: Int
    ) {
        this.mContext = context.applicationContext
        this.mTitle = title
        this.mDesc = desc
        this.mNotificationIntent = notificationIntent
        this.mNotifId = notifId
        this.notificationType = notificationType
    }

    constructor(
        context: Context,
        notificationType: String,
        title: String,
        desc: String,
        stackBuilder: TaskStackBuilder,
        notifId: Int
    ) {
        this.mContext = context.applicationContext
        this.mTitle = title
        this.mDesc = desc
        this.taskStackBuilder = stackBuilder
        this.mNotifId = notifId
        this.notificationType = notificationType
    }

    constructor(
        context: Context,
        title: String,
        desc: String,
        notificationIntent: Intent,
        notifId: Int,
        isAutoCancel: Boolean
    ) {
        this.mContext = context.applicationContext
        this.mTitle = title
        this.mDesc = desc
        this.mNotificationIntent = notificationIntent
        this.mNotifId = notifId
        this.isAutoCancel = isAutoCancel
    }

    fun show() {
        if (TextUtils.isEmpty(mDesc)) {
            return
        }
        if (mNotifId == 0) {
            val rand = Random()
            mNotifId = rand.nextInt(1000) + Integer.MAX_VALUE
        }
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = mContext!!.getString(R.string.app_name)
        }

        val whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

        val bitmap = BitmapFactory.decodeResource(mContext!!.resources, R.mipmap.ic_launcher)
        val channelId = mContext!!.getString(R.string.app_name)
        val noti_builder = NotificationCompat.Builder(mContext!!, channelId)
            .setPriority(NotificationCompat.PRIORITY_MAX) // it will popup on UI same as incoming default call
            .setContentTitle(mTitle)
            .setContentText(mDesc)
            .setLargeIcon(bitmap)
            .setSmallIcon(notificationIcon)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mDesc))
        //                .setColor(whiteIcon ? mContext.getResources().getColor(R.color.colorPrimary, null)
        //                        : mContext.getResources().getColor(android.R.color.transparent, null));

        if (!isAutoCancel) {
            noti_builder.setOngoing(true)
        }

        if (pendingIntent == null) {
            if (taskStackBuilder == null) {
                Log.v(TAG, "show:1 ")
                mNotificationIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                pendingIntent = PendingIntent.getActivity(
                    mContext,
                    mNotifId,
                    mNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                Log.v(TAG, "show:2 ")
                pendingIntent = taskStackBuilder!!.getPendingIntent(mNotifId, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        noti_builder.setContentIntent(pendingIntent)


        val noti = noti_builder.build()
        val notificationManager = mContext!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()

        noti.flags = noti.flags or Notification.FLAG_AUTO_CANCEL// hide the menu_notification after its selected
        //notificationManager.notify(mNotifId, noti);
        playSound()
        NotificationManagerCompat.from(mContext!!).notify(mNotifId, noti)

    }

    private fun playSound() {
        val am = mContext!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (am.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                var mp = MediaPlayer.create(mContext, PreferenceHelper.getInstance().notification_sound!!)
                mp?.start()
            }
        }

    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val channelId = mContext!!.getString(R.string.app_name)
            val name = mContext!!.getString(R.string.app_name)
            val description = mContext!!.getString(R.string.app_name)
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
            channel.description = description
            channel.setSound(null, null)
            // Register the channel with the system
            val notificationManager = mContext!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {

        private val TAG = "NotificationUtil"
    }
}
