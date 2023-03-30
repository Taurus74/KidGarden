package com.tausoft.kidsgarden.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tausoft.kidsgarden.calendarView.TauCalendarView
import com.tausoft.kidsgarden.calendarView.ui.CalendarRow
import com.tausoft.kidsgarden.data.DayOff
import com.tausoft.kidsgarden.data.DaysOffRepository
import com.tausoft.kidsgarden.network.WorkCalendarRepository
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkCalendarViewModel @Inject internal constructor(
    private val workCalendarRepository: WorkCalendarRepository,
    private val daysOffRepository: DaysOffRepository
    ) : ViewModel() {

    private val _year = MutableLiveData( CalendarHelper.currentYear() )
    val year: LiveData<Int> = _year

    private val calendar = TauCalendarView()

    private var _calendarRows = MutableLiveData (listOf<CalendarRow>() )
    var calendarRows: LiveData<List<CalendarRow>> = _calendarRows

    init {
        year.observeForever {
            updateCalendar(it)
        }
    }

    private fun updateCalendar(year: Int) {
        calendar.setYear(year)
        val aYearFrom = Date(mYear = year).toInt()
        val aYearTo   = Date(mYear = year.inc()).toInt()
        getDaysOff(aYearFrom, aYearTo).observeForever {daysOff ->
            val intDaysOff = mutableListOf <Int>()
            daysOff.forEach {
                intDaysOff.add(it.day)
            }
            calendar.loadDaysOff( intDaysOff )
            _calendarRows.value = calendar.getCalendarRows()
        }
    }

    // Загрузить данные о нерабочих днях с сервера
    // и записать в локальную базу
    fun listOfDays() {
        viewModelScope.launch {
            workCalendarRepository
                .getData(year.value ?: CalendarHelper.currentYear())
                .forEach {
                    addDayOff(it)
                }
        }
    }

    fun decYear() {
        _year.value = _year.value!!.dec()
    }

    fun incYear() {
        _year.value = _year.value!!.inc()
    }

    // Записать нерабочий день в локальную базу
    private fun addDayOff(day: DayOff) = daysOffRepository.addDayOff(day)

    // Получить нерабочие дни за период
    private fun getDaysOff(yearFrom: Int, yearTo: Int) =
        daysOffRepository.getDaysOff(yearFrom, yearTo)

}