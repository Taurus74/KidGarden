package com.tausoft.kidsgarden.util

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.tausoft.kidsgarden.data.AbsenceType

@BindingAdapter("app:seasonColor")
fun seasonColor(view: View, color: Int) {
    view.setBackgroundColor(color)
}

@BindingAdapter("app:hideIfZero")
fun hideIfZero(view: View, id: Int) {
    if (id == 0)
        view.visibility = View.GONE
}

// AbsenceType binding adapter
private val entries: Array<AbsenceType> = AbsenceType.values()

@BindingAdapter("absenceType")
fun setAbsenceType(spinner: AppCompatSpinner, value: AbsenceType) {
    val selectedPosition = entries.indexOf(value)
    spinner.setSelection(selectedPosition)
}

@InverseBindingAdapter(attribute = "absenceType")
fun getAbsenceType(spinner: AppCompatSpinner): AbsenceType {
    return spinner.selectedItem as AbsenceType
}

@BindingAdapter("absenceTypeAttrChanged")
fun setListener(spinner: AppCompatSpinner, listener: InverseBindingListener?) {
    spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            listener?.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            listener?.onChange()
        }
    }
}