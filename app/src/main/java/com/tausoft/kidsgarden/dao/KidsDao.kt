package com.tausoft.kidsgarden.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.tausoft.kidsgarden.data.Kid

@Dao
interface KidsDao: BaseDao<Kid> {
    @Query("SELECT * FROM kids ORDER BY name")
    fun getAllKids(): LiveData<List<Kid>>

    @Query("SELECT * FROM kids WHERE id = :id")
    fun getKid(id: Int): Kid?

    @Query("DELETE FROM absences WHERE kid_id = :kidId")
    fun deleteKidAbsences(kidId: Int)

    @Transaction
    fun deleteKid(kid: Kid) {
        deleteKidAbsences(kid.id)
        delete(kid)
    }
}