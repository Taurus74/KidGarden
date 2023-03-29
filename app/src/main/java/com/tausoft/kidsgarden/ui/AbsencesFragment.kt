package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.databinding.FragmentAbsencesBinding
import com.tausoft.kidsgarden.databinding.RowAbsenceBinding
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import com.tausoft.kidsgarden.ui.MainActivity.Companion.ABSENCE_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH
import com.tausoft.kidsgarden.ui.MainActivity.Companion.YEAR
import com.tausoft.kidsgarden.util.CalendarHelper
import com.tausoft.kidsgarden.util.SwipeHelper
import com.tausoft.kidsgarden.viewModels.AbsencesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AbsencesFragment : Fragment() {

    private lateinit var binding: FragmentAbsencesBinding
    private val viewModel: AbsencesViewModel by viewModels()

    @Inject lateinit var navigator: AppNavigator
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AbsencesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            viewModel.setKidId(it.getInt(KID_ID))
            viewModel.year  = it.getInt(YEAR)
            viewModel.month = it.getInt(MONTH)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAbsencesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.rvAbsencesList.apply {
            setHasFixedSize(true)
        }

        adapter = AbsencesAdapter(navigator)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )

        binding.fabAddAbsence.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(KID_ID, viewModel.kidId.value!!)
            navigator.navigateTo(Screens.EDIT_ABSENCE, bundle)
        }

        initSwipeToDelete()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAbsences().observe(this) {
            recyclerView.adapter = adapter
            adapter.setDatasets(it, viewModel.bundle())
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
        viewModel.deleteAbsence(absence)
        adapter.delete(position)
    }

}

private class AbsencesAdapter(private val mNavigator: AppNavigator)
    : RecyclerView.Adapter<AbsencesAdapter.AbsencesViewHolder>() {

    private lateinit var binding: RowAbsenceBinding
    private var absencesDataSet: MutableList<Absence> = mutableListOf()
    private var bundle: Bundle = Bundle()

    fun setDatasets(mAbsencesDataSet: List<Absence>, mBundle: Bundle) {
        absencesDataSet = mAbsencesDataSet.toMutableList()
        bundle = mBundle
    }

    class AbsencesViewHolder(layout: LinearLayout, val bundle: Bundle, mNavigator: AppNavigator):
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
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_absence, parent, false)
        return AbsencesViewHolder(binding.root as LinearLayout, bundle, mNavigator)
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