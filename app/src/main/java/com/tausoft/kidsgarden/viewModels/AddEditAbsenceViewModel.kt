package com.tausoft.kidsgarden.viewModels

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.AbsenceType
import com.tausoft.kidsgarden.data.DaysOffRepository
import com.tausoft.kidsgarden.data.KidsRepository
import com.tausoft.kidsgarden.util.*
import com.tausoft.kidsgarden.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class AddEditAbsenceViewModel @Inject constructor(
    private val kidsRepository: KidsRepository,
    private val daysOffRepository: DaysOffRepository
): ObservableViewModel() {

    // Текущий месяц
    private var _year = MutableLiveData( CalendarHelper.currentYear() )
    val year: LiveData<Int> = _year
    fun setYear(year: Int) {
        _year.value = year
    }

    private var _month = MutableLiveData( CalendarHelper.currentMonth() )
    val month: LiveData<Int> = _month
    fun setMonth(month: Int) {
        _month.value = month
    }

    private var _defaultDate = MutableLiveData( getDate(
        CalendarHelper.currentDay(),
        month.value ?: CalendarHelper.currentMonth(),
        year.value ?: CalendarHelper.currentYear()
    ) )

    private val defaultDate: LiveData<Int> = _defaultDate

    // id ребенка
    private var _kidId = MutableLiveData(0)
    val kidId: LiveData<Int> = _kidId
    fun setKidId(kidId: Int) {
        _kidId.value = kidId
    }

    private val _kidName = MutableLiveData("")
    val kidName: LiveData<String> = _kidName
    private fun setKidName(value: String) {
        _kidName.value = value
    }

    // id записи об отсутствии
    private var _id = MutableLiveData(0)
    val id: LiveData<Int> = _id
    fun setId(id: Int) {
        _id.value = id
    }

    // Запись об отсутствии
    var absence = Absence()

    // Границы отсутствия
    private var _aDateFrom = MutableLiveData(defaultDate.value!!)
    private val aDateFrom: LiveData<Int> = _aDateFrom
    fun setDateFrom(date: Int) {
        _aDateFrom.value = date
    }
    fun getDateFromPickerDialog(context: Context, listener: OnDateSetListener) =
        getDatePickerDialog( context, listener, Date().fromInt(aDateFrom.value!!) )

    private var _aDateTo = MutableLiveData(defaultDate.value!!)
    private val aDateTo: LiveData<Int> = _aDateTo
    fun setDateTo(date: Int) {
        _aDateTo.value = date
    }
    fun getDateToPickerDialog(context: Context, listener: OnDateSetListener) =
        getDatePickerDialog( context, listener, Date().fromInt(aDateTo.value!!) )

    // и текстовое представление
    private var _dateFrom = MutableLiveData("")
    val dateFrom: LiveData<String> = _dateFrom

    private var _dateTo = MutableLiveData("")
    val dateTo: LiveData<String> = _dateTo

    // Тип отсутствия
    // - набор доступных значений
    private var _absenceTypes = MutableLiveData(AbsenceType.values().toList())
    val absenceTypes: LiveData<List<AbsenceType>> = _absenceTypes

    // - выбранное значение
    private var _absenceTypeStr = MutableLiveData(AbsenceType.ABSENCE.toString())
    val absenceTypeStr: LiveData<String> = _absenceTypeStr
    fun setAbsenceTypeStr(value: String) {
        _absenceTypeStr.value = value
    }

    var absenceType: AbsenceType = AbsenceType.ABSENCE
        @Bindable set(value) {
            if (field != value)
                field = value
        }

    init {
        _id.observeForever {
            if (it > 0) {
                kidsRepository.getAbsence(it).observeForever { _absence ->
                    absence = _absence
                    absenceType = _absence.absenceType
                    setAbsenceTypeStr( _absence.absenceType.toString() )
                    setDateFrom(_absence.dateFrom)
                    setDateTo  (_absence.dateTo)
                }
            }
        }
        _month.observeForever {
            _defaultDate.value = getDate(
                CalendarHelper.currentDay(), it,year.value ?: CalendarHelper.currentYear()
            )
        }
        _year.observeForever {
            _defaultDate.value = getDate(
                CalendarHelper.currentDay(),month.value ?: CalendarHelper.currentMonth(), it
            )
        }
        _defaultDate.observeForever {
            _aDateFrom.value = it
            _aDateTo.value   = it
        }
        _aDateFrom.observeForever {
            _dateFrom.value = formatDate( it )
        }
        _aDateTo.observeForever {
            _dateTo.value = formatDate(it)
        }
        _kidId.observeForever {
            if (it > 0)
                kidsRepository.getKid(it).observeForever { kid ->
                    setKidName( kid?.name ?: "" )
                }
        }
    }

    private fun formatDate(date: Int): String {
        return if (date == 0)
            ""
        else {
            val aDate = Date().fromInt(date)
            val sb = StringBuilder("")
            Formatter(sb, Locale.getDefault()).format(
                Locale.getDefault(),
                "%02d.%02d.%04d", aDate.day, aDate.month + 1, aDate.year)
            sb.toString()
        }
    }

    fun addAbsence(context: Context): Boolean {
        val numOfDays = countNumOfDays()
        val limitOk = checkLimits(context, numOfDays)
        return if (limitOk) {
            absence.id = id.value!!
            absence.kidId = kidId.value!!
            absence.dateFrom = aDateFrom.value!!
            absence.dateTo = aDateTo.value!!
            absence.numOfDays = numOfDays
            absence.absenceType = absenceType
            kidsRepository.addAbsence(absence)
            true
        } else
            false
    }

    private fun getDatePickerDialog(context: Context, listener: OnDateSetListener, date: Date) =
        DatePickerDialog( context, listener, date.year, date.month, date.day )

    // ----------
    // Проверки:
    // - начальная дата не позже конечной
    fun checkDatesOrder() = aDateFrom.value!! <= aDateTo.value!!

    // - введённые даты из одного месяца
    fun sameMonthDates() =
        Date().fromInt(aDateFrom.value!!).month == Date().fromInt(aDateTo.value!!).month

    // - на пересечение нового периода с уже введёнными
    fun checkCrossing() = kidsRepository
        .checkCrossing(kidId.value!!, id.value!!, aDateFrom.value!!, aDateTo.value!!)

    // - лимита по количеству дней в зав-ти от типа отсутствия
    private fun checkLimits(context: Context, numOfDays: Int): Boolean {
        // Получить даты начала и окончания периода в зависимости от заданной даты и типа отсутствия
        val limitFrom = AbsencesHelper(context)
            .limitDateFrom(absenceType, Date().fromInt(aDateFrom.value!!))
        val limitTo = AbsencesHelper(context)
            .limitDateTo(absenceType, Date().fromInt(aDateTo.value!!))

        // Получить остаток лимита
        val sum = kidsRepository.getSumKidAbsence(kidId.value!!, id.value!!, absenceType,
            limitFrom, limitTo)

        // Проверить и вернуть результат
        return AbsencesHelper(context).checkLimits(absenceType, numOfDays + sum)
    }
    // Проверки
    // ----------

    // Расчёт кол-ва дней
    private fun countNumOfDays() =
        CalendarHelper.dateDiff(aDateFrom.value!!, aDateTo.value!!) -
            // Вычесть нерабочие дни
            daysOffRepository.getDaysOffCount(aDateFrom.value!!, aDateTo.value!!)

    // Получить корректную дату по заданным параметрам
    private fun getDate(day: Int, month: Int, year: Int): Int {
        val maxDayOfMonth = CalendarHelper.maxDayOfMonth(month, year)
        return Date( min(day, maxDayOfMonth), month, year ).toInt()
    }
}