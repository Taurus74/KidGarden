package com.tausoft.kidsgarden.data

import com.tausoft.kidsgarden.dao.DaysOffDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaysOffRepository @Inject constructor(private val daysOffDao: DaysOffDao) {

    fun getDaysOff(dateFrom: Int, dateTo: Int) = daysOffDao.getDaysOff(dateFrom, dateTo)

}