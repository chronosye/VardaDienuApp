package com.uldisj.vardadienuapp.view.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.uldisj.vardadienuapp.R
import com.uldisj.vardadienuapp.databinding.ActivityMainBinding
import com.uldisj.vardadienuapp.utils.DateUtil
import com.uldisj.vardadienuapp.viewmodel.NameDayViewModel
import java.text.SimpleDateFormat
import java.util.*

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
    }

    private fun showProgressDialog(){
        progressDialog = Dialog(this)
        progressDialog?.let {
            it.setContentView(R.layout.custom_dialog_progress)
            it.show()
        }
    }

    private fun hideProgressDialog(){
        progressDialog?.let{
            it.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun nameDayViewModelObserver() {
        nameDayViewModel.nameDayResponse.observe(this) { nameDayResponse ->
            nameDayResponse?.let {
                binding.tvNameDays.text = nameDayResponse.toString().substring(1,nameDayResponse.toString().length -1)
                binding.tvDate.text = DateUtil().getDate("dd")
                binding.tvDayText.text = DateUtil().getDayInLatvian()
                binding.tvMonthText.text = DateUtil().getMonthInLatvian()
            }
        }
        nameDayViewModel.nameDayLoadingError.observe(this){
            dataError ->
            dataError?.let{
                Log.e("Error","Errors")
            }
        }
        nameDayViewModel.loadNameDay.observe(this){
            loadNameDay->
            loadNameDay?.let {
                if(loadNameDay){
                    showProgressDialog()
                }else{
                    hideProgressDialog()
                }
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Vārda dienu App"
    }
}