package com.example.composeapptask.feature.taskify.taskDetails

import com.example.composeapptask.feature.dao.taskFeature.TaskEntity

internal data class TaskDetailsUiState(
    val taskDetails: TaskDetailsContractor ? = null,
    val isLoading: Boolean = false
)

internal data class TaskDetailsContractor(
    val taskID: Int,
    val title: String,
    val description: String,
    val priority: String,
    val dueDate: String,
    val isCompleted: Boolean
)

internal fun TaskEntity.toTaskDetailsContractor(isCompleted: Boolean?): TaskDetailsContractor {
    return TaskDetailsContractor(
        taskID = this.id,
        title = this.taskTitle,
        description = this.taskDescription,
        priority = this.taskPriority,
        dueDate = this.dueDate,
        isCompleted = isCompleted ?: this.isCompleted
    )
}
