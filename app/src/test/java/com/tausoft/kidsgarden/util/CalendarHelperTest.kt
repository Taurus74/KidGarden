package com.tausoft.kidsgarden.util

import org.junit.Assert.*
import org.junit.Test
import java.util.*

internal class CalendarHelperTest {

    @Test
    fun getSeason() {
        assertEquals(CalendarHelper.Season.WINTER, CalendarHelper.getSeason(11))
        assertEquals(CalendarHelper.Season.WINTER, CalendarHelper.getSeason(1))
        assertEquals(CalendarHelper.Season.WINTER, CalendarHelper.getSeason(0))
        assertEquals(CalendarHelper.Season.SPRING, CalendarHelper.getSeason(2))
        assertEquals(CalendarHelper.Season.SPRING, CalendarHelper.getSeason(4))
        assertEquals(CalendarHelper.Season.SUMMER, CalendarHelper.getSeason(5))
        assertEquals(CalendarHelper.Season.SUMMER, CalendarHelper.getSeason(7))
        assertEquals(CalendarHelper.Season.AUTUMN, CalendarHelper.getSeason(8))
        assertEquals(CalendarHelper.Season.AUTUMN, CalendarHelper.getSeason(10))
        assertEquals(CalendarHelper.Season.WINTER, CalendarHelper.getSeason(-1))
        assertEquals(CalendarHelper.Season.WINTER, CalendarHelper.getSeason(12))
        assertNotEquals(CalendarHelper.Season.WINTER, CalendarHelper.getSeason(2))
    }

    @Test
    fun currentDay() {
        assertEquals(
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            CalendarHelper.currentDay())

        assertNotEquals(0, CalendarHelper.currentDay())
        assertNotEquals(32, CalendarHelper.currentDay())
    }

    @Test
    fun currentMonth() {
        assertEquals(
            Calendar.getInstance().get(Calendar.MONTH),
            CalendarHelper.currentMonth())
        assertEquals(0, CalendarHelper.currentMonth(0))
        assertEquals(7, CalendarHelper.currentMonth(7))
        assertEquals(11, CalendarHelper.currentMonth(11))

        assertNotEquals(-1, CalendarHelper.currentMonth(-1))
        assertNotEquals(-1, CalendarHelper.currentMonth())
    }

    @Test
    fun currentYear() {
        assertEquals(
            Calendar.getInstance().get(Calendar.YEAR),
            CalendarHelper.currentYear())
        assertEquals(2023, CalendarHelper.currentYear(2023))

        assertNotEquals(2023, CalendarHelper.currentYear(2022))
        assertNotEquals(23, CalendarHelper.currentYear(2023))
    }

    @Test
    fun monthYear() {
        assertEquals("Январь 2023 г.", CalendarHelper.monthYear(0, 2023))
        assertEquals("Июль 2023 г.", CalendarHelper.monthYear(6, 2023))
        assertEquals("Декабрь 2023 г.", CalendarHelper.monthYear(11, 2023))
        // Правильно, хотя в данном проекте не используется
        assertEquals("Июль 23 г.", CalendarHelper.monthYear(6, 23))

        assertNotEquals("Январь 2023 г.", CalendarHelper.monthYear(1, 2023))
        assertNotEquals("Декабрь 2023 г.", CalendarHelper.monthYear(12, 2023))
    }

    @Test
    fun getMonths() {
        val months = CalendarHelper.getMonths(Locale("ru"))
        assertArrayEquals(
            arrayOf("января","февраля","марта","апреля","мая","июня",
                "июля","августа","сентября","октября","ноября","декабря",""),
            months)
    }

    @Test
    fun maxDayOfMonth() {
        assertEquals(31, CalendarHelper.maxDayOfMonth(0))
        assertEquals(31, CalendarHelper.maxDayOfMonth(0, 2023))
        assertEquals(30, CalendarHelper.maxDayOfMonth(5))
        assertEquals(30, CalendarHelper.maxDayOfMonth(5, 2023))
        assertEquals(31, CalendarHelper.maxDayOfMonth(11))
        assertEquals(31, CalendarHelper.maxDayOfMonth(11, 2023))
        assertEquals(28, CalendarHelper.maxDayOfMonth(1, 2023))
        assertEquals(29, CalendarHelper.maxDayOfMonth(1, 2024))

        assertNotEquals(29, CalendarHelper.maxDayOfMonth(1, 2023))
        assertNotEquals(28, CalendarHelper.maxDayOfMonth(1, 2024))
    }

    @Test
    fun dayMonthToInt() {
        assertEquals(101, CalendarHelper.dayMonthToInt(1, 1))
        assertEquals(1231, CalendarHelper.dayMonthToInt(31, 12))
        assertEquals(9999, CalendarHelper.dayMonthToInt(99, 99))
    }

    @Test
    fun intToStringDate() {
        assertEquals("1 января", CalendarHelper.intToStringDate(101))
        assertEquals("31 декабря", CalendarHelper.intToStringDate(1231))
        assertEquals("1 января", CalendarHelper.intToStringDate(20230101))
        assertEquals("31 декабря", CalendarHelper.intToStringDate(20231231))

        assertNotEquals("1 января", CalendarHelper.intToStringDate(1))
        assertNotEquals("1 января", CalendarHelper.intToStringDate(2023101))
        assertNotEquals("31 декабря", CalendarHelper.intToStringDate(1232))
        assertNotEquals("31 декабря", CalendarHelper.intToStringDate(1131))
    }

    @Test
    fun dateDiff() {
        assertEquals(1, CalendarHelper.dateDiff(20230101, 20230101))
        assertEquals(2, CalendarHelper.dateDiff(20221231, 20230101))
        assertEquals(1, CalendarHelper.dateDiff(101, 101))
        assertEquals(2, CalendarHelper.dateDiff(20230101, 20221231))
        assertEquals(365, CalendarHelper.dateDiff(20230101, 20231231))
        assertEquals(366, CalendarHelper.dateDiff(20240101, 20241231))

        assertNotEquals(0, CalendarHelper.dateDiff(20230101, 20230101))
        assertNotEquals(1, CalendarHelper.dateDiff(20221231, 20230101))
    }
}