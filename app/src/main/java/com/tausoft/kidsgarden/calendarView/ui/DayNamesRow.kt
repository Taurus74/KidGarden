package com.tausoft.kidsgarden.calendarView.ui

import java.util.ArrayList

class DayNamesRow(year: Int, month: Int, val dayNames: ArrayList<String>) : CalendarRow(year, month) {
    init {
        rowType = DAY_NAMES_ROW
    }
}