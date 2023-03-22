package com.tausoft.kidsgarden.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Периоды, когда ребенок числится в группе
@Entity(tableName = "in_group")
data class InGroup(
    // Ссылка на ребенка
    @ColumnInfo(name = "kid_id")
    var kidId: Int = 0,
    // Начало периода
    @ColumnInfo(name = "date_from")
    var date_from: Int = 0,
    // Окончание периода
    @ColumnInfo(name = "date_to")
    var date_to: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}