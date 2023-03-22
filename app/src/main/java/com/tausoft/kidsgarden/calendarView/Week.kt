package com.tausoft.kidsgarden.calendarView

import java.util.Calendar

class Week(
    // Начальная дата недели
    dateFrom: Calendar,
    // Позиция начальной даты соотв. дням недели
    position: Int = 0) {
    var numOfWeek: Int = 0
    var weekDates = IntArray(7)

    init {
        numOfWeek = dateFrom.get(Calendar.WEEK_OF_YEAR)
        val lastDayOfMonth = dateFrom.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 0 until position)
            weekDates[day] = 0
        for (day in position..6) {
            val value = dateFrom[Calendar.DAY_OF_MONTH] + day - position
            weekDates[day] = if (value <= lastDayOfMonth)
                value
            else
                0
        }
    }
}