package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.calendarView.TauCalendarView
import com.tausoft.kidsgarden.calendarView.ui.CalendarAdapter
import com.tausoft.kidsgarden.data.DaysOffDataSource
import com.tausoft.kidsgarden.di.DatabaseDaysOff
import com.tausoft.kidsgarden.network.WorkCalendarRepository
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.Date
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class WorkCalendarFragment : Fragment() {

    @DatabaseDaysOff
    @Inject lateinit var daysOffDataSource: DaysOffDataSource
    @Inject lateinit var workCalendarRepository: WorkCalendarRepository

    private lateinit var recyclerView: RecyclerView
    private lateinit var yearBtn: MaterialButton

    // Текущий год
    private var year = CalendarHelper.currentYear()
    // Границы года
    private var yearFrom = Date(mYear = year).toInt()
    private var yearTo   = Date(31, 12, year).toInt()

    private lateinit var calendar: TauCalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_work_calendar, container, false)
        yearBtn = view.findViewById(R.id.current_year)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateYear(0, yearBtn)

        recyclerView = view.findViewById<RecyclerView>(R.id.rv_work_calendar).apply {
            setHasFixedSize(true)
        }

        view.findViewById<MaterialButton>(R.id.dec_year).setOnClickListener {
            updateYear(-1, yearBtn)
        }

        view.findViewById<MaterialButton>(R.id.inc_year).setOnClickListener {
            updateYear(1, yearBtn)
        }

        view.findViewById<FloatingActionButton>(R.id.fab_work_calendar).setOnClickListener {
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch {
                val listOfDays = workCalendarRepository.getData(year.toString())
                for (day in listOfDays) {
                    daysOffDataSource.addDayOff(day)
                }
                getDaysOff()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getDaysOff()
        makeTitle()
    }

    private fun makeTitle() {
        requireActivity().title = requireContext().resources.getString(R.string.title_work_calendar)
    }

    private fun getDaysOff() {
        daysOffDataSource.getDaysOff(yearFrom, yearTo) {
            val daysOff = mutableSetOf<Int>()
            for (dayOff in it)
                daysOff.add(dayOff.day)
            calendar.loadDaysOff(daysOff)
            val calendarRows = calendar.getCalendarRows()
            recyclerView.adapter = CalendarAdapter(requireContext(), calendarRows)
            (recyclerView.adapter as CalendarAdapter)
                .notifyItemRangeChanged(0, calendarRows.size)
        }
    }

    private fun updateYear(num: Int, view: MaterialButton) {
        year += num
        view.text = "$year"
        yearFrom = Date(mYear = year).toInt()
        yearTo = Date(31, 11, year).toInt()
        getDaysOff()

        val calendarDateFrom = Calendar.getInstance()
        calendarDateFrom.set(year, 0, 1)
        val calendarDateTo = Calendar.getInstance()
        calendarDateTo.set(year, 11, 31)
        calendar = TauCalendarView(calendarDateFrom, calendarDateTo)
    }
}