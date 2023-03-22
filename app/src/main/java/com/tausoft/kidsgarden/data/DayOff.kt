package com.tausoft.kidsgarden.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Список выходных
@Entity(tableName = "days_off", indices = [Index(value = ["day"])])
data class DayOff(
    // Дата - в формате ГГГГММДД
    @PrimaryKey(autoGenerate = false)
    val day: Int,
    // Признак, что день является выходным или праздничным( == 1),
    // или рабочим ( == 0).
    // По умолчанию в базе сохраняются только выходные и [пред-]праздничные дни,
    // поэтому предполагается, что значение dayOff == 1 всегда.
    val dayOff: Int = 1
)