package com.tausoft.kidsgarden.data

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun toAbsenceType(value: Int): AbsenceType = enumValues<AbsenceType>()[value]

    @TypeConverter
    fun fromAbsenceType(value: AbsenceType) = value.ordinal
}