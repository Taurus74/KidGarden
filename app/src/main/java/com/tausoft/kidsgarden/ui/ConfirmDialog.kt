package com.tausoft.kidsgarden.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.tausoft.kidsgarden.R

class ConfirmDialog(
    private val confirmListener: DialogInterface.OnClickListener,
    private val cancelListener: DialogInterface.OnClickListener): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_delete_record)
                .setPositiveButton(R.string.text_delete, confirmListener)
                .setNegativeButton(R.string.cancel, cancelListener)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}