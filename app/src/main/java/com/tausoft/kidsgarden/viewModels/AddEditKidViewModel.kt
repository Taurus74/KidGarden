package com.tausoft.kidsgarden.viewModels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tausoft.kidsgarden.data.*
import com.tausoft.kidsgarden.ui.MainActivity
import com.tausoft.kidsgarden.util.AbsencesFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditKidViewModel @Inject constructor(
    private val kidsRepository: KidsRepository): ViewModel() {

    @Inject lateinit var absencesFormatter: AbsencesFormatter

    // id ребенка
    private var _id = MutableLiveData(0)
    val id: LiveData<Int> = _id
    fun setId(id: Int) {
        _id.value = id
    }

    // Ребенок
    var kid = Kid("")

    // Имя ребенка
    private var _name = MutableLiveData("")
    val name: LiveData<String> = _name

    // Набор записей об отсутствии ребенка - текстовый вид
    private var _absences = MutableLiveData("")
    val absences: LiveData<String> = _absences

    // Границы текущего месяца
    var monthFrom = 0
    var monthTo   = 0

    init {
        _id.observeForever {
            if (it > 0) {
                kidsRepository.getKid(it).observeForever { _kid ->
                    kid = _kid
                    _name.value = kid.name
                }
                kidsRepository.getAbsences(it, monthFrom, monthTo)
                    .observeForever { kidAbsences ->
                        _absences.value = absencesFormatter.formatAbsences(kidAbsences)
                    }
            }
        }
    }

    fun fillBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt(MainActivity.KID_ID,     kid.id)
        bundle.putInt(MainActivity.MONTH_FROM, monthFrom)
        bundle.putInt(MainActivity.MONTH_TO,   monthTo)
        return bundle
    }

    fun addKid(name: String) {
        kid.name = name
        kidsRepository.addKid(kid)
    }
}