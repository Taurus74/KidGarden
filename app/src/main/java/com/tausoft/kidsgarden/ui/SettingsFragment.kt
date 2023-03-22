package com.tausoft.kidsgarden.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.util.KidsGardenPrefs
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_YEAR_BEGIN
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_YEAR_END
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_ABSENCE_DAYS_PER_MONTH
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_ABSENCE_DAYS_PER_YEAR
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_MAX_ABSENCE_DAYS_PER_MONTH
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_MAX_ABSENCE_DAYS_PER_YEAR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: PreferenceFragmentCompat() {

    @Inject lateinit var prefs: KidsGardenPrefs

    private var yearBeginPref: Preference? = null
    private var yearEndPref: Preference? = null
    private var absenceDaysPerMonthPref: Preference? = null
    private var absenceDaysPerYearPref: Preference? = null

    //
    // Необходимо объявлять ссылку на listener как члена класса (за пределами onCreatePreferences),
    // иначе она будет очищена сборщиком мусора, и реакция на изменение prefs работать не будет
    //
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            Log.d("KidsGarden", "SharedPreferenceChangeListener $key")
            when (key) {
                KEY_YEAR_BEGIN -> yearBeginPref?.summary = prefs.getPrefSummary(key)
                KEY_YEAR_END   -> yearEndPref?.  summary = prefs.getPrefSummary(key)
                KEY_ABSENCE_DAYS_PER_MONTH ->
                    absenceDaysPerMonthPref?.summary = prefs.getPrefSummary(key)
                KEY_ABSENCE_DAYS_PER_YEAR  ->
                    absenceDaysPerYearPref?. summary = prefs.getPrefSummary(key)
            }
        }
        prefs.registerChangeListener(listener)

        yearBeginPref = findPreference(KEY_YEAR_BEGIN)
        yearBeginPref?.summary = prefs.getPrefSummary(KEY_YEAR_BEGIN)
        yearBeginPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = DayMonthDialog(
                KEY_YEAR_BEGIN,
                prefs.pref(KEY_YEAR_BEGIN),
                resources.getString(R.string.hint_year_begin)
            )
            val ft = activity?.supportFragmentManager
            if (ft != null) {
                dialog.show(ft, "dialog")
            }
            true
        }

        yearEndPref = findPreference(KEY_YEAR_END)
        yearEndPref?.summary = prefs.getPrefSummary(KEY_YEAR_END)
        yearEndPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = DayMonthDialog(
                KEY_YEAR_END,
                prefs.pref(KEY_YEAR_END),
                resources.getString(R.string.hint_year_end)
            )
            val ft = activity?.supportFragmentManager
            if (ft != null) {
                dialog.show(ft, "dialog")
            }
            true
        }

        absenceDaysPerMonthPref = findPreference(KEY_ABSENCE_DAYS_PER_MONTH)
        absenceDaysPerMonthPref?.summary = prefs.getPrefSummary(KEY_ABSENCE_DAYS_PER_MONTH)
        absenceDaysPerMonthPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = NumOfDaysDialog(
                KEY_ABSENCE_DAYS_PER_MONTH,
                prefs.pref(KEY_ABSENCE_DAYS_PER_MONTH),
                prefs.pref(KEY_MAX_ABSENCE_DAYS_PER_MONTH),
                resources.getString(R.string.hint_absence_days_per_month)
            )
            val ft = activity?.supportFragmentManager
            if (ft != null) {
                dialog.show(ft, "dialog")
            }
            true
        }

        absenceDaysPerYearPref = findPreference(KEY_ABSENCE_DAYS_PER_YEAR)
        absenceDaysPerYearPref?.summary = prefs.getPrefSummary(KEY_ABSENCE_DAYS_PER_YEAR)
        absenceDaysPerYearPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = NumOfDaysDialog(
                KEY_ABSENCE_DAYS_PER_YEAR,
                prefs.pref(KEY_ABSENCE_DAYS_PER_YEAR),
                prefs.pref(KEY_MAX_ABSENCE_DAYS_PER_YEAR),
                resources.getString(R.string.hint_absence_days_per_year)
            )
            val ft = activity?.supportFragmentManager
            if (ft != null) {
                dialog.show(ft, "dialog")
            }
            true
        }
    }
}