package com.tausoft.kidsgarden.data

import com.tausoft.kidsgarden.di.DatabaseDaysOff
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaysOffRepository @Inject constructor() {

    @DatabaseDaysOff
    @Inject lateinit var daysOff: DaysOffDataSource

    fun addDayOff(day: DayOff) = daysOff.addDayOff(day)

    fun getDaysOff(yearFrom: Int, yearTo: Int) = daysOff.getDaysOff(yearFrom, yearTo)

    fun getDaysOffCount(dateFrom: Int, dateTo: Int): Int {
        var result = 0
        daysOff.getDaysOffCount(dateFrom, dateTo) {
            result = it
        }
        return result
    }

}