package com.example.composeapptask.di

import android.content.Context
import androidx.room.Room
import com.example.composeapptask.data.localDatabaseConfig.AppDatabase
import com.example.composeapptask.feature.common.utils.LOCAL_DATABASE_NAME
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
}