package com.tausoft.kidsgarden.data

import androidx.lifecycle.LiveData

interface AbsencesDataSource {
    fun addAbsence(absence: Absence)
    fun deleteAbsence(absence: Absence)

    // Получить все отсутствия за период в разрезе kid.id
    fun getAbsences(dateFrom: Int, dateTo: Int): LiveData<Map<Int, List<Absence>>>

    // Получить все отсутствия за период заданного ребенка
    fun getKidAbsences(kidId: Int, dateFrom: Int, dateTo: Int): LiveData<List<Absence>>

    // Получить запись об отсутствии
    fun getAbsence(absenceId: Int): LiveData<Absence>

    // Рассчитать для заданного ребенка сводное количество дней по типу отсутствия за период,
    // исключая заданную запись об отсутствии
    fun getSumKidAbsence(
        kidId: Int, absenceId: Int, absenceType: AbsenceType, dateFrom: Int, dateTo: Int,
        callback: (Int) -> Unit)

    // Выбрать данные об отсутствии ребенка, сгруппированные по типу отсутствия, за период
    fun getSumKidAbsences(
        kidId: Int, dateFrom: Int, dateTo: Int, callback: (Map<AbsenceType, Int>) -> Unit
    )

    // Проверить пересечение отсутствия с другими за заданный период
    fun checkCrossing(kidId: Int, absenceId: Int, dateFrom: Int, dateTo: Int): LiveData<Int>
}