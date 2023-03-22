package com.tausoft.kidsgarden.calendarView

import java.util.Calendar

class Month(dateFrom: Calendar, dateTo: Calendar? = null, private val firstDayOfWeek: Int) {
    val weeks: MutableSet<Week> = mutableSetOf()
    var year: Int = 0
    var monthNum: Int = 0
    private lateinit var mDateTo: Calendar

    init {
        year = dateFrom[Calendar.YEAR]
        monthNum = dateFrom[Calendar.MONTH]
        if (dateTo == null)
            setDateTo(dateFrom.getActualMaximum(Calendar.DAY_OF_MONTH))
        else {
            mDateTo = dateTo
            if (monthNum != dateTo[Calendar.MONTH]) {
                // Защита от ошибки передачи параметров:
                // начальная и конечная дата принадлежат одному месяцу
                setDateTo(dateFrom.getActualMaximum(Calendar.DAY_OF_MONTH))
            }
        }

        fillMonth(dateFrom, mDateTo)
    }

    private fun fillMonth(dateFrom: Calendar, dateTo: Calendar) {
        // Номер дня недели первого дня интервала
        val weekDayStart = dateFrom.get(Calendar.DAY_OF_WEEK)
        var startIndex = weekDayStart - firstDayOfWeek
        if (startIndex < 0)
            startIndex += 7
        // Первая неделя интервала
        weeks.add(Week(dateFrom, startIndex))
        // Остальные недели
        val aDate = aDate(dateFrom)
        aDate.add(Calendar.DAY_OF_MONTH, 7)

        while (aDate < dateTo) {
            weeks.add(Week(aDate))
            aDate.add(Calendar.DAY_OF_MONTH, 7)
        }
    }

    private fun setDateTo(dayOfMonth: Int) {
        mDateTo = Calendar.getInstance()
        mDateTo.set(year, monthNum, dayOfMonth)
    }

    private fun aDate(date: Calendar): Calendar {
        val aDate = date.clone() as Calendar
        aDate.set(Calendar.HOUR_OF_DAY, 0)
        aDate.clear(Calendar.MINUTE)
        aDate.clear(Calendar.SECOND)
        aDate.clear(Calendar.MILLISECOND)
        aDate.set(Calendar.DAY_OF_WEEK, aDate.firstDayOfWeek)
        return aDate
    }
}