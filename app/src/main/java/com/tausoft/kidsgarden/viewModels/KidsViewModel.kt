package com.tausoft.kidsgarden.viewModels

import android.app.Application
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.data.KidsRepository
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KidsViewModel @Inject constructor(
    private val application: Application,
    private val kidsRepository: KidsRepository
): ViewModel() {

    // Строка-представление текущего периода (месяц-год)
    private val _currentPeriod = MutableLiveData(SpannableString(""))
    val currentPeriod: LiveData<SpannableString> = _currentPeriod
    private fun setCurrentPeriod() {
        val text = SpannableString(CalendarHelper.monthYear(month.value!!, year.value!!))
        _currentPeriod.value = text
        _currentPeriod.value?.setSpan(StyleSpan(Typeface.ITALIC), 0, text.length, 0)
    }

    // Цвет кнопок в зависимости от сезона
    private val _seasonColor = MutableLiveData(0)
    var seasonColor: LiveData<Int> = _seasonColor
    private fun setSeasonColor() {
        _seasonColor.value = getColor(month.value!!)
    }

    // Текущая дата на момент запуска:
    // - год
    private val _year = MutableLiveData(CalendarHelper.currentYear())
    val year: LiveData<Int> = _year
    fun setYear(year: Int) {
        _year.value = year
    }

    // - месяц
    private val _month = MutableLiveData(CalendarHelper.currentMonth())
    val month: LiveData<Int> = _month
    fun setMonth(month: Int) {
        _month.value = month
    }

    // - число
    private val _dayOfMonth = MutableLiveData(CalendarHelper.currentDay())
    var dayOfMonth: LiveData<Int> = _dayOfMonth
    fun setDay(day: Int) {
        _dayOfMonth.value = day
    }

    // Границы текущего (на момент запуска) месяца
    private var _monthFrom = MutableLiveData(0)
    val monthFrom: LiveData<Int> = _monthFrom

    private var _monthTo   = MutableLiveData(0)
    val monthTo: LiveData<Int> = _monthTo

    // Список детей
    var kids: LiveData<List<Kid>> = MutableLiveData(listOf())

    // Набор записей об отсутствии в разрезе детей
    var kidsAbsences: LiveData<Map<Int, List<Absence>>> = MutableLiveData(emptyMap())

    init {
        setSeasonColor()
        setCurrentPeriod()
        updateDates()

        kids = kidsRepository.getKids()
        kidsAbsences = kidsRepository.getAbsences(monthFrom.value!!, monthTo.value!!)
    }

    fun deleteKid(kid: Kid) = kidsRepository.deleteKid(kid)

    fun decMonth() {
        _month.value = _month.value!!.dec()
        if (_month.value!! < 0) {
            _year.value = _year.value!! - 1
            _month.value = 11
        }
        setSeasonColor()
        setCurrentPeriod()
        updateDates()
    }

    fun incMonth() {
        _month.value = _month.value!!.inc()
        if (_month.value!! > 11) {
            _year.value = _year.value!! + 1
            _month.value = 0
        }
        setSeasonColor()
        setCurrentPeriod()
    }

    // Обновить данные по границам месяца и учебного года в связи с выбором новой даты или месяца
    fun updateDates() {
        val aDate = Date(1, month.value!!, year.value!!)
        _monthFrom.value = aDate.toInt()
        _monthTo.value   = aDate.incMonth().toInt()
    }

    private fun getColor(month: Int) =
        getColor( CalendarHelper.getSeason(month) )

    private fun getColor(season: CalendarHelper.Season): Int =
        when (season) {
            CalendarHelper.Season.SPRING -> getColorRes(R.color.spring)
            CalendarHelper.Season.SUMMER -> getColorRes(R.color.summer)
            CalendarHelper.Season.AUTUMN -> getColorRes(R.color.autumn)
            CalendarHelper.Season.WINTER -> getColorRes(R.color.winter)
        }

    private fun getColorRes(resId: Int): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            application.applicationContext.resources.getColor(resId, null)
        else
            @Suppress("DEPRECATION")
            application.applicationContext.resources.getColor(R.color.spring)
}