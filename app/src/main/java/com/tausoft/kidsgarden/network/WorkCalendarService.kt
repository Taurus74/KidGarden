package com.tausoft.kidsgarden.network

import com.tausoft.kidsgarden.workCalendar.WorkCalendar
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkCalendarService {
    @GET("data/ru/{year}/calendar.json")
    suspend fun getData(@Path("year") year: String): Response<WorkCalendar>
}