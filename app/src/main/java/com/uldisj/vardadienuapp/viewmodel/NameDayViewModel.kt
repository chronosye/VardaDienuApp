package com.uldisj.vardadienuapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uldisj.vardadienuapp.model.NameDayResponse
import com.uldisj.vardadienuapp.utils.DateUtil
import org.json.JSONObject

class NameDayViewModel : ViewModel() {

    val loadNameDay = MutableLiveData<Boolean>()
    val nameDayResponse = MutableLiveData<NameDayResponse>()
    val nameDayLoadingError = MutableLiveData<Boolean>()

    fun getNameDay(context : Context) {
        val assetManager = context.assets

        val jsonString = assetManager.open("namedaysExtended.json").bufferedReader().use { it.readText() }

        val day = DateUtil().getDate("dd")
        val month = DateUtil().getDate("MM")

        val jsonObject = JSONObject(jsonString)
        val regular = jsonObject.getJSONObject(month).getJSONObject(day).getString("regular")
        val additional = jsonObject.getJSONObject(month).getJSONObject(day).getString("additional")
        loadNameDay.value = false
        nameDayResponse.value = NameDayResponse(regular, additional)
        nameDayLoadingError.value = false
    }
}