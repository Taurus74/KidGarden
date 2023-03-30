package com.tausoft.kidsgarden.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.tausoft.kidsgarden.data.DayOff

@Dao
interface DaysOffDao: BaseDao<DayOff> {
    // Получить список выходных дней за заданный период, включая границы
    @Query("SELECT * FROM days_off WHERE day >= :dateFrom AND day <= :dateTo ORDER BY day")
    fun getDaysOff(dateFrom: Int, dateTo: Int): LiveData<List<DayOff>>

    // Получить количество выходных дней за заданный период, включая границы
    @Query("SELECT SUM(dayOff) FROM days_off WHERE day >= :dateFrom AND day <= :dateTo")
    fun getDaysOffCount(dateFrom: Int, dateTo: Int): Int
}