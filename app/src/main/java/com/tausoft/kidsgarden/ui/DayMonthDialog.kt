package com.tausoft.kidsgarden.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.Date
import com.tausoft.kidsgarden.util.KidsGardenPrefs
import com.tausoft.kidsgarden.util.MinMaxFilter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DayMonthDialog(private val paramName: String, date: Int, private val title: String
    ): DialogFragment() {

    @Inject lateinit var prefs: KidsGardenPrefs

    private var mDay: Int = 0
    private var mMonth: Int = 0

    init {
        val dayMonth = Date().fromInt(date)
        mDay = dayMonth.day
        mMonth = dayMonth.month + 1
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val layout = LinearLayout.inflate(it.baseContext, R.layout.dialog_day_month, null)
            builder.setView(layout)

            var maxDay = CalendarHelper.maxDayOfMonth(mMonth)

            val day = layout.findViewById<EditText>(R.id.day)
            day.setText(mDay.toString())
            day.filters = arrayOf(MinMaxFilter(1, maxDay))
            day.doOnTextChanged { text, _, _, _ ->
                val aDay = text.toString()
                if (aDay.isEmpty())
                    mDay = 0
                else if (mDay != aDay.toInt())
                    mDay = aDay.toInt()
            }

            val minusButton = layout.findViewById<Button>(R.id.minus_button)
            minusButton.setOnClickListener {
                if (mDay > 1) {
                    mDay--
                    day.setText(mDay.toString())
                }
            }

            val plusButton = layout.findViewById<Button>(R.id.plus_button)
            plusButton.setOnClickListener {
                if (mDay < maxDay) {
                    mDay++
                    day.setText(mDay.toString())
                }
            }

            val spinner = layout.findViewById<Spinner>(R.id.month)
            spinner.adapter = ArrayAdapter(
                builder.context,
                android.R.layout.simple_spinner_dropdown_item,
                CalendarHelper.getMonths()
            )
            spinner.setSelection(mMonth - 1)
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mMonth = position + 1
                    maxDay = CalendarHelper.maxDayOfMonth(mMonth)
                    day.filters = arrayOf(MinMaxFilter(1, maxDay))
                    if (mDay > maxDay) {
                        mDay = maxDay
                        day.setText(mDay.toString())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            builder
                .setTitle(title)
                .setPositiveButton("ОК") { _, _ ->
                    prefs.storePref(paramName, CalendarHelper.dayMonthToInt(mDay, mMonth))
                }
                .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}