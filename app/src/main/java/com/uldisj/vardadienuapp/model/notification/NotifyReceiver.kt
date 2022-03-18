package com.uldisj.vardadienuapp.model.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.uldisj.vardadienuapp.R
import com.uldisj.vardadienuapp.model.network.NameDayApiService
import com.uldisj.vardadienuapp.utils.Constants
import com.uldisj.vardadienuapp.utils.DateUtil
import com.uldisj.vardadienuapp.utils.NetworkChecker
import com.uldisj.vardadienuapp.view.activities.MainActivity
import java.util.*


class NotifyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val networkInfo = NetworkChecker().checkForInternet(context!!)

        val alarmManager =
            context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intentFromRestart = Intent(context, NotifyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentFromRestart, 0)
        val settings = context.getSharedPreferences(
            "NameDayAppPreferences",
            AppCompatActivity.MODE_PRIVATE
        )

        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = settings.getInt("Hours", 10)
        calendar[Calendar.MINUTE] = settings.getInt("Minutes", 0)

        if (networkInfo) {
            sendNotification(context, true)
        } else {
            sendNotification(context, false)
        }

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )

        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
            )
        }
    }

    private fun sendNotification(context: Context, networkOn: Boolean) {
        val notificationId = 0

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = "Šodien vārda dienu svin:"

        if (networkOn) {
            val nameDayApiService = NameDayApiService()

            nameDayApiService.getNameDay(DateUtil().getDate("MM-dd")).subscribe { list ->
                list.removeLast()
                val subtitleNotification = list.toString().substring(1, list.toString().length - 1)

                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                val notification =
                    NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL)
                        .setContentTitle(titleNotification)
                        .setContentText(subtitleNotification)
                        .setSmallIcon(R.drawable.ic_notification_logo)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                notification.priority = NotificationCompat.PRIORITY_LOW

                val channel = NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL,
                    Constants.NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(notificationId, notification.build())
            }
        } else {
            val subtitleNotification = "Nav interneta pieslēguma..."

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val notification =
                NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL)
                    .setContentTitle(titleNotification)
                    .setContentText(subtitleNotification)
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            notification.priority = NotificationCompat.PRIORITY_LOW

            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL,
                Constants.NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(notificationId, notification.build())
        }
    }
}