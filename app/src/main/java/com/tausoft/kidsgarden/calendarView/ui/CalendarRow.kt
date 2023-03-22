package com.tausoft.kidsgarden.calendarView.ui

open class CalendarRow(val year: Int, val month: Int) {
    var rowType = UNDEFINED

    companion object {
        const val MONTH_ROW     = 1
        const val DAY_NAMES_ROW = 2
        const val DATES_ROW     = 3
        const val UNDEFINED     = 0
    }
}