package com.tausoft.kidsgarden.data

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.tausoft.kidsgarden.dao.KidsDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class KidsLocalDataSource @Inject constructor(private val kidsDao: KidsDao): KidsDataSource {

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun addKid(kid: Kid) {
        executorService.execute {
            kidsDao.insert(kid)
        }
    }

    override fun getKid(id: Int, callback: (Kid) -> Unit) {
        executorService.execute {
            val kid = kidsDao.getKid(id)
            mainThreadHandler.post {
                if (kid != null) {
                    callback(kid)
                }
            }
        }
    }

    override fun getKids(): LiveData<List<Kid>> = kidsDao.getAllKids()

    override fun deleteKid(kid: Kid) {
        executorService.execute {
            kidsDao.deleteKid(kid)
        }
    }

}