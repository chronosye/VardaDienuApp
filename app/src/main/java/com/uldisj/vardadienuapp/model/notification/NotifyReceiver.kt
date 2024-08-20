package com.uldisj.vardadienuapp.model.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.uldisj.vardadienuapp.R
import com.uldisj.vardadienuapp.utils.Constants
import com.uldisj.vardadienuapp.utils.DateUtil
import com.uldisj.vardadienuapp.view.activities.MainActivity
import org.json.JSONObject
import java.util.*


class NotifyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val alarmManager =
            context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intentFromRestart = Intent(context, NotifyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentFromRestart, PendingIntent.FLAG_IMMUTABLE)
        val settings = context.getSharedPreferences(
            "NameDayAppPreferences",
            AppCompatActivity.MODE_PRIVATE
        )

        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = settings.getInt("Hours", 10)
        calendar[Calendar.MINUTE] = settings.getInt("Minutes", 0)

        sendNotification(context)

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )

        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
            )
        }
    }

    private fun sendNotification(context: Context) {
        val notificationId = 0

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = "Šodien vārda dienu svin:"

        val assetManager = context.assets

        val jsonString = assetManager.open("namedaysExtended.json").bufferedReader().use { it.readText() }

        val day = DateUtil().getDate("dd")
        val month = DateUtil().getDate("MM")

        val jsonObject = JSONObject(jsonString)
        val subtitleNotification =
            jsonObject.getJSONObject(month).getJSONObject(day).getString("regular")

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification =
            NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(titleNotification)
                .setContentText(subtitleNotification)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(0,"Apskatīt kalendārā neierakstītos vārdus", pendingIntent)

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