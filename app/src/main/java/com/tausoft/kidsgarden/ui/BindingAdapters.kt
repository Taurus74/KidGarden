package com.tausoft.kidsgarden.ui

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:seasonColor")
fun seasonColor(view: View, color: Int) {
    view.setBackgroundColor(color)
}