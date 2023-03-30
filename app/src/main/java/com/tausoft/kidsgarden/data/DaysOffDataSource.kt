package com.tausoft.kidsgarden.data

import androidx.lifecycle.LiveData

interface DaysOffDataSource {
    fun addDayOff(day: DayOff)
    fun getDaysOff(dateFrom: Int, dateTo: Int): LiveData<List<DayOff>>
    fun getDaysOffCount(dateFrom: Int, dateTo: Int, callback: (Int) -> Unit)
}