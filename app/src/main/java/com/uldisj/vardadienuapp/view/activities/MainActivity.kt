package com.uldisj.vardadienuapp.view.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.uldisj.vardadienuapp.R
import com.uldisj.vardadienuapp.databinding.ActivityMainBinding
import com.uldisj.vardadienuapp.model.notification.NotifyWorker
import com.uldisj.vardadienuapp.utils.DateUtil
import com.uldisj.vardadienuapp.viewmodel.NameDayViewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var nameDayViewModel: NameDayViewModel

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        nameDayViewModel = ViewModelProvider(this).get(NameDayViewModel::class.java)
        nameDayViewModel.getNameDayFromAPI()
        nameDayViewModelObserver()
        startWork()
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
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

    private fun startWork(){
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "Name day Notify Work",
            ExistingPeriodicWorkPolicy.KEEP,
            createWorkRequest())
    }

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotifyWorker>(15, TimeUnit.MINUTES)
        .setConstraints(createConstraints())
        .build()

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(false)
        .build()
}