package com.tausoft.kidsgarden.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.util.KidsGardenPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NumOfDaysDialog(
    private val paramName: String, number: Int, private val maxValue: Int, private val title: String
    ): DialogFragment() {

    @Inject lateinit var prefs: KidsGardenPrefs

    private var mNumber: Int = 0

    init {
        mNumber = number
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val layout = LinearLayout.inflate(it.baseContext, R.layout.dialog_num_of_days, null)
            builder.setView(layout)

            val numOfDays = layout.findViewById<EditText>(R.id.num_of_days)
            numOfDays.setText(mNumber.toString())
            numOfDays.filters = arrayOf(MinMaxFilter(1, maxValue))
            numOfDays.doOnTextChanged { text, _, _, _ ->
                val aNumber = text.toString()
                if (aNumber.isEmpty())
                    mNumber = 0
                else if (mNumber != aNumber.toInt())
                    mNumber = aNumber.toInt()
            }

            val minusButton = layout.findViewById<Button>(R.id.minus_button)
            minusButton.setOnClickListener {
                if (mNumber > 1) {
                    mNumber--
                    numOfDays.setText(mNumber.toString())
                }
            }

            val plusButton = layout.findViewById<Button>(R.id.plus_button)
            plusButton.setOnClickListener {
                if (mNumber < maxValue) {
                    mNumber++
                    numOfDays.setText(mNumber.toString())
                }
            }

            builder
                .setTitle(title)
                .setPositiveButton("ОК") { _, _ -> prefs.storePref(paramName, mNumber) }
                .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int
        ): CharSequence? {
            try {
                val input = if (end > 0) {
                    // Вставка
                    if (start <= dStart)
                        // вначале фрагмента
                        Integer.parseInt(source.toString() + dest.toString())
                    else
                        // в конце фрагмента
                        Integer.parseInt(dest.toString() + source.toString())
                } else {
                    // Удаление
                    Integer.parseInt(dest.toString() + source.toString())
                }
                if (isInRange(intMin, intMax, input))
                    return null
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int) =
            if (b > a) c in a..b else c in b..a
    }
}