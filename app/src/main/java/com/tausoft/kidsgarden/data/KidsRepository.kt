package com.tausoft.kidsgarden.data

import androidx.lifecycle.LiveData
import com.tausoft.kidsgarden.di.DatabaseAbsences
import com.tausoft.kidsgarden.di.DatabaseKids
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KidsRepository @Inject constructor() {

    @DatabaseKids
    @Inject lateinit var kids: KidsDataSource
    @DatabaseAbsences
    @Inject lateinit var absences: AbsencesDataSource

    fun addKid(kid: Kid) = kids.addKid(kid)

    fun addAbsence(absence: Absence) = absences.addAbsence(absence)

    fun getKids() = kids.getKids()

    fun getKid(id: Int) = kids.getKid(id)

    fun getAbsences(dateFrom: Int, dateTo: Int): LiveData<Map<Int, List<Absence>>> =
         absences.getAbsences(dateFrom, dateTo)

    fun getAbsences(id: Int, dateFrom: Int, dateTo: Int): LiveData<List<Absence>> =
        absences.getKidAbsences(id, dateFrom, dateTo)

    fun getAbsence(id: Int): LiveData<Absence> = absences.getAbsence(id)

    fun deleteKid(kid: Kid) = kids.deleteKid(kid)

    fun deleteAbsence(absence: Absence) = absences.deleteAbsence(absence)

    fun checkCrossing(kidId: Int, absenceId: Int, dateFrom: Int, dateTo: Int) =
        absences.checkCrossing(kidId, absenceId, dateFrom, dateTo)

    fun getSumKidAbsence(
        kidId: Int, absenceId: Int, absenceType: AbsenceType, limitFrom: Int, limitTo: Int): Int {
        var result = 0
        absences.getSumKidAbsence(kidId, absenceId, absenceType, limitFrom, limitTo) {
            result = it
        }
        return result
    }

}