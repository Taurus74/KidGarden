package com.tausoft.kidsgarden.ui

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_FROM
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_TO
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.util.*
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_YEAR_BEGIN
import com.tausoft.kidsgarden.util.KidsGardenPrefs.Companion.KEY_YEAR_END
import com.tausoft.kidsgarden.viewModels.KidsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KidsFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private val viewModel: KidsViewModel by viewModels()

    @Inject lateinit var navigator: AppNavigator
    @Inject lateinit var absencesFormatter: AbsencesFormatter
    @Inject lateinit var prefs: KidsGardenPrefs

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KidsAdapter

    // Текущая дата на момент запуска
    private var aYear  = 0
    private var aMonth = 0
    private var aDay   = 0

    // Границы текущего (на момент запуска) месяца
    private var monthFrom = 0
    private var monthTo   = 0
    // Границы учебного года
    private var yearFrom = 0
    private var yearTo   = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kids, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateDates(
            CalendarHelper.currentYear(), CalendarHelper.currentMonth(), CalendarHelper.currentDay()
        )
        makePeriodText(aMonth, aYear)

        recyclerView = view.findViewById<RecyclerView>(R.id.rv_kids_list).apply {
            setHasFixedSize(true)
        }
        adapter = KidsAdapter(absencesFormatter, navigator)
        recyclerView.adapter = adapter

        initSwipeToDelete()

        view.findViewById<MaterialButton>(R.id.dec_month).setOnClickListener {
            val monthYear = Date(1, aMonth, aYear).decMonth()
            onPeriodChange(monthYear.year, monthYear.month)
        }

        view.findViewById<MaterialButton>(R.id.inc_month).setOnClickListener {
            val monthYear = Date(1, aMonth, aYear).incMonth()
            onPeriodChange(monthYear.year, monthYear.month)
        }

        view.findViewById<MaterialButton>(R.id.current_period).setOnClickListener {
            DatePickerDialog(requireContext(), this, aYear, aMonth, aDay)
                .show()
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab).setOnClickListener {
            navigator.navigateTo(Screens.EDIT_KID, Bundle())
        }

    }

    private fun onPeriodChange(year: Int, month: Int, dayOfMonth: Int = 0) {
        updateDates(year, month, dayOfMonth)
        getKids()
        makePeriodText(month, year)
    }

    private fun makePeriodText(month: Int, year: Int) {
        val button = requireActivity().findViewById<MaterialButton>(R.id.current_period)
        val text = SpannableString(CalendarHelper.monthYear(month, year))
        text.setSpan(StyleSpan(Typeface.ITALIC), 0, text.length, 0)
        button.text = text

        val color = getSeasonColor(month)
        button.setBackgroundColor(color)

        val decButton = requireActivity().findViewById<MaterialButton>(R.id.dec_month)
        decButton.setBackgroundColor(color)
        val incButton = requireActivity().findViewById<MaterialButton>(R.id.inc_month)
        incButton.setBackgroundColor(color)
    }

    private fun getSeasonColor(month: Int): Int =
        when (CalendarHelper.getSeason(month)) {
            CalendarHelper.Season.SPRING -> getColorRes(R.color.spring)
            CalendarHelper.Season.SUMMER -> getColorRes(R.color.summer)
            CalendarHelper.Season.AUTUMN -> getColorRes(R.color.autumn)
            CalendarHelper.Season.WINTER -> getColorRes(R.color.winter)
        }

    private fun getColorRes(resId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            resources.getColor(resId, null)
        else
            @Suppress("DEPRECATION")
            resources.getColor(R.color.spring)
    }

    override fun onResume() {
        super.onResume()
        getKids()
        makeTitle()
    }

    private fun getKids() {
        viewModel.kids
            .observe(viewLifecycleOwner) { kidsList ->
                val absencesMap = viewModel.kidsAbsences
                val bundle = Bundle()
                bundle.putInt(MONTH_FROM, monthFrom)
                bundle.putInt(MONTH_TO,   monthTo)
                recyclerView.adapter = adapter
                adapter.setDatasets(kidsList, absencesMap, bundle)
                adapter.notifyItemRangeChanged(0, kidsList.size)
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
                        deleteKid(position)
                   }, {_, _ -> })
                       .show(requireActivity().supportFragmentManager, "confirm")
                }
            })
    }

    private fun deleteKid(position: Int) {
        val kid = adapter.getItem(position)
        viewModel.deleteKid(kid)
        adapter.delete(position)
    }

    private fun makeTitle() {
        requireActivity().title = resources.getString(R.string.app_name)
    }

    // Обновить данные по границам месяца и учебного года в связи с выбором новой даты или месяца
    private fun updateDates(year: Int, month: Int, dayOfMonth: Int = 0) {
        if (dayOfMonth > 0)
            aDay = dayOfMonth
        aMonth = month
        aYear  = year
        monthFrom = Date(1, aMonth, aYear).toInt()
        monthTo   = Date(1, aMonth, aYear).incMonth().toInt()
        setYearDates()
    }

    // Обновить данные по границам учебного года
    private fun setYearDates() {
        val yearBegin = prefs.pref(KEY_YEAR_BEGIN)
        val diff = if (aMonth * 100 + aDay >= yearBegin)
            1
        else
            0
        yearFrom = (aYear - 1 + diff) * 10000 + yearBegin
        yearTo   = (aYear + diff) * 10000 + prefs.pref(KEY_YEAR_END)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        onPeriodChange(year, month, dayOfMonth)
/*
        updateDates(year, month, dayOfMonth)
        requireActivity().findViewById<MaterialButton>(R.id.current_period).text =
            CalendarHelper.monthYear(aMonth, aYear)
*/
    }
}

private class KidsAdapter(
    private val absencesFormatter: AbsencesFormatter,
    private val mNavigator: AppNavigator
): RecyclerView.Adapter<KidsAdapter.KidsViewHolder>() {

    private var kidsDataSet: MutableList<Kid> = mutableListOf()
    private var absencesDataSet: Map<Int, List<Absence>> = mapOf()
    private var bundle: Bundle = Bundle()

    fun setDatasets(
        mKidsDataSet: List<Kid>, mAbsencesDataSet: Map<Int, List<Absence>>, mBundle: Bundle) {
        kidsDataSet = mKidsDataSet.toMutableList()
        absencesDataSet = mAbsencesDataSet
        bundle = mBundle
    }

    class KidsViewHolder(layout: LinearLayout, val bundle: Bundle, mNavigator: AppNavigator)
        : RecyclerView.ViewHolder(layout), View.OnClickListener {

        private val navigator = mNavigator

        var kidId: Int = 0
        val tvKidName: TextView = layout.findViewById(R.id.kid_name)
        val tvAbsences: TextView = layout.findViewById(R.id.absences)

        init {
            tvKidName.setOnClickListener(this)
            tvAbsences.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            bundle.putInt(KID_ID, kidId)
            navigator.navigateTo(Screens.EDIT_KID, bundle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KidsViewHolder {
        return KidsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_kids, parent, false) as LinearLayout,
            bundle,
            mNavigator
        )
    }

    override fun onBindViewHolder(holder: KidsViewHolder, position: Int) {
        val kid = kidsDataSet[position]
        holder.kidId = kid.id
        holder.tvKidName.text = kid.name
        holder.tvAbsences.text = absencesFormatter.formatAbsences(absencesDataSet[kid.id])
    }

    override fun getItemCount(): Int = kidsDataSet.size

    fun getItem(position: Int) = kidsDataSet[position]

    fun delete(position: Int) {
        kidsDataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}