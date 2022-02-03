package com.uldisj.vardadienuapp.model.network


import com.uldisj.vardadienuapp.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class NameDayApiService {

    private val api = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(NameDayAPI::class.java)

    fun getNameDay(date: String): Single<ArrayList<String>> {
        return api.getNameDay(Constants.API_KEY,date)
    }
}