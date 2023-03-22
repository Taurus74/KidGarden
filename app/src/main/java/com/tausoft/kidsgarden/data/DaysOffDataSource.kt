package com.tausoft.kidsgarden.data

interface DaysOffDataSource {
    fun addDayOff(day: DayOff)
    fun getDaysOff(dateFrom: Int, dateTo: Int, callback: (List<DayOff>) -> Unit)
    fun getDaysOffCount(dateFrom: Int, dateTo: Int, callback: (Int) -> Unit)
}