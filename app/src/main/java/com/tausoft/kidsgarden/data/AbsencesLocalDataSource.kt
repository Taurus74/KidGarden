package com.tausoft.kidsgarden.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
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

    override fun getAbsences(dateFrom: Int, dateTo: Int): LiveData<Map<Int, List<Absence>>> =
        absenceDao.getAbsences(dateFrom, dateTo)

    override fun getKidAbsences(kidId: Int, dateFrom: Int, dateTo: Int): LiveData<List<Absence>> =
        absenceDao.getKidAbsences(kidId, dateFrom, dateTo)

    override fun getSumKidAbsence(
        kidId: Int, absenceId: Int, absenceType: AbsenceType, dateFrom: Int, dateTo: Int,
        callback: (Int) -> Unit
    ) {
        executorService.execute {
            val days = absenceDao.getSumKidAbsence(kidId, absenceId, absenceType, dateFrom, dateTo)
            mainThreadHandler.post { callback(days) }
        }
    }

    override fun getSumKidAbsences(
        kidId: Int, dateFrom: Int, dateTo: Int, callback: (Map<AbsenceType, Int>) -> Unit
    ) {
        executorService.execute {
            val sumAbsences = absenceDao.getSumKidAbsences(kidId, dateFrom, dateTo)
            mainThreadHandler.post { callback(sumAbsences) }
        }
    }

    override fun getAbsence(absenceId: Int): LiveData<Absence> =
        absenceDao.getAbsence(absenceId)

    override fun checkCrossing(kidId: Int, absenceId: Int, dateFrom: Int, dateTo: Int) =
        absenceDao.checkCrossing(kidId, absenceId, dateFrom, dateTo)

    override fun deleteAbsence(absence: Absence) {
        executorService.execute {
            absenceDao.delete(absence)
        }
    }
}
