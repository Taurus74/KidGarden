package com.tausoft.kidsgarden.data

import androidx.room.*

// Периоды отсутствия
@Entity(tableName = "absences")
data class Absence(
    // Ссылка на ребенка
    @ColumnInfo(name = "kid_id")
    var kidId: Int = 0,
    // Начало периода
    @ColumnInfo(name = "date_from")
    var dateFrom: Int = 0,
    // Окончание периода
    @ColumnInfo(name = "date_to")
    var dateTo: Int = 0,
    // Тип отсутствия
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "absence_type")
    var absenceType: AbsenceType = AbsenceType.ABSENCE,
    // Количество дней отсутствия.
    // Предназначено для вывода на экране и рассчитывается динамически
    // в зависимости от типа отсутствия и периода
    @ColumnInfo(name = "num_of_days")
    var numOfDays: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

enum class AbsenceType {
    ABSENCE    { override fun toString() = "Отсутствие Н/Н (5 д.)" },
    VACATION   { override fun toString() = "Отпуск (56 д.)" },
    COSTED     { override fun toString() = "За свой счет Н/О" },
    SICK_LEAVE { override fun toString() = "Больничный Б/Л" }
}