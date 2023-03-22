package com.tausoft.kidsgarden.util

import android.text.InputFilter
import android.text.Spanned

class MinMaxFilter(minValue: Int, maxValue: Int): InputFilter {
    private var intMin: Int = minValue
    private var intMax: Int = maxValue

    override fun filter(
        source: CharSequence?, start: Int, end: Int, dest: Spanned?, dStart: Int, dEnd: Int
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