package com.example.composeapptask.feature.dao.taskFeature

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taskEntityTable")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var taskTitle: String = "",
    var taskDescription: String = "",
    var taskPriority: String = "",
    var dueDate: String = "",
    var isCompleted: Boolean = false
)
