package com.tausoft.kidsgarden.viewModels

import android.app.Application
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.data.KidsRepository
import com.tausoft.kidsgarden.ui.MainActivity
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
    private fun setYear(year: Int) {
        _year.value = year
    }

    // - месяц
    private val _month = MutableLiveData(CalendarHelper.currentMonth())
    val month: LiveData<Int> = _month
    private fun setMonth(month: Int) {
        _month.value = month
    }

    // - число
    private val _dayOfMonth = MutableLiveData(CalendarHelper.currentDay())
    var dayOfMonth: LiveData<Int> = _dayOfMonth
    private fun setDay(day: Int) {
        _dayOfMonth.value = day
    }

    // Список детей
    private var _kids = MutableLiveData<List<Kid>>(listOf())
    var kids: LiveData<List<Kid>> = _kids

    // Набор записей об отсутствии в разрезе детей
    private var _kidsAbsences = MutableLiveData<Map<Int, List<Absence>>>(emptyMap())
    var kidsAbsences: LiveData<Map<Int, List<Absence>>> = _kidsAbsences

    init {
        year.observeForever {
            setCurrentPeriod()
        }
        month.observeForever {
            setSeasonColor()
            setCurrentPeriod()
        }
        currentPeriod.observeForever {
            kidsRepository.getKids().observeForever {
                _kids.value = it
            }
            val aDate = Date(1, month.value!!, year.value!!)
            kidsRepository.getAbsences(aDate.toInt(), aDate.incMonth().toInt()).observeForever {
                _kidsAbsences.value = it
            }
        }
    }

    fun deleteKid(kid: Kid) = kidsRepository.deleteKid(kid)

    fun decMonth() {
        _month.value = _month.value!!.dec()
        if (_month.value!! < 0) {
            _year.value = _year.value!! - 1
            _month.value = 11
        }
    }

    fun incMonth() {
        _month.value = _month.value!!.inc()
        if (_month.value!! > 11) {
            _year.value = _year.value!! + 1
            _month.value = 0
        }
    }

    fun onPeriodChange(year: Int, month: Int, dayOfMonth: Int = 0) {
        setYear(year)
        setMonth(month)
        setDay(dayOfMonth)
    }

    private fun getColor(month: Int) = getColor( CalendarHelper.getSeason(month) )

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
            application.applicationContext.resources.getColor(resId)

    fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt(MainActivity.YEAR,  year.value!!)
        bundle.putInt(MainActivity.MONTH, month.value!!)
        return bundle
    }
}