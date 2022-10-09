package com.sdci.tp2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "channel"



class ExtendedFirebaseMessagingService : FirebaseMessagingService() {

    var ZonaCtrlID = ""
    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        ZonaCtrlID = intent?.getStringExtra("ZonaCtrlID").toString()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("INTENT","Es $ZonaCtrlID")
        Log.d("MSGSERVICE", "From: ${remoteMessage.from}")
        if(remoteMessage.notification != null){
            generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
        }

    }

    private fun generateNotification(title: String, message: String){


        val intent2 = Intent(this,VerAlertas::class.java)
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        val pendingIntent = PendingIntent.getActivity(this,0,intent2,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.icono_principal)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0,builder.build())
    }

}