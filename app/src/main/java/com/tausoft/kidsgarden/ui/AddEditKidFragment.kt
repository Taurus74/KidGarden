package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.AbsencesDataSource
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.data.KidsDataSource
import com.tausoft.kidsgarden.di.DatabaseAbsences
import com.tausoft.kidsgarden.di.DatabaseKids
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_FROM
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_TO
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.util.AbsencesFormatter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEditKidFragment : Fragment() {
    @DatabaseKids
    @Inject lateinit var kids: KidsDataSource
    @DatabaseAbsences
    @Inject lateinit var absences: AbsencesDataSource
    @Inject lateinit var navigator: AppNavigator
    @Inject lateinit var absencesFormatter: AbsencesFormatter

    private lateinit var kidName: TextInputEditText
    private lateinit var textAbsence: MaterialTextView

    private var kidId: Int = 0
    private var kid: Kid? = null
    private var monthFrom = 0
    private var monthTo   = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            kidId    = it.getInt(KID_ID)
            monthFrom = it.getInt(MONTH_FROM)
            monthTo   = it.getInt(MONTH_TO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit_kid, container, false)
        if (kidId == 0) {
            view.findViewById<MaterialTextView>(R.id.text_absence).visibility = GONE
            view.findViewById<MaterialButton>  (R.id.add_absence). visibility = GONE
        }
        makeTitle()
        return view
    }

    private fun makeTitle() {
        if (kidId == 0)
            requireActivity().title = resources.getString(R.string.new_kid)
        else {
            kids.getKid(kidId) {
                requireActivity().title = it.name
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        kidName = view.findViewById(R.id.kid_name)

        textAbsence = view.findViewById(R.id.text_absence)
        textAbsence.setOnClickListener {
            navigator.navigateTo(Screens.ABSENCES, fillBundle())
        }

        view.findViewById<MaterialButton>(R.id.add_absence).setOnClickListener {
            navigator.navigateTo(Screens.EDIT_ABSENCE, fillBundle())
        }

        view.findViewById<MaterialButton>(R.id.cancel_button).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        view.findViewById<MaterialButton>(R.id.OK_button).setOnClickListener {
            if (kid == null)
                kid = Kid(kidName.text.toString())
            else
                kid!!.name = kidName.text.toString()
            kids.addKid(kid!!)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        if (kidId > 0) {
            kids.getKid(kidId) { mKid ->
                kid = mKid
                kidName.setText(kid!!.name)
                absences.getSumAbsences(kidId, monthFrom, monthTo) {
                    textAbsence.text = absencesFormatter.formatAbsences(it)
                }
            }
        }
        makeTitle()
    }

    private fun fillBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt(KID_ID, kidId)
        bundle.putInt(MONTH_FROM, monthFrom)
        bundle.putInt(MONTH_TO,   monthTo)
        return bundle
    }
}