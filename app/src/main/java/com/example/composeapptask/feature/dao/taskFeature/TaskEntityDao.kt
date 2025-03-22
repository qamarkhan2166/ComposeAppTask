package com.example.composeapptask.feature.dao.taskFeature

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(entity: TaskEntity): Long

    @Query("SELECT * from taskEntityTable WHERE isCompleted = :isCompleted")
    fun getCompletedTasks(isCompleted: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM taskEntityTable WHERE id = :id")
    fun getTaskById(id: Int): Flow<TaskEntity?>

    @Query("DELETE FROM taskEntityTable WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Update
    suspend fun updateTask(entity: TaskEntity)

    @Query("SELECT * from taskEntityTable")
    fun getAllTasks(): Flow<List<TaskEntity>>
}