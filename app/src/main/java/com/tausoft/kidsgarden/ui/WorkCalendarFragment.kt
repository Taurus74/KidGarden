package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.calendarView.ui.CalendarAdapter
import com.tausoft.kidsgarden.calendarView.ui.CalendarRow
import com.tausoft.kidsgarden.databinding.FragmentWorkCalendarBinding
import com.tausoft.kidsgarden.viewModels.WorkCalendarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkCalendarFragment : Fragment() {

    private val viewModel: WorkCalendarViewModel by viewModels()
    private lateinit var binding: FragmentWorkCalendarBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_work_calendar, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.rvWorkCalendar.apply {
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.calendarRows.observe(this) {
            if (it.isNotEmpty()) {
                recyclerView.adapter =
                    CalendarAdapter(requireContext(), it as MutableList<CalendarRow>)
                (recyclerView.adapter as CalendarAdapter)
                    .notifyItemRangeChanged(0, it.size)
            }
        }
    }
}