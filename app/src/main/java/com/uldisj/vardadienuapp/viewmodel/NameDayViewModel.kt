package com.uldisj.vardadienuapp.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uldisj.vardadienuapp.model.network.NameDayApiService
import com.uldisj.vardadienuapp.utils.DateUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class NameDayViewModel : ViewModel() {

    private val nameDayApiService = NameDayApiService()

    private val compositeDisposable = CompositeDisposable()

    val loadNameDay = MutableLiveData<Boolean>()
    val nameDayResponse = MutableLiveData<ArrayList<String>>()
    val nameDayLoadingError = MutableLiveData<Boolean>()


    fun getNameDayFromAPI() {
        loadNameDay.value = true
        compositeDisposable.add(
            nameDayApiService.getNameDay(DateUtil().getDate("MM-dd"))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ArrayList<String>>() {
                    override fun onSuccess(value: ArrayList<String>) {
                        value.removeLast()
                        loadNameDay.value = false
                        nameDayResponse.value = value
                        nameDayLoadingError.value = false
                    }

                    override fun onError(e: Throwable) {
                        loadNameDay.value = false
                        nameDayLoadingError.value = true
                        e.printStackTrace()
                    }
                })
        )
    }


}