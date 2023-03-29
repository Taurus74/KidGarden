package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.databinding.FragmentAddEditAbsenceBinding
import com.tausoft.kidsgarden.ui.MainActivity.Companion.ABSENCE_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_FROM
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH_TO
import com.tausoft.kidsgarden.util.Date
import com.tausoft.kidsgarden.viewModels.AddEditAbsenceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditAbsenceFragment : Fragment() {

    private lateinit var binding: FragmentAddEditAbsenceBinding
    private val viewModel: AddEditAbsenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            viewModel.setKidId(it.getInt(KID_ID))
            viewModel.monthFrom = it.getInt(MONTH_FROM)
            viewModel.monthTo   = it.getInt(MONTH_TO)
            viewModel.setId   (it.getInt(ABSENCE_ID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_add_edit_absence, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        binding.absenceTypeStr.setOnClickListener { popupMenu().show() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.dateFrom.setOnClickListener {
            viewModel
                .getDateFromPickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                    viewModel.setDateFrom(Date(dayOfMonth, month, year).toInt())
                }
                .show()
        }

        binding.dateTo.setOnClickListener {
            viewModel
                .getDateToPickerDialog(requireContext()) { _, year, month, dayOfMonth ->
                    viewModel.setDateTo(Date(dayOfMonth, month, year).toInt())
                }
                .show()
        }

        binding.OKButton.setOnClickListener {
            // Проверка заполнения не нужна - даты по умолчанию заполнены,
            // очистка полей заблокирована
            if (!viewModel.checkDatesOrder()) {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.wrong_dates), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (!viewModel.sameMonthDates()) {
                Toast.makeText(requireContext(),
                    resources.getText(R.string.wrong_month),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Проверка на пересечение нового периода с уже введёнными
            viewModel.checkCrossing().observe(viewLifecycleOwner) {
                if (it == 0) {
                    if (viewModel.addAbsence(requireContext()))
                        requireActivity().supportFragmentManager.popBackStack()
                    else
                        Toast.makeText(requireContext(),
                            "Лимит дней превышен", Toast.LENGTH_SHORT
                        ).show()
                }
                else
                    Toast.makeText(requireContext(),
                        resources.getText(R.string.has_crossing),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    // Временный вариант, замена Spinner, который при открытии записи
    // подставляет не текущее значение из БД, а установленное по умолчанию во viewModel.
    private fun popupMenu(): PopupMenu {
        val popupMenu = PopupMenu(requireContext(), binding.absenceTypeStr, Gravity.END)
        viewModel.absenceTypes.value!!.forEach {
            popupMenu.menu.add(Menu.NONE, it.ordinal, it.ordinal, it.toString())
        }
        popupMenu.setOnMenuItemClickListener {
            viewModel.absenceType = viewModel.absenceTypes.value!![ it.itemId ]
            viewModel.setAbsenceTypeStr( it.title.toString() )
            false
        }
        return popupMenu
    }
}