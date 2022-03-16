package com.uldisj.vardadienuapp.view.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.uldisj.vardadienuapp.R
import com.uldisj.vardadienuapp.databinding.ActivityMainBinding
import com.uldisj.vardadienuapp.model.notification.NotifyReceiver
import com.uldisj.vardadienuapp.utils.DateUtil
import com.uldisj.vardadienuapp.utils.NetworkChecker
import com.uldisj.vardadienuapp.viewmodel.NameDayViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var nameDayViewModel: NameDayViewModel

    private var progressDialog: Dialog? = null

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    var calendar: Calendar = Calendar.getInstance()

    private lateinit var settings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var timePicker: MaterialTimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        settings = getSharedPreferences("NameDayAppPreferences", MODE_PRIVATE)
        editor = settings.edit()

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        if (settings.getInt("Hours", -1) == -1) {
            calendar[Calendar.HOUR_OF_DAY] = 10
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            editor.putInt("Hours", calendar[Calendar.HOUR_OF_DAY])
            editor.putInt("Minutes", calendar[Calendar.MINUTE])
            editor.commit()
            startWork()
        } else {
            calendar[Calendar.HOUR_OF_DAY] = settings.getInt("Hours", -1)
            calendar[Calendar.MINUTE] = settings.getInt("Minutes", -1)
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }

        nameDayViewModel = ViewModelProvider(this).get(NameDayViewModel::class.java)
        nameDayViewModel.getNameDayFromAPI()
        nameDayViewModelObserver()
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog?.let {
            it.setContentView(R.layout.custom_dialog_progress)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        progressDialog?.let {
            it.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                showTimePicker()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showTimePicker() {
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar[Calendar.HOUR_OF_DAY])
            .setMinute(calendar[Calendar.MINUTE])
            .setTheme(R.style.AppTheme_MaterialTimePickerTheme)
            .setTitleText("Lūdzu izvēlies, cikos nosūtīt ziņojumu")
            .build()

        timePicker.show(supportFragmentManager, "foxandroid")

        timePicker.addOnPositiveButtonClickListener {
            calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
            calendar[Calendar.MINUTE] = timePicker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            stopWork()
            startWork()
            editor.putInt("Hours", timePicker.hour)
            editor.putInt("Minutes", timePicker.minute)
            editor.commit()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun nameDayViewModelObserver() {
        nameDayViewModel.nameDayResponse.observe(this) { nameDayResponse ->
            nameDayResponse?.let {
                binding.tvNameDays.text =
                    nameDayResponse.toString().substring(1, nameDayResponse.toString().length - 1)
                binding.tvDate.text = DateUtil().getDate("dd")
                binding.tvDayText.text = DateUtil().getDayInLatvian()
                binding.tvMonthText.text = DateUtil().getMonthInLatvian()
            }
        }
        nameDayViewModel.nameDayLoadingError.observe(this) { dataError ->
            dataError?.let {
                if (dataError) {
                    binding.tvNameDays.text = "Kaut kas nogāja greizi..."
                }
            }
        }
        nameDayViewModel.loadNameDay.observe(this) { loadNameDay ->
            loadNameDay?.let {
                if (loadNameDay) {
                    showProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }

    private fun startWork() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotifyReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
    }

    private fun stopWork() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotifyReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.cancel(pendingIntent)
    }
}