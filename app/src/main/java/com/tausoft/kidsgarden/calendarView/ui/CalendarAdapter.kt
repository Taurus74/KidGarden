package com.tausoft.kidsgarden.calendarView.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.calendarView.ui.CalendarRow.Companion.DATES_ROW
import com.tausoft.kidsgarden.calendarView.ui.CalendarRow.Companion.DAY_NAMES_ROW
import com.tausoft.kidsgarden.calendarView.ui.CalendarRow.Companion.MONTH_ROW
import com.tausoft.kidsgarden.util.CalendarHelper

class CalendarAdapter(
    private val context: Context,
    private val calendarRows: MutableList<CalendarRow>)
    : RecyclerView.Adapter<ViewHolder>() {

    class VHMonthRow(view: View): ViewHolder(view) {
        val monthName: TextView = view.findViewById(R.id.month)
    }

    class VHDayNamesRow(view: View, val context: Context): ViewHolder(view) {
        val dayNames: ArrayList<TextView> = arrayListOf()
        init {
            for (day in 0..6) {
                dayNames.add(view.findViewById(findViewByTextId(day)))
            }
        }

        @SuppressLint("DiscouragedApi")
        private fun findViewByTextId(id: Int) =
            context.resources.getIdentifier("day$id", "id", context.packageName)
    }

    class VHDatesRow(view: View, val context: Context): ViewHolder(view) {
        val dates: ArrayList<TextView> = arrayListOf()
        init {
            for (day in 0..6) {
                dates.add(view.findViewById(findViewByTextId(day)))
            }
        }

        @SuppressLint("DiscouragedApi")
        private fun findViewByTextId(id: Int) =
            context.resources.getIdentifier("day$id", "id", context.packageName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        return when (viewType) {
            MONTH_ROW -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_calendar_month, parent, false)
                VHMonthRow(view)
            }
            DAY_NAMES_ROW -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_calendar_day_names, parent, false)
                VHDayNamesRow(view, context)
            }
            DATES_ROW -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_calendar_dates, parent, false)
                VHDatesRow(view, context)
            }
            else -> {
                VHMonthRow(View(context))
            }
        }
    }

    override fun getItemViewType(position: Int) = calendarRows[position].rowType

    override fun getItemCount() = calendarRows.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calendarRow = calendarRows[position]
        when (calendarRow.rowType) {
            MONTH_ROW -> {
                (holder as VHMonthRow).monthName.text =
                    CalendarHelper.monthYear(calendarRow.month, calendarRow.year)
            }
            DAY_NAMES_ROW -> {
                for (day in 0..6) {
                    (holder as VHDayNamesRow).dayNames[day].text =
                        (calendarRow as DayNamesRow).dayNames[day]
                }
            }
            DATES_ROW -> {
                for (day in 0..6) {
                    val date = (calendarRow as DatesRow).dates[day]
                    (holder as VHDatesRow).dates[day].text =
                        if (date == 0)
                            ""
                        else
                            "$date"
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        holder.dates[day].setTextAppearance(
                            dateStyle(calendarRow.daysOff[day])
                        )
                    }
                    else {
                        @Suppress("DEPRECATION")
                            holder.dates[day].setTextAppearance(context,
                                dateStyle(calendarRow.daysOff[day])
                            )
                    }
                }
            }
        }
    }

    private fun dateStyle(isDayOff: Boolean = false) =
        if (isDayOff)
            R.style.CalendarDayOff
        else
            R.style.CalendarWorkDay
}