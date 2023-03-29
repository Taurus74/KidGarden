package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val KID_ID     = "KID_ID"
        const val ABSENCE_ID = "ABSENCE_ID"
        const val YEAR       = "YEAR"
        const val MONTH      = "MONTH"
    }

    @Inject lateinit var navigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigator.navigateTo(Screens.KIDS)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings     -> navigator.navigateTo(Screens.SETTINGS)
            R.id.menu_import       -> navigator.navigateTo(Screens.IMPORT)
            R.id.menu_working_days -> navigator.navigateTo(Screens.WORK_CALENDAR)

            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}