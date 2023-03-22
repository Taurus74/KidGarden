package com.tausoft.kidsgarden.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tausoft.kidsgarden.dao.AbsenceDao
import com.tausoft.kidsgarden.dao.DaysOffDao
import com.tausoft.kidsgarden.dao.KidsDao

@Database(
    entities = [Kid::class, Absence::class, InGroup::class, DayOff::class],
    version = 1, exportSchema = false)
abstract class KidsGardenDB : RoomDatabase() {

    abstract fun kidsDao(): KidsDao
    abstract fun absenceDao(): AbsenceDao
    abstract fun daysOffDao(): DaysOffDao

}