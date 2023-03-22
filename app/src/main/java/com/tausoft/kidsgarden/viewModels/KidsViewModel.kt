package com.tausoft.kidsgarden.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.Kid
import com.tausoft.kidsgarden.data.KidsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KidsViewModel @Inject internal constructor(
    private val kidsRepository: KidsRepository
): ViewModel() {

    val kids: LiveData<List<Kid>> = kidsRepository.getKids()

    val kidsAbsences: Map<Int, List<Absence>> =
        kidsRepository.getAbsences(0, 0)

    fun deleteKid(kid: Kid) = kidsRepository.deleteKid(kid)
}