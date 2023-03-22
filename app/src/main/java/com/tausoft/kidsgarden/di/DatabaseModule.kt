package com.tausoft.kidsgarden.di

import android.content.Context
import androidx.room.Room
import com.tausoft.kidsgarden.dao.AbsenceDao
import com.tausoft.kidsgarden.dao.DaysOffDao
import com.tausoft.kidsgarden.dao.KidsDao
import com.tausoft.kidsgarden.data.KidsGardenDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): KidsGardenDB {
        return Room.databaseBuilder(
            appContext,
            KidsGardenDB::class.java,
            "kidsGarden.db"
        ).build()
    }

    @Provides
    fun provideKidsDao(database: KidsGardenDB): KidsDao {
        return database.kidsDao()
    }

    @Provides
    fun provideAbsenceDao(database: KidsGardenDB): AbsenceDao {
        return database.absenceDao()
    }

    @Provides
    fun provideDaysOffDao(database: KidsGardenDB): DaysOffDao {
        return database.daysOffDao()
    }
}