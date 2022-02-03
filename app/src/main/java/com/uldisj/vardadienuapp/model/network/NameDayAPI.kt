package com.uldisj.vardadienuapp.model.network

import com.uldisj.vardadienuapp.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NameDayAPI {

    @GET(Constants.API_ENDPOINT)
    fun getNameDay(
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.DATE) date: String
    ):Single<ArrayList<String>>
}