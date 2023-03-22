package com.tausoft.kidsgarden.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.ui.*
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor(private val activity: FragmentActivity) : AppNavigator {

    override fun navigateTo(screen: Screens, params: Bundle?) {
        val fragment: Fragment = when (screen) {
            Screens.SETTINGS      -> SettingsFragment()
            Screens.IMPORT        -> ImportKidsFragment()
            Screens.KIDS          -> KidsFragment()
            Screens.EDIT_KID      -> AddEditKidFragment()
            Screens.ABSENCES      -> AbsencesFragment()
            Screens.EDIT_ABSENCE  -> AddEditAbsenceFragment()
            Screens.WORK_CALENDAR -> WorkCalendarFragment()
        }

        if (params != null &&
            (screen == Screens.EDIT_KID || screen == Screens.ABSENCES
                    || screen == Screens.EDIT_ABSENCE))
            fragment.arguments = params

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(fragment::class.java.canonicalName)
            .commit()
    }
}