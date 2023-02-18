package com.uldisj.vardadienuapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uldisj.vardadienuapp.utils.DateUtil
import org.json.JSONObject

class NameDayViewModel : ViewModel() {

    val loadNameDay = MutableLiveData<Boolean>()
    val nameDayResponse = MutableLiveData<String>()
    val nameDayLoadingError = MutableLiveData<Boolean>()


    fun getNameDay(context : Context) {
        val assetManager = context.assets

        val jsonString = assetManager.open("namedays.json").bufferedReader().use { it.readText() }

        val day = DateUtil().getDate("dd")
        val month = DateUtil().getDate("MM")

        val jsonObject = JSONObject(jsonString)
        val subtitleNotification = jsonObject.getJSONObject(month).getString(day)
        loadNameDay.value = false
        nameDayResponse.value = subtitleNotification
        nameDayLoadingError.value = false
    }
}