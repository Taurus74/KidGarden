package com.tausoft.kidsgarden.data

import android.os.Handler
import android.os.Looper
import com.tausoft.kidsgarden.dao.AbsenceDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class AbsencesLocalDataSource @Inject constructor(private val absenceDao: AbsenceDao)
    : AbsencesDataSource {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun addAbsence(absence: Absence) {
        executorService.execute {
            absenceDao.insert(absence)
        }
    }

    override fun getAbsences(
        dateFrom: Int, dateTo: Int, callback: (Map<Int, List<Absence>>) -> Unit
    ) {
        executorService.execute {
            val absences = absenceDao.getAbsences(dateFrom, dateTo)
            mainThreadHandler.post { callback(absences) }
        }
    }

    override fun getKidAbsences(
        kidId: Int, dateFrom: Int, dateTo: Int, callback: (List<Absence>) -> Unit) {
        executorService.execute {
            val absences = absenceDao.getKidAbsences(kidId, dateFrom, dateTo)
            mainThreadHandler.post { callback(absences) }
        }
    }

    override fun getSumKidAbsence(
        kidId: Int, absenceId: Int, absenceType: AbsenceType, dateFrom: Int, dateTo: Int,
        callback: (Int) -> Unit
    ) {
        executorService.execute {
            val days = absenceDao.getSumKidAbsence(kidId, absenceId, absenceType, dateFrom, dateTo)
            mainThreadHandler.post { callback(days) }
        }
    }

    override fun getSumAbsences(
        kidId: Int, dateFrom: Int, dateTo: Int, callback: (Map<AbsenceType, Int>) -> Unit
    ) {
        executorService.execute {
            val sumAbsences = absenceDao.getSumKidAbsences(kidId, dateFrom, dateTo)
            mainThreadHandler.post { callback(sumAbsences) }
        }
    }

    override fun getAbsence(kidId: Int, absenceId: Int, callback: (Absence) -> Unit) {
        executorService.execute {
            val absence = absenceDao.getKidAbsence(kidId, absenceId)
            mainThreadHandler.post { callback(absence!!) }
        }
    }

    override fun checkCrossing(
        kidId: Int, absenceId: Int, dateFrom: Int, dateTo: Int, callback: (Int) -> Unit) {
        executorService.execute {
            val result = absenceDao.checkCrossing(kidId, absenceId, dateFrom, dateTo)
            mainThreadHandler.post { callback(result) }
        }
    }

    override fun deleteAbsence(absence: Absence) {
        executorService.execute {
            absenceDao.delete(absence)
        }
    }

}