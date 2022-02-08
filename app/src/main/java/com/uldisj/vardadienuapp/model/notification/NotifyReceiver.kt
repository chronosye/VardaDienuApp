package com.uldisj.vardadienuapp.model.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.uldisj.vardadienuapp.R
import com.uldisj.vardadienuapp.model.network.NameDayApiService
import com.uldisj.vardadienuapp.utils.Constants
import com.uldisj.vardadienuapp.utils.DateUtil
import com.uldisj.vardadienuapp.view.activities.MainActivity
import java.util.*

class NotifyReceiver : BroadcastReceiver() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        sendNotification(context!!)
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val intentFromRestart = Intent(context, NotifyReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intentFromRestart, 0)
            val settings = context.getSharedPreferences("NameDayAppPreferences", AppCompatActivity.MODE_PRIVATE)

            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = settings.getInt("Hours", 10)
            calendar[Calendar.MINUTE] = settings.getInt("Minutes", 0)

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(context:Context){
        val notificationId = 0

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = "Šodien vārda dienu svin:"
        val subtitleNotification: String

        val nameDayApiService = NameDayApiService()

        val response = nameDayApiService.getNameDay(DateUtil().getDate("MM-dd")).blockingGet()
        response.removeLast()
        subtitleNotification  = response.toString().substring(1, response.toString().length - 1)

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

        if(subtitleNotification.isNotBlank()){
            notificationManager.notify(notificationId, notification.build())
        }
    }
}