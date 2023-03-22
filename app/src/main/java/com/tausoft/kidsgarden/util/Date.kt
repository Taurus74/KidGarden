package com.tausoft.kidsgarden.util

class Date(
    mDay: Int = 1,
    mMonth: Int = 0,
    mYear: Int = 1
) {
    var day: Int = mDay
    // Параметр month (месяц) имеет значение 0..11 (как в календаре Android)
    var month: Int = mMonth
    var year: Int = mYear

    // Для вывода даты увеличиваем month на 1
    override fun toString() = String.format("%02d.%02d.%4d", day, month + 1, year)

    // Упаковать дату (день, месяц, год) в число по формату ГГГГММДД
    // Параметр month (месяц) имеет значение 0..11 (как в календаре Android)
    // При преобразовании к числу месяца добавляется 1
    fun toInt(mDay: Int = day, mMonth: Int = month, mYear: Int = year)
            = (mYear * 100 + mMonth + 1) * 100 + mDay

    // Распаковать дату из числа по формату ГГГГММДД в объект (день, месяц, год)
    // Параметр month (месяц) имеет значение 0..11 (как в календаре Android)
    // При преобразовании от числа месяца отнимается 1
    fun fromInt(date: Int): Date {
        year = date.div(10000)
        val v = date - year * 10000
        this.month = v.div(100)
        this.day = v - month * 100
        this.month--
        return this
    }

    // Уменьшить дату на 1 месяц
    fun decMonth(): Date {
        month--
        if (month < 0) {
            month = 11
            year--
        }
        return this
    }

    // Увеличить дату на 1 месяц
    fun incMonth(): Date {
        month++
        if (month > 11) {
            month = 0
            year++
        }
        return this
    }
}