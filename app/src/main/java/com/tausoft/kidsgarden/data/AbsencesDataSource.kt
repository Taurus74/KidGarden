package com.tausoft.kidsgarden.data

interface AbsencesDataSource {
    fun addAbsence(absence: Absence)
    fun getAbsences(dateFrom: Int, dateTo: Int, callback: (Map<Int, List<Absence>>) -> Unit)
    fun getKidAbsences(kidId: Int, dateFrom: Int, dateTo: Int, callback: (List<Absence>) -> Unit)
    fun getSumKidAbsence(
        kidId: Int, absenceId: Int, absenceType: AbsenceType, dateFrom: Int, dateTo: Int,
        callback: (Int) -> Unit)
    fun getSumAbsences(
        kidId: Int, dateFrom: Int, dateTo: Int, callback: (Map<AbsenceType, Int>) -> Unit
    )
    fun getAbsence(kidId: Int, absenceId: Int, callback: (Absence) -> Unit)
    fun checkCrossing(
        kidId: Int, absenceId: Int, dateFrom: Int, dateTo: Int, callback: (Int) -> Unit
    )
    fun deleteAbsence(absence: Absence)
}