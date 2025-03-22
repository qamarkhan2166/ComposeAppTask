package com.example.composeapptask.data.localDatabaseConfig

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composeapptask.feature.dao.taskFeature.TaskEntity
import com.example.composeapptask.feature.dao.taskFeature.TaskEntityDao

@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskEntityDao(): TaskEntityDao
}
