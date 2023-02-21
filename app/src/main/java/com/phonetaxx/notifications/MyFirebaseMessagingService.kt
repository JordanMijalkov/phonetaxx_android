package com.phonetaxx.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.phonetaxx.R
import com.readystatesoftware.chuck.internal.ui.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    var count = 0
    private var senderId: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //senderId = BaseUtils.getSenderAccountId(applicationContext)
        if (remoteMessage != null) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        count++
        var title = remoteMessage.data["title"]
        var body = remoteMessage.data["body"]
        val id = remoteMessage.data["sender_id"]
        val intent: Intent
//        if (remoteMessage.data["type"] != null) {
            //if(remoteMessage.getData().get("type").equals("s")){
            //   if (remoteMessage.getData().get("booking_type").equals("deliver_now")){
            intent = Intent(this,MainActivity::class.java)
//            intent.putExtra("sender_id", remoteMessage.data["sender_id"])
//            intent.putExtra("userImage", remoteMessage.data["businessLogo"])
//            intent.putExtra("userName", remoteMessage.data["businessName"])
            intent.putExtra("notification", "")
            title = "Phonetaxx"
            body = remoteMessage.data["title"]
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addNextIntentWithParentStack(intent)
            setNotificationData(title, body!!, stackBuilder)

            //            }else{
//                intent = new Intent(this, DeliveryRequest.class);
//                intent.putExtra("id", remoteMessage.getData().get("booking_id"));
//                intent.putExtra("notification","");
//                title = "Grumer";
//                body = remoteMessage.getData().get("title");
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//                stackBuilder.addNextIntentWithParentStack(intent);
//                setNotificationData(title, body, stackBuilder);
//            }
//         }
//        } else {
//            assert(id != null)
//            if (id != senderId) {
//                val mTitle = remoteMessage.data["title"]
//                val mData = remoteMessage.data["message"]
//                intent = Intent(this, ActivityChat::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.putExtra("customerId", id)
//                intent.putExtra("customerName", remoteMessage.data["businessName"])
//                intent.putExtra("notification", "")
//                val stackBuilder = TaskStackBuilder.create(this)
//                stackBuilder.addNextIntentWithParentStack(intent)
//                setNotificationData(mTitle!!, mData!!, stackBuilder)
//            }
//        }
    }


    private fun setNotificationData(
        title: String,
        message: String,
        stackBuilder: TaskStackBuilder
    ) {
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        //String channelId = getString(R.string.default_notification_channel_id);
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("ID", "Channel", importance)
            mChannel.name = Html.fromHtml("<b>$title</b>", 0)
            mChannel.description = message
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.setSound(defaultSoundUri, null)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(mNotifyManager != null)
            mNotifyManager.createNotificationChannel(mChannel)
        }
        val mBuilder = NotificationCompat.Builder(this, "ID")
        mBuilder
            .setContentTitle(Html.fromHtml("<b>$title</b>"))
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_app_icon_small)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setChannelId("ID")
            .setShowWhen(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setNumber(count)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        assert(mNotifyManager != null)
        mNotifyManager.cancelAll()
        mNotifyManager.notify(0, mBuilder.build())
    }


}