package com.tausoft.kidsgarden.util

import java.text.DateFormatSymbols
import java.util.*
import kotlin.math.max
import kotlin.math.min

// Вспомогательный объект.
// Функции работы с календарем для интерфейса
object CalendarHelper {
    private val MONTHS = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

    // Времена года
    enum class Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }

    // Получить время года по месяцу
    fun getSeason(month: Int): Season =
        when (month) {
            2, 3, 4 -> Season.SPRING
            5, 6, 7 -> Season.SUMMER
            8, 9, 10 -> Season.AUTUMN
            else -> Season.WINTER
        }

    // Получить текущее число месяца
    fun currentDay(): Int {
        val calendar = Calendar.getInstance()
        return calendar[Calendar.DAY_OF_MONTH]
    }

    // Получить текущий месяц
    fun currentMonth(month: Int = -1): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, currentYear())
        if (month in 0..11) 
            calendar.set(Calendar.MONTH, month)
        return calendar[Calendar.MONTH]
    }

    // Получить текущий год
    fun currentYear(year: Int = 0): Int {
        val calendar = Calendar.getInstance()
        if (year != 0)
            calendar.set(year, 1, 1)
        return calendar[Calendar.YEAR]
    }

    // Получить "внутреннее" название месяца в именительном падеже
    private fun monthName(month: Int): String {
        return if (month in 0..11)
            MONTHS[month]
        else
            ""
    } 

    // Получить строку вида "Месяц год"
    fun monthYear(month: Int, year: Int): String {
        return "${monthName(month)} $year г."
    }

    // Получить массив названий месяцев
    fun getMonths(locale: Locale = Locale.getDefault()): Array<String> {
        val symbols = DateFormatSymbols.getInstance(locale)
        return symbols.months
    }

    // Получить максимальное число месяца для заданных месяца и года
    fun maxDayOfMonth(month: Int, year: Int = 0): Int {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear(year), month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Преобразовать день и месяц в число формата ММДД
    fun dayMonthToInt(day: Int, month: Int) = month * 100 + day

    // Преобразовать дату в формате ГГГГММДД в строку
    fun intToStringDate(intDate: Int): String {
        val date = Date().fromInt(intDate)
        return if (date.month in 0..11)
            "${date.day} ${getMonths()[date.month]}"
        else
            ""
    }

    // Вычислить разницу между датами, заданными числами в формате ГГГГММДД
    // К результату добавляется единица - для учета начального дня
    fun dateDiff(intDate1: Int, intDate2: Int): Int {
        val calendar1 = intToCalendar(min(intDate1, intDate2))
        val calendar2 = intToCalendar(max(intDate1, intDate2))

        return (calendar2.timeInMillis - calendar1.timeInMillis)
            .div(24*60*60*1000)
            .toInt() + 1
    }

    // Получить объект типа Calendar с установленной датой, переданной в формате ГГГГММДД
    private fun intToCalendar(intDate: Int): Calendar {
        val date = Date().fromInt(intDate)
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.month, date.day)
        return calendar
    }
}