package com.example.vacinasapucaia.repository

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.vacinasapucaia.R


private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    Log.i("notification", "sender")
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    ).setSmallIcon(R.drawable.ic_vaccine)
        .setContentTitle(applicationContext.getString(R.string.app_name))
        .setContentText(messageBody)

    notify(NOTIFICATION_ID, builder.build())
}