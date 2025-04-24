package com.example.composeapptask.appFeatures.sensorActivity.receiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.composeapptask.R

class ReminderReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("medicine_name") ?: return
        val dosage = intent.getStringExtra("dosage") ?: return

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val notification = NotificationCompat.Builder(
            context,
            "medicine_reminders"
        )
            .setContentTitle("REMINDER: Time to take your medicine")
            .setContentText("Take $dosage of $medicineName now!")
            .setSmallIcon(R.drawable.jetpack_compose_logo)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(medicineName.hashCode(), notification)
    }
}