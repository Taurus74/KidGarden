package com.tausoft.kidsgarden.calendarView.ui

class DatesRow(
    year: Int,
    month: Int,
    val dates: IntArray = IntArray(7),
    val daysOff: BooleanArray = BooleanArray(7)): CalendarRow(year, month) {
    init {
        rowType = DATES_ROW
    }
}