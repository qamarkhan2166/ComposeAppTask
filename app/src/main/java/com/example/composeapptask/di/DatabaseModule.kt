package com.example.composeapptask.di

import android.content.Context
import androidx.room.Room
import com.example.composeapptask.data.localDatabaseConfig.AppDatabase
import com.example.composeapptask.appFeatures.common.utils.LOCAL_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            LOCAL_DATABASE_NAME,
        ).fallbackToDestructiveMigration().enableMultiInstanceInvalidation().allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideTaskEntityDao(database: AppDatabase) = database.taskEntityDao()

    @Singleton
    @Provides
    fun provideMedicineReminderDao1(database: AppDatabase) = database.medicineReminderDao()

    @Singleton
    @Provides
    fun provideSessionDao(database: AppDatabase) = database.sessionDao()

    @Singleton
    @Provides
    fun provideActivityTransitionDao(database: AppDatabase) = database.activityTransitionDao()

    @Singleton
    @Provides
    fun provideSensorReadingDao(database: AppDatabase) = database.sensorReadingDao()

    @Singleton
    @Provides
    fun provideLocationPointDao(database: AppDatabase) = database.locationPointDao()
}