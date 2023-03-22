package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.AbsencesDataSource
import com.tausoft.kidsgarden.data.KidsDataSource
import com.tausoft.kidsgarden.di.DatabaseAbsences
import com.tausoft.kidsgarden.di.DatabaseKids
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import com.tausoft.kidsgarden.ui.MainActivity.Companion.ABSENCE_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_FROM
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_TO
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.TAG
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.SwipeHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AbsencesFragment : Fragment() {
    @DatabaseKids
    @Inject lateinit var kids: KidsDataSource
    @DatabaseAbsences
    @Inject lateinit var absences: AbsencesDataSource
    @Inject lateinit var navigator: AppNavigator

    private var kidId: Int = 0
    private var monthFrom = 0
    private var monthTo   = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AbsencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            kidId = it.getInt(KID_ID)
            monthFrom = it.getInt(MONTH_FROM)
            monthTo   = it.getInt(MONTH_TO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_absences, container, false)

        view.findViewById<FloatingActionButton>(R.id.fab_add_absence).setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(KID_ID, kidId)
            navigator.navigateTo(Screens.EDIT_ABSENCE, bundle)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById<RecyclerView>(R.id.rv_absences_list).apply {
            setHasFixedSize(true)
        }
        adapter = AbsencesAdapter(navigator)
        recyclerView.adapter = adapter

        initSwipeToDelete()
    }

    override fun onResume() {
        super.onResume()
        if (kidId > 0)
            getAbsences(kidId)
        else {
            Log.e(TAG, "Wrong parameter: kidId = 0")
            requireActivity().supportFragmentManager.popBackStack()
        }
        makeTitle()
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

    private fun getAbsences(kidId: Int) {
        absences.getKidAbsences(kidId, monthFrom, monthTo) {
            val bundle = Bundle()
            bundle.putInt(MONTH_FROM, monthFrom)
            bundle.putInt(MONTH_TO,   monthTo)
            recyclerView.adapter = adapter
            adapter.setDatasets(it, bundle)
            adapter.notifyItemRangeChanged(0, it.size)
        }
    }

    private fun initSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(recyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return listOf(deleteButton(position))
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            resources.getString(R.string.text_delete),
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    ConfirmDialog({_, _ ->
                        deleteAbsence(position)
                    }, {_, _ -> })
                        .show(requireActivity().supportFragmentManager, "confirm")
                }
            })
    }

    private fun deleteAbsence(position: Int) {
        val absence = adapter.getItem(position)
        absences.deleteAbsence(absence)
        adapter.delete(position)
    }

}

private class AbsencesAdapter(private val mNavigator: AppNavigator)
    : RecyclerView.Adapter<AbsencesAdapter.AbsencesViewHolder>() {

    private var absencesDataSet: MutableList<Absence> = mutableListOf()
    private var bundle: Bundle = Bundle()

    fun setDatasets(mAbsencesDataSet: List<Absence>, mBundle: Bundle) {
        absencesDataSet = mAbsencesDataSet.toMutableList()
        bundle = mBundle
    }

    class AbsencesViewHolder(layout: LinearLayout, mNavigator: AppNavigator, val bundle: Bundle):
        RecyclerView.ViewHolder(layout), View.OnClickListener {

        private val navigator = mNavigator
        val tvDateFrom:    TextView = layout.findViewById(R.id.date_from)
        val tvDateTo:      TextView = layout.findViewById(R.id.date_to)
        val tvAbsenceType: TextView = layout.findViewById(R.id.absence_type)
        var kidId: Int = 0
        var absenceId = 0

        init {
            tvDateFrom.   setOnClickListener(this)
            tvDateTo.     setOnClickListener(this)
            tvAbsenceType.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            bundle.putInt(KID_ID, kidId)
            bundle.putInt(ABSENCE_ID, absenceId)
            navigator.navigateTo(Screens.EDIT_ABSENCE, bundle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsencesViewHolder {
        return AbsencesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_absence, parent, false) as LinearLayout,
            mNavigator,
            bundle
        )
    }

    override fun onBindViewHolder(holder: AbsencesViewHolder, position: Int) {
        val absence = absencesDataSet[position]
        holder.kidId = absence.kidId
        holder.absenceId = absence.id
        holder.tvDateFrom.text = CalendarHelper.intToStringDate(absence.dateFrom)
        holder.tvDateTo.  text = CalendarHelper.intToStringDate(absence.dateTo)
        holder.tvAbsenceType.text = absence.absenceType.toString()
    }

    override fun getItemCount(): Int = absencesDataSet.size

    fun getItem(position: Int) = absencesDataSet[position]

    fun delete(position: Int) {
        absencesDataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}