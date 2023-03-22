package com.tausoft.kidsgarden.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.tausoft.kidsgarden.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KidsGardenPrefs @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        const val KEY_YEAR_BEGIN = "YEAR_BEGIN"
        const val KEY_YEAR_END   = "YEAR_END"
        const val KEY_ABSENCE_DAYS_PER_MONTH = "ABSENCE_DAYS_PER_MONTH"
        const val KEY_ABSENCE_DAYS_PER_YEAR  = "ABSENCE_DAYS_PER_YEAR"
        const val KEY_MAX_ABSENCE_DAYS_PER_MONTH = "MAX_ABSENCE_DAYS_PER_MONTH"
        const val KEY_MAX_ABSENCE_DAYS_PER_YEAR  = "MAX_ABSENCE_DAYS_PER_YEAR"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val prefs: MutableMap<String, Int> = mutableMapOf()

    init {
        prefs[KEY_YEAR_BEGIN] = sharedPreferences
            .getInt(KEY_YEAR_BEGIN,
                context.resources.getInteger(R.integer.default_year_begin))
        prefs[KEY_YEAR_END] = sharedPreferences
            .getInt(KEY_YEAR_END,
                context.resources.getInteger(R.integer.default_year_end))
        prefs[KEY_ABSENCE_DAYS_PER_MONTH] = sharedPreferences
            .getInt(KEY_ABSENCE_DAYS_PER_MONTH,
                context.resources.getInteger(R.integer.default_absence_days_per_month))
        prefs[KEY_ABSENCE_DAYS_PER_YEAR] = sharedPreferences
            .getInt(KEY_ABSENCE_DAYS_PER_YEAR,
                context.resources.getInteger(R.integer.default_absence_days_per_year))
        prefs[KEY_MAX_ABSENCE_DAYS_PER_MONTH] = sharedPreferences
            .getInt(KEY_MAX_ABSENCE_DAYS_PER_MONTH,
                context.resources.getInteger(R.integer.max_absence_days_per_month))
        prefs[KEY_MAX_ABSENCE_DAYS_PER_YEAR] = sharedPreferences
            .getInt(KEY_MAX_ABSENCE_DAYS_PER_YEAR,
                context.resources.getInteger(R.integer.max_absence_days_per_year))
    }

    fun registerChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun getPrefSummary(key: String): String {
        return when (val value = prefs[key]) {
            null -> "Не задано"
            0    -> "Не задано"
            else -> when (key) {
                KEY_YEAR_BEGIN -> CalendarHelper.intToStringDate(value)
                KEY_YEAR_END   -> CalendarHelper.intToStringDate(value)
                KEY_ABSENCE_DAYS_PER_MONTH -> value.toString()
                KEY_ABSENCE_DAYS_PER_YEAR  -> value.toString()
                else -> "Не задано"
            }
        }
    }

    fun pref(key: String): Int {
        return if (prefs.containsKey(key))
            prefs[key] as Int
        else
            0
    }

    fun storePref(key: String, value: Int) {
        prefs[key] = value
        sharedPreferences.edit()
            .putInt(key, value)
            .apply()
    }
}
