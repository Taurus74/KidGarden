package com.tausoft.kidsgarden.util

import android.content.Context
import com.tausoft.kidsgarden.data.AbsenceType

class AbsencesHelper(context: Context) {
    private var prefs = KidsGardenPrefs(context)

    fun checkLimits(absenceType: AbsenceType, days: Int): Boolean {
        // Задать значение лимита дней для каждого типа отсутствия.
        // 0 означает без ограничений.
        val limits = mapOf(
            AbsenceType.ABSENCE to prefs.pref(KidsGardenPrefs.KEY_ABSENCE_DAYS_PER_MONTH),
            AbsenceType.VACATION to prefs.pref(KidsGardenPrefs.KEY_ABSENCE_DAYS_PER_YEAR)
        )

        return if (limits[absenceType] == null)
            true
        else if (limits[absenceType] == 0)
            true
        else
            days <= limits[absenceType]!!
    }

    // Возвращает начальную дату для расчета лимита дней - в зависимости от типа периода
    fun limitDateFrom(absenceType: AbsenceType, date: Date): Int {
        return if (absenceType == AbsenceType.VACATION) {
            val prefValue = prefs.pref(KidsGardenPrefs.KEY_YEAR_BEGIN)
            if (prefValue <= date.month * 100 + date.day)
                date.year * 10000 + prefValue
            else
                (date.year - 1) * 10000 + prefValue
        } else {
            Date(1, date.month, date.year).toInt()
        }
    }

    // Возвращает конечную дату для расчета лимита дней - в зависимости от типа периода
    fun limitDateTo(absenceType: AbsenceType, date: Date): Int {
        return if (absenceType == AbsenceType.VACATION) {
            val prefValue = prefs.pref(KidsGardenPrefs.KEY_YEAR_END)
            if (prefValue < date.month * 100 + date.day)
                (date.year + 1) * 10000 + prefValue
            else
                date.year * 10000 + prefValue
        } else {
            Date(CalendarHelper.maxDayOfMonth(date.month, date.year), date.month, date.year)
                .toInt() - 1
        }
    }
}