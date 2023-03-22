package com.tausoft.kidsgarden.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.tausoft.kidsgarden.R
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.data.KidsDataSource
import com.tausoft.kidsgarden.util.KidsListImport
import com.tausoft.kidsgarden.di.DatabaseKids
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImportKidsFragment : Fragment() {

    @DatabaseKids
    @Inject lateinit var kids: KidsDataSource
    @Inject lateinit var kidsListImport: KidsListImport

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_import_kids, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/plain"))
        }
        getImportDialog().launch(intent)
    }

    private fun getImportDialog(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    this.requireActivity().contentResolver?.openInputStream(uri)
                        .let { inputStream ->
                            val mimeType = this.requireActivity().contentResolver?.getType(uri)
                            if (mimeType == "text/plain" && inputStream != null) {
                                val kidsList = kidsListImport.readStream(inputStream)
                                kidsList.forEach {
                                    kids.addKid( Kid(it) )
                                }
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                }
            }
            else
                // Закрыть фрагмент вместе с диалогом
                requireActivity().supportFragmentManager.popBackStack()
        }
    }
}