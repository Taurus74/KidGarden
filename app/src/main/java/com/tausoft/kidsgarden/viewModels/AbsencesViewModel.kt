package com.tausoft.kidsgarden.viewModels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.KidsRepository
import com.tausoft.kidsgarden.ui.MainActivity
import com.tausoft.kidsgarden.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AbsencesViewModel @Inject constructor(
    private val kidsRepository: KidsRepository
    ) : ViewModel() {

    // id ребенка
    private var _kidId = MutableLiveData(0)
    val kidId: LiveData<Int> = _kidId
    fun setKidId(kidId: Int) {
        _kidId.value = kidId
    }

    // Фамилия Имя ребенка
    private val _kidName = MutableLiveData("")
    val kidName: LiveData<String> = _kidName
    private fun setKidName(value: String) {
        _kidName.value = value
    }

    // Текущий месяц
    var year  = 0
    var month = 0

    init {
        _kidId.observeForever {
            if (it > 0)
                kidsRepository.getKid(it).observeForever { kid ->
                    setKidName( kid?.name ?: "" )
                }
        }
    }

    fun getAbsences(): LiveData<List<Absence>> {
        val aDate = Date(1, month, year)
        return kidsRepository.getAbsences(kidId.value!!, aDate.toInt(), aDate.incMonth().toInt())
    }

    fun deleteAbsence(absence: Absence) = kidsRepository.deleteAbsence(absence)

    fun bundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt(MainActivity.YEAR,  year)
        bundle.putInt(MainActivity.MONTH, month)
        return bundle
    }
}