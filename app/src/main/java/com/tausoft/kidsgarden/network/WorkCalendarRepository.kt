package com.tausoft.kidsgarden.network

import android.util.Log
import com.tausoft.kidsgarden.data.DayOff
import com.tausoft.kidsgarden.di.ExchangeModule
import com.tausoft.kidsgarden.util.Date
import com.tausoft.kidsgarden.workCalendar.WorkCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkCalendarRepository @Inject constructor(
    private val workCalendarService: WorkCalendarService
) {

    suspend fun getData(year: Int): List<DayOff> = withContext(
        Dispatchers.IO
    ) {
        val workCalendar = workCalendarService.getData(year.toString())
        convert(workCalendar)
    }

    // Конвертер ответа сервера в список выходных дней
    //
    // Месяц 1..12
    //
    // Дни: строка чисел через запятую, без пробелов.
    // Содержит список всех выходных данного месяца.
    // Доп. знаки:
    //   - звездочка (*) - короткий предпраздничный;
    //   - плюс (+) - перенесенный выходной (т.е. данный рабочий день становится выходным).

    private fun convert(response: Response<WorkCalendar>): List<DayOff> {
        val daysOff = mutableListOf<DayOff>()
        if (response.isSuccessful) {
            with (response.body()) {
                for (month in this!!.months) {
                    val days = month.days.split(",")
                    for (day in days) {
                        // короткие предпраздничные дни приравниваем к обычным рабочим
                        // - т.е. пропускаем
                        if (!day.endsWith("*")) {
                            // перенесенные выходные учитываем как обычные выходные
                            val dayInt = day.replace("+", "").toInt()
                            daysOff.add(
                                DayOff( Date(dayInt, month.month - 1, this.year).toInt() )
                            )
                        }
                    }
                }
            }
        }
        else
            Log.e(ExchangeModule.TAG, response.errorBody().toString())
        return daysOff
    }
}