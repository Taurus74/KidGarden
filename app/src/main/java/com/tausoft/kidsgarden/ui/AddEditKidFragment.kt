package com.tausoft.kidsgarden.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.databinding.FragmentAddEditKidBinding
import com.tausoft.kidsgarden.navigator.AppNavigator
import com.tausoft.kidsgarden.navigator.Screens
import com.tausoft.kidsgarden.ui.MainActivity.Companion.KID_ID
import com.tausoft.kidsgarden.ui.MainActivity.Companion.MONTH
import com.tausoft.kidsgarden.ui.MainActivity.Companion.YEAR
import com.tausoft.kidsgarden.viewModels.AddEditKidViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEditKidFragment : Fragment() {

    @Inject lateinit var navigator: AppNavigator
    private lateinit var binding: FragmentAddEditKidBinding
    private val viewModel: AddEditKidViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            with (viewModel) {
                year  = it.getInt(YEAR)
                month = it.getInt(MONTH)
                setId(it.getInt(KID_ID))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_add_edit_kid, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textAbsence.setOnClickListener {
            navigator.navigateTo(Screens.ABSENCES, viewModel.bundle())
        }

        binding.addAbsence.setOnClickListener {
            navigator.navigateTo(Screens.EDIT_ABSENCE, viewModel.bundle())
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.OKButton.setOnClickListener {
            viewModel.addKid(binding.kidName.text.toString())
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}