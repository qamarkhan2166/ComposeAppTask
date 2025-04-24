package com.example.composeapptask.appFeatures.sensorActivity.sensorWorkers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.composeapptask.R
import com.example.composeapptask.appFeatures.sensorActivity.receiver.ReminderReceiver

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val medicineName = inputData.getString("medicine_name") ?: return Result.failure()
        val dosage = inputData.getString("dosage") ?: return Result.failure()

        showNotification(medicineName, dosage)
        scheduleExactAlarm(medicineName, dosage)

        return Result.success()
    }

    private fun showNotification(medicineName: String, dosage: String) {
        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "medicine_reminders",
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            "medicine_reminders"
        )
            .setContentTitle("Time to take your medicine")
            .setContentText("Take $dosage of $medicineName")
            .setSmallIcon(R.drawable.jetpack_compose_logo)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(medicineName.hashCode(), notification)
    }

    @SuppressLint("ObsoleteSdkInt", "ScheduleExactAlarm")
    private fun scheduleExactAlarm(medicineName: String, dosage: String) {
        val alarmManager = applicationContext.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager

        val intent = Intent(applicationContext, ReminderReceiver::class.java).apply {
            putExtra("medicine_name", medicineName)
            putExtra("dosage", dosage)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            medicineName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + (15 * 60 * 1000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}
