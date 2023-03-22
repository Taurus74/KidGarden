package com.tausoft.kidsgarden.util

import android.content.Context
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.AbsenceType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class AbsencesFormatter @Inject constructor(@ApplicationContext context: Context) {

    private var resources = context.resources

    fun formatAbsences(absences: List<Absence>?): String {
        if (absences == null || absences.isEmpty())
            return noAbsences()

        val builder = StringBuilder("")
        for (absence in absences) {
            if (absence.numOfDays > 0) {
                builder
                    .append(absence.absenceType.toString())
                    .append(" ")
                    .append(absencesDays(absence.numOfDays))
                    .append("\n")
            }
        }
        return if (builder.isEmpty())
            noAbsences()
        else
            builder.toString()
    }

    fun formatAbsences(absences: Map<AbsenceType, Int>?): String {
        if (absences == null || absences.isEmpty())
            return noAbsences()

        val builder = StringBuilder("")
        for (absence in absences) {
            if (absence.value > 0) {
                builder
                    .append(absence.key.toString())
                    .append(" ")
                    .append(absencesDays(absence.value))
                    .append("\n")
            }
        }
        return if (builder.isEmpty())
            noAbsences()
        else
            builder.toString()
    }

    private fun noAbsences() = resources.getString(R.string.no_absences)

    // По дням отсутствия в счет лимита за месяц
    private fun absencesDays(days: Int): String {
        return resources.getQuantityString(R.plurals.plurals_days, days, days)
    }

    // Рассчитать количество дней отсутствия за дни (dateFrom..dateTo)
    // по заданному периоду (periodFrom..periodTo)
    private fun numberOfDays(dateFrom: Int, dateTo: Int, periodFrom: Int, periodTo: Int): Int {
        val date1 = max(dateFrom, periodFrom)
        val date2 = min(dateTo, periodTo)
        return CalendarHelper.dateDiff(date1, date2)
    }

    // Выбрать дату в зависимости от типа отсутствия:
    // отпуск рассчитывается за год, отстальные типы - за месяц
    private fun getDateOfAbsenceType(yearDate: Int, monthDate: Int, absenceType: AbsenceType): Int {
        return when (absenceType) {
            AbsenceType.VACATION -> yearDate
            else -> monthDate
        }
    }
}