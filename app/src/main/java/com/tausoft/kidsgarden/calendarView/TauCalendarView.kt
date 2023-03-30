package com.tausoft.kidsgarden.calendarView

import com.tausoft.kidsgarden.calendarView.ui.CalendarRow
import com.tausoft.kidsgarden.calendarView.ui.DatesRow
import com.tausoft.kidsgarden.calendarView.ui.DayNamesRow
import com.tausoft.kidsgarden.calendarView.ui.MonthRow
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class TauCalendarView(firstDayOfWeek: Int = Calendar.MONDAY) {
    private var mFirstDayOfWeek: Int = firstDayOfWeek

    private val calendar = Calendar.getInstance()
    private var mDateFrom = calendar.clone() as Calendar
    private var mDateTo   = calendar.clone() as Calendar
    private var weekDays: ArrayList<String> = arrayListOf()
    private var months: ArrayList<Month> = arrayListOf()

    // Набор нерабочих (выходные, праздники, в т.ч. перенесённые) дней
    // День указывается целым числом в формате ГГГГММДД
    private var mDaysOff: MutableSet<Int> = mutableSetOf()

    fun setYear(year: Int) {
        mDateFrom = Calendar.getInstance()
        if (year > 0)
            mDateFrom.set(Calendar.YEAR, year)
        mDateFrom.set(Calendar.DAY_OF_YEAR, 1)

        mDateTo   = Calendar.getInstance()
        if (year > 0)
            mDateTo.set(Calendar.YEAR, year)
        mDateTo.set(Calendar.DAY_OF_YEAR, mDateTo.getActualMaximum(Calendar.DAY_OF_YEAR))

        setWeekDays()
        initDaysOff( arrayListOf(Calendar.SATURDAY, Calendar.SUNDAY) )
        initMonths()
    }

    private fun initMonths() {
        months.clear()
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

    @Suppress("unused")
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
    fun loadDaysOff(daysOff: List<Int>) {
        if (daysOff.isNotEmpty())
            mDaysOff = daysOff.toMutableSet()
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
        mDaysOff.clear()
        val date = mDateFrom.clone() as Calendar
        while (date <= mDateTo) {
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
    @Suppress("unused")
    fun addDaysOff(daysOff: ArrayList<Int>) = mDaysOff.addAll(daysOff)

    // Удаление нерабочих дней
    @Suppress("unused")
    fun removeDaysOff(daysOff: ArrayList<Int>) {
        for (day in daysOff)
            mDaysOff.remove(day)
    }
}