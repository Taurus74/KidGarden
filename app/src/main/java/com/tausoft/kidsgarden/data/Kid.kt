package com.tausoft.kidsgarden.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Дети
@Entity(tableName = "kids")
data class Kid(
    // Имя ребенка
    var name: String = "",
    // Дата рождения (формат ГГГГММДД)
    var birthday: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString() = name
}