package com.tausoft.kidsgarden.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.databinding.FragmentKidsBinding
import com.tausoft.kidsgarden.databinding.RowKidsBinding
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_FROM
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_TO
import com.tausoft.kidsgarden.util.*
import com.tausoft.kidsgarden.viewModels.KidsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KidsFragment: Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentKidsBinding
    private val viewModel: KidsViewModel by viewModels()

    @Inject lateinit var navigator: AppNavigator
    @Inject lateinit var absencesFormatter: AbsencesFormatter

    private lateinit var rvKidsList: RecyclerView
    private lateinit var adapter: KidsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKidsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.updateDates()

        adapter = KidsAdapter(absencesFormatter, navigator)
        rvKidsList = binding.rvKidsList
        rvKidsList.adapter = adapter
        rvKidsList.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
        rvKidsList.apply {
            setHasFixedSize(true)
        }

        initSwipeToDelete(rvKidsList) {
            deleteKid(it)
        }

        binding.currentPeriod.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                this,
                viewModel.year.value!!,
                viewModel.month.value!!,
                viewModel.dayOfMonth.value!!)
                .show()
        }

        binding.fab.setOnClickListener {
            navigator.navigateTo(Screens.EDIT_KID, Bundle())
        }

    }

    private fun onPeriodChange(year: Int, month: Int, dayOfMonth: Int = 0) {
        viewModel.setYear(year)
        viewModel.setMonth(month)
        viewModel.setDay(dayOfMonth)
        viewModel.updateDates()
        getKids()
    }

    override fun onResume() {
        super.onResume()
        getKids()
    }

    private fun getKids() {
        viewModel.kids
            .observe(viewLifecycleOwner) { kidsList ->
                viewModel.kidsAbsences
                    .observe(viewLifecycleOwner) { absencesMap ->
                        val bundle = Bundle()
                        bundle.putInt(MONTH_FROM, viewModel.monthFrom.value!!)
                        bundle.putInt(MONTH_TO,   viewModel.monthTo.value!!)
                        rvKidsList.adapter = adapter
                        adapter.setDatasets(kidsList, absencesMap, bundle)
                        adapter.notifyItemRangeChanged(0, kidsList.size)
                    }
            }
    }

    private fun initSwipeToDelete(recyclerView: RecyclerView, onClick: (Int) -> Unit) {
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(recyclerView) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                return listOf(deleteButton(position, onClick))
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun deleteButton(position: Int, _onClick: (Int) -> Unit): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            resources.getString(R.string.text_delete),
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                   ConfirmDialog(
                       {_, _ -> _onClick(position) },
                       {_, _ -> })
                       .show(requireActivity().supportFragmentManager, "confirm")
                }
            })
    }

    private fun deleteKid(position: Int) {
        val kid = adapter.getItem(position)
        viewModel.deleteKid(kid)
        adapter.delete(position)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        onPeriodChange(year, month, dayOfMonth)
    }
}

class KidsAdapter(
    private val absencesFormatter: AbsencesFormatter,
    private val mNavigator: AppNavigator
): RecyclerView.Adapter<KidsAdapter.KidsViewHolder>() {

    private lateinit var binding: RowKidsBinding
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
        private val tvKidName: TextView = layout.findViewById(R.id.kid_name)
        private val tvAbsences: TextView = layout.findViewById(R.id.absences)

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
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_kids, parent, false)
        return KidsViewHolder(binding.root as LinearLayout, bundle, mNavigator)
    }

    override fun onBindViewHolder(holder: KidsViewHolder, position: Int) {
        val kid = kidsDataSet[position]
        holder.kidId = kid.id
        binding.kid = kid
        binding.absencesStr = absencesFormatter.formatAbsences(absencesDataSet[kid.id])
    }

    override fun getItemCount(): Int = kidsDataSet.size

    fun getItem(position: Int) = kidsDataSet[position]

    fun delete(position: Int) {
        kidsDataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}