package com.tausoft.kidsgarden.data

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

    fun getKids() = kids.getKids()

    fun getAbsences(dateFrom: Int, dateTo: Int): Map<Int, List<Absence>> {
        var result: Map<Int, List<Absence>> = mapOf()
         absences.getAbsences(dateFrom, dateTo) {
            result = it
        }
        return result
    }  

    fun deleteKid(kid: Kid) = kids.deleteKid(kid)
}