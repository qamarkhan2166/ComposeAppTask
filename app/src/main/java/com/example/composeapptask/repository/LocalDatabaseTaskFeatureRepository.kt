package com.example.composeapptask.repository

import com.example.composeapptask.feature.dao.taskFeature.TaskEntity
import com.example.composeapptask.feature.dao.taskFeature.TaskEntityDao
import kotlinx.coroutines.flow.Flow

class LocalDatabaseTaskFeatureRepository(private val taskEntityDao: TaskEntityDao,) {

    suspend fun insertTask(entity: TaskEntity): Long {
        return taskEntityDao.insertTask(entity = entity)
    }

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskEntityDao.getAllTasks()
    }

    fun getCompletedTasks(isCompleted: Boolean): Flow<List<TaskEntity>> {
        return taskEntityDao.getCompletedTasks(isCompleted = isCompleted)
    }

    fun getTaskById(id: Int): Flow<TaskEntity?> {
        return taskEntityDao.getTaskById(id)
    }

    suspend fun deleteTaskById(id: Int) {
        return taskEntityDao.deleteTaskById(id)
    }

    suspend fun updateTask(value: TaskEntity) {
        return taskEntityDao.updateTask(value)
    }
}
