package com.tausoft.kidsgarden.di

import com.tausoft.kidsgarden.data.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class DatabaseKids

@InstallIn(SingletonComponent::class)
@Module
abstract class KidsDatabaseModule {
    @DatabaseKids
    @Singleton
    @Binds
    abstract fun bindDatabaseKids(impl: KidsLocalDataSource): KidsDataSource
}

@Qualifier
annotation class DatabaseAbsences

@InstallIn(SingletonComponent::class)
@Module
abstract class AbsencesDatabaseModule {
    @DatabaseAbsences
    @Singleton
    @Binds
    abstract fun bindDatabaseAbsences(impl: AbsencesLocalDataSource): AbsencesDataSource
}

@Qualifier
annotation class DatabaseDaysOff

@InstallIn(SingletonComponent::class)
@Module
abstract class DaysOffDatabaseModule {
    @DatabaseDaysOff
    @Singleton
    @Binds
    abstract fun bindDatabaseDaysOff(impl: DaysOffLocalDataSource): DaysOffDataSource
}