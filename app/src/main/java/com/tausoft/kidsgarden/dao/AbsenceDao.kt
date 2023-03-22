package com.tausoft.kidsgarden.dao

import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import com.tausoft.kidsgarden.data.Absence
import com.tausoft.kidsgarden.data.AbsenceType

@Dao
interface AbsenceDao: BaseDao<Absence> {
    // Выбрать сводные данные об отсутствии всех детей за период
    @MapInfo(keyColumn = "kid_id")
    @Query("SELECT * FROM absences"
            + " WHERE (date_from >= :dateFrom AND date_from < :dateTo)"
            + "     OR (date_to >= :dateFrom AND date_to < :dateTo)"
            + " ORDER BY date_from DESC")
    fun getAbsences(dateFrom: Int, dateTo: Int): Map<Int, List<Absence>>

    // Выбрать данные об отсутствии ребенка за период
    @Query("SELECT * FROM absences"
            + " WHERE ((date_from >= :dateFrom AND date_from <= :dateTo)"
            + "     OR (date_to >= :dateFrom AND date_to <= :dateTo))"
            + "     AND kid_id = :kidId"
            + " ORDER BY date_from DESC")
    fun getKidAbsences(kidId: Int, dateFrom: Int, dateTo: Int): List<Absence>

    // Выбрать данные об отсутствии ребенка, сгруппированные по типу отсутствия, за период
    @MapInfo(keyColumn = "absence_type", valueColumn = "sum_of_days")
    @Query("SELECT absence_type, SUM(num_of_days) AS sum_of_days FROM absences"
            + " WHERE ((date_from >= :dateFrom AND date_from <= :dateTo)"
            + "     OR (date_to >= :dateFrom AND date_to <= :dateTo))"
            + "     AND kid_id = :kidId"
            + " GROUP BY absence_type")
    fun getSumKidAbsences(kidId: Int, dateFrom: Int, dateTo: Int): Map<AbsenceType, Int>

    // Выбрать для заданного ребенка сводное количество дней по типу отсутствия за период
    @Query("SELECT SUM(num_of_days) AS sum_of_days FROM absences"
            + " WHERE ((date_from >= :dateFrom AND date_from <= :dateTo)"
            + "     OR (date_to >= :dateFrom AND date_to <= :dateTo))"
            + "     AND kid_id = :kidId AND absence_type = :absenceType"
            + "     AND id <> :absenceId")
    fun getSumKidAbsence(
        kidId: Int, absenceId: Int, absenceType: AbsenceType, dateFrom: Int, dateTo: Int): Int

    // Выбрать заданную запись об отсутствии ребенка
    @Query("SELECT * FROM absences WHERE id = :absenceId AND kid_id = :kidId")
    fun getKidAbsence(kidId: Int, absenceId: Int): Absence?

    // Найти, есть ли пересечение периода с существующими в базе
    @Query("SELECT EXISTS (SELECT * FROM absences"
            + " WHERE ((date_from >= :dateFrom AND date_from <= :dateTo)"
            + "     OR (date_to >= :dateFrom AND date_to <= :dateTo))"
            + "     AND kid_id = :kidId"
            + "     AND id <> :absenceId)")
    fun checkCrossing(kidId: Int, absenceId: Int, dateFrom: Int, dateTo: Int): Int
}