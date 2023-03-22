package com.tausoft.kidsgarden.calendarView

import com.tausoft.kidsgarden.calendarView.ui.CalendarRow
import com.tausoft.kidsgarden.calendarView.ui.DatesRow
import com.tausoft.kidsgarden.calendarView.ui.DayNamesRow
import com.tausoft.kidsgarden.calendarView.ui.MonthRow
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min

class TauCalendarView(val dateFrom: Calendar? = null, val dateTo: Calendar? = null,
                      firstDayOfWeek: Int = Calendar.MONDAY) {
    private var mFirstDayOfWeek: Int = firstDayOfWeek

    private val calendar = Calendar.getInstance()
    private var mDateFrom = calendar.clone() as Calendar
    private var mDateTo   = calendar.clone() as Calendar
    private var weekDays: ArrayList<String> = arrayListOf()
    private var months: ArrayList<Month> = arrayListOf()

    // Набор нерабочих (выходные, праздники, в т.ч. перенесенные) дней
    // День указывается целым числом в формате ГГГГММДД
    var mDaysOff: MutableSet<Int> = mutableSetOf()

    init {
        if (dateFrom != null && dateTo != null) {
            mDateFrom.timeInMillis = min(dateFrom.timeInMillis, dateTo.timeInMillis)
            mDateTo.  timeInMillis = max(dateFrom.timeInMillis, dateTo.timeInMillis)
        }
        else {
            if (dateFrom == null) {
                mDateFrom = Calendar.getInstance()
                mDateFrom.set(Calendar.MONTH, 0)
                mDateFrom.set(Calendar.DAY_OF_MONTH, 1)
            }
            else
                mDateFrom = dateFrom
            if (dateTo == null) {
                mDateTo = Calendar.getInstance()
                mDateTo.set(Calendar.MONTH, 11)
                mDateTo.set(Calendar.DAY_OF_MONTH, 31)
            }
            else
                mDateTo = dateTo
            if (mDateFrom.timeInMillis > mDateTo.timeInMillis) {
                val t = mDateFrom.timeInMillis
                mDateFrom.timeInMillis = mDateTo.timeInMillis
                mDateTo.timeInMillis = t
            }
        }

        setWeekDays()
        initDaysOff( arrayListOf(Calendar.SATURDAY, Calendar.SUNDAY) )
        initMonths()
    }

    private fun initMonths() {
        // Первый месяц
        months.add(Month(mDateFrom, null, mFirstDayOfWeek))
        // Второй и последующие месяцы
        val month = mDateFrom
        month.set(Calendar.DAY_OF_MONTH, 1)
        month.add(Calendar.MONTH, 1)
        while (month < mDateTo) {
            val aMonth = Month(month, mDateTo, mFirstDayOfWeek)
            months.add(aMonth)
            month.add(Calendar.MONTH, 1)
        }
    }

    fun getCalendarRows(): MutableList<CalendarRow> {
        val calendarRows = mutableListOf<CalendarRow>()
        for (month in months) {
            calendarRows.add(calendarRows.size, MonthRow(month.year, month.monthNum))
            calendarRows.add(calendarRows.size, DayNamesRow(month.year, month.monthNum, weekDays))
            for (week in month.weeks) {
                val daysOffSlice = getDaysOffSlice(month.year, month.monthNum, week.weekDates)
                calendarRows.add(
                    calendarRows.size,
                    DatesRow(month.year, month.monthNum, week.weekDates, daysOffSlice))
            }
        }
        return calendarRows
    }

    fun setFirstDayOfWeek(firstDayOfWeek: Int) {
        mFirstDayOfWeek = firstDayOfWeek
        setWeekDays()
    }

    // Заполнение названий дней недели
    private fun setWeekDays() {
        calendar.firstDayOfWeek = mFirstDayOfWeek
        for (day in mFirstDayOfWeek..7)
            weekDays.add(weekDays.size, dayName(day))
        for (day in 1 until mFirstDayOfWeek)
            weekDays.add(weekDays.size, dayName(day))
    }

    // Формирование дня недели по номеру
    private fun dayName(day: Int, full: Boolean = false): String {
        return if (full)
            DateFormatSymbols.getInstance().weekdays[day]
        else
            DateFormatSymbols.getInstance().shortWeekdays[day]
    }

    // Загрузка нерабочих дней
    fun loadDaysOff(daysOff: MutableSet<Int>) {
        if (daysOff.size > 0)
            mDaysOff = daysOff
    }

    // Получение среза нерабочих дней по заданным датам
    private fun getDaysOffSlice(year: Int, month: Int, dates: IntArray): BooleanArray {
        val daysOff = BooleanArray(7)
        for (day in 0..min(6, dates.size - 1)) {
            val date = year * 10000 + (month + 1) * 100 + dates[day]
            daysOff[day] = mDaysOff.contains(date)
        }
        return daysOff
    }

    // Заполнение нерабочих дней по заданным выходным дням недели
    private fun initDaysOff(weekend: ArrayList<Int>) {
        mDaysOff = mutableSetOf()
        val date = mDateFrom.clone() as Calendar
        while (date < mDateTo) {
            if (weekend.contains( date.get(Calendar.DAY_OF_WEEK) ))
                mDaysOff.add(calendarToInt(date))
            date.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun calendarToInt(date: Calendar): Int {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)
        return year * 10000 + (month + 1) * 100 + dayOfMonth
    }

    // Добавление нерабочих дней
    fun addDaysOff(daysOff: ArrayList<Int>) {
        mDaysOff.addAll(daysOff)
    }

    // Удаление нерабочих дней
    fun removeDaysOff(daysOff: ArrayList<Int>) {
        for (day in daysOff)
            mDaysOff.remove(day)
    }
}