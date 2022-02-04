package com.uldisj.vardadienuapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    @SuppressLint("SimpleDateFormat")
    fun getDate(format: String): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date())
    }

    fun getDayInLatvian(): String {
        val day = getDate("EEEE")
        var returnableDay = ""
        when (day) {
            "Monday" -> {
                returnableDay = "Pirmdiena"
            }
            "Tuesday" -> {
                returnableDay = "Otrdiena"
            }
            "Wednesday" -> {
                returnableDay = "Trešdiena"
            }
            "Thursday" -> {
                returnableDay = "Ceturtdiena"
            }
            "Friday" -> {
                returnableDay = "Piektdiena"
            }
            "Saturday" -> {
                returnableDay = "Sestdiena"
            }
            "Sunday" -> {
                returnableDay = "Svētdiena"
            }
        }
        return returnableDay
    }

    fun getMonthInLatvian(): String {
        val month = getDate("MM")
        var returnableMonth = ""
        when (month) {
            "01" -> {
                returnableMonth = "Janvāris"
            }
            "02" -> {
                returnableMonth = "Februāris"
            }
            "03" -> {
                returnableMonth = "Marts"
            }
            "04" -> {
                returnableMonth = "Aprīlis"
            }
            "05" -> {
                returnableMonth = "Maijs"
            }
            "06" -> {
                returnableMonth = "Jūnijs"
            }
            "07" -> {
                returnableMonth = "Jūlijs"
            }
            "08" -> {
                returnableMonth = "Augusts"
            }
            "09" -> {
                returnableMonth = "Septembris"
            }
            "10" -> {
                returnableMonth = "Oktobris"
            }
            "11" -> {
                returnableMonth = "Novembris"
            }
            "12" -> {
                returnableMonth = "Decembris"
            }
        }
        return returnableMonth
    }
}