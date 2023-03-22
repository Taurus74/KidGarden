package com.tausoft.kidsgarden.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.AbsenceType
import com.tausoft.kidsgarden.data.AbsencesDataSource
import com.tausoft.kidsgarden.data.DaysOffDataSource
import com.tausoft.kidsgarden.di.DatabaseAbsences
import com.tausoft.kidsgarden.di.DatabaseDaysOff
import com.tausoft.kidsgarden.ui.MainActivity.Companion.ABSENCE_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_FROM
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_TO
import com.tausoft.kidsgarden.ui.MainActivity.Companion.TAG
import com.tausoft.kidsgarden.util.AbsencesHelper
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.Date
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEditAbsenceFragment : Fragment() {
    @DatabaseAbsences
    @Inject lateinit var absencesDataSource: AbsencesDataSource
    @DatabaseDaysOff
    @Inject lateinit var daysOffDataSource: DaysOffDataSource

    private lateinit var dateFrom: TextInputEditText
    private lateinit var dateTo:   TextInputEditText
    private lateinit var spinner:  Spinner

    private lateinit var absence: Absence
    private var kidId = 0
    private var absenceId = 0

    private lateinit var listener: DatePickerDialog.OnDateSetListener

    companion object {
        private const val PICKER_DIALOG_FROM = "PICKER_DIALOG_FROM"
        private const val PICKER_DIALOG_TO   = "PICKER_DIALOG_TO"
    }

    // Границы отсутствия
    private var aYearFrom  = CalendarHelper.currentYear()
    private var aMonthFrom = CalendarHelper.currentMonth()
    private var aDayFrom   = CalendarHelper.currentDay()

    private var aYearTo  = CalendarHelper.currentYear()
    private var aMonthTo = CalendarHelper.currentMonth()
    private var aDayTo   = CalendarHelper.currentDay()

    // Границы месяца
    private var monthFrom: Int = 0
    private var monthTo:   Int = 0

    private var absenceType = AbsenceType.ABSENCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            kidId = it.getInt(KID_ID)
            monthFrom = it.getInt(MONTH_FROM)
            monthTo   = it.getInt(MONTH_TO)
            if (kidId > 0) {
                absenceId = it.getInt(ABSENCE_ID)
                if (absenceId == 0)
                    absence = Absence(kidId)
                else {
                    absencesDataSource.getAbsence(kidId, absenceId) {
                        absence = it
                        if (absenceId > 0) {
                            spinner.setSelection(
                                (spinner.adapter as ArrayAdapter<Absence>).getPosition(absence)
                            )
                        }
                        dateFrom.setText( CalendarHelper.intToStringDate(it.dateFrom) )
                        dateTo.  setText( CalendarHelper.intToStringDate(it.dateTo) )

                        updateDates(it.dateFrom, it.dateTo)
                    }
                }
            }
            else {
                Log.e(TAG, "Wrong parameter: kidId = 0")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            when (view?.tag) {
                PICKER_DIALOG_FROM -> {
                    aYearFrom  = year
                    aMonthFrom = month
                    aDayFrom   = dayOfMonth
                    dateFrom.setText(
                        CalendarHelper.intToStringDate(Date(dayOfMonth, month, year).toInt())
                    )
                }
                PICKER_DIALOG_TO -> {
                    aYearTo = year
                    aMonthTo = month
                    aDayTo = dayOfMonth
                    dateTo.setText(
                        CalendarHelper.intToStringDate(Date(dayOfMonth, month, year).toInt())
                    )
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit_absence, container, false)
        spinner = view.findViewById(R.id.absences_type)
        spinner.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, AbsenceType.values()
        )
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                absenceType = spinner.adapter?.getItem(position) as AbsenceType
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dateFrom = view.findViewById(R.id.date_from)
        dateTo   = view.findViewById(R.id.date_to)

        dateFrom.setOnClickListener {
            val datePickerDialogFrom = DatePickerDialog(
                requireContext(), listener, aYearFrom, aMonthFrom, aDayFrom)
            datePickerDialogFrom.datePicker.tag = PICKER_DIALOG_FROM
            datePickerDialogFrom.show()
        }

        dateTo.setOnClickListener {
            val datePickerDialogTo = DatePickerDialog(
                requireContext(), listener, aYearTo, aMonthTo, aDayTo)
            datePickerDialogTo.datePicker.tag = PICKER_DIALOG_TO
            datePickerDialogTo.show()
        }

        view.findViewById<MaterialButton>(R.id.OK_button).setOnClickListener {
            // Проверка заполнения
            if (dateFrom.text!!.isEmpty()) {
                Toast.makeText(requireContext(),
                    resources.getString(R.string.date_is_empty, "начальную"),
                    Toast.LENGTH_SHORT).show()
            }
            else if (dateTo.text!!.isEmpty()) {
                Toast.makeText(requireContext(),
                    resources.getString(R.string.date_is_empty, "конечную"),
                    Toast.LENGTH_SHORT).show()
            }
            // Обработка
            else {
                val aDateFrom = Date().toInt(aDayFrom, aMonthFrom, aYearFrom)
                val aDateTo   = Date().toInt(aDayTo, aMonthTo, aYearTo)
                // Проверка правильности выбора дат
                if (checkDates(aDateFrom, aDateTo)) {
                    // Проверка на пересечение нового периода с уже введенными
                    absencesDataSource.checkCrossing(kidId, absenceId, aDateFrom, aDateTo) {
                        if (it == 0) {
                            // Расчет кол-ва дней
                            daysOffDataSource.getDaysOffCount(aDateFrom, aDateTo) { daysOffCount ->
                                // Вычесть нерабочие дни
                                val numOfDays =
                                    CalendarHelper.dateDiff(aDateFrom, aDateTo) - daysOffCount
                                val limitFrom = AbsencesHelper(requireContext())
                                    .limitDateFrom(absenceType, Date().fromInt(aDateFrom))
                                val limitTo = AbsencesHelper(requireContext())
                                    .limitDateTo(absenceType, Date().fromInt(aDateTo))
                                // Проверка лимита по количеству дней в зав-ти от типа отсутствия
                                absencesDataSource.getSumKidAbsence(
                                    kidId, absenceId, absenceType, limitFrom, limitTo
                                ) { sum ->
                                    if (AbsencesHelper(requireContext().applicationContext)
                                            .checkLimits(absenceType, numOfDays + sum)) {
                                        addAbsence(aDateFrom, aDateTo, numOfDays)
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                    else
                                        Toast.makeText(requireContext(),
                                            "Лимит дней превышен", Toast.LENGTH_SHORT
                                        ).show()
                                }
                            }
                        }
                        else
                            Toast.makeText(requireContext(),
                                resources.getText(R.string.has_crossing),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.cancel_button).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun checkDates(dateFrom: Int, dateTo: Int): Boolean {
        if (dateFrom > dateTo) {
            Toast.makeText(requireContext(),
                resources.getText(R.string.wrong_dates), Toast.LENGTH_SHORT
            ).show()
            return false
        }
        // Проверка: введенные даты из одного месяца
        else {
            val mDateFrom = Date().fromInt(dateFrom)
            val mDateTo   = Date().fromInt(dateTo)
            return if ((mDateFrom.month == mDateTo.month) && (mDateFrom.year == mDateTo.year))
                true
            else {
                Toast.makeText(requireContext(),
                    resources.getText(R.string.wrong_month),
                    Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private fun addAbsence(aDateFrom: Int, aDateTo: Int, numOfDays: Int) {
        absence.dateFrom = aDateFrom
        absence.dateTo   = aDateTo
        absence.absenceType = absenceType
        absence.numOfDays = numOfDays
        absencesDataSource.addAbsence(absence)
    }

    private fun updateDates(aDateFrom: Int, aDateTo: Int) {
        val mDateFrom = Date().fromInt(aDateFrom)
        aYearFrom  = mDateFrom.year
        aMonthFrom = mDateFrom.month
        aDayFrom   = mDateFrom.day

        val mDateTo = Date().fromInt(aDateTo)
        aYearTo  = mDateTo.year
        aMonthTo = mDateTo.month
        aDayTo   = mDateTo.day
    }
}