package com.tausoft.kidsgarden.data

import androidx.lifecycle.LiveData

interface KidsDataSource {
    fun addKid(kid: Kid)
    fun getKid(id: Int, callback: (Kid) -> Unit)
    fun getKids(): LiveData<List<Kid>>
    fun deleteKid(kid: Kid)
}