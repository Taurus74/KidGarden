package com.tausoft.kidsgarden.data

import androidx.lifecycle.LiveData
import com.tausoft.kidsgarden.dao.KidsDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class KidsLocalDataSource @Inject constructor(private val kidsDao: KidsDao): KidsDataSource {

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)

    override fun addKid(kid: Kid) {
        executorService.execute {
            kidsDao.insert(kid)
        }
    }

    override fun getKid(id: Int): LiveData<Kid> = kidsDao.getKid(id)

    override fun getKids(): LiveData<List<Kid>> = kidsDao.getAllKids()

    override fun deleteKid(kid: Kid) {
        executorService.execute {
            kidsDao.deleteKid(kid)
        }
    }

}